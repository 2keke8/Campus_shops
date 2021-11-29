package com.controller.User;

import com.entity.Login;
import com.entity.UserInfo;
import com.service.LoginService;
import com.service.UserInfoService;
import com.util.*;
import com.vo.ResultVo;
import net.sf.json.JSONObject;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Controller
public class UserController {
    @Autowired
    private LoginService loginService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    JavaMailSender javaMailSender;

    /**手机号和更换手机号验证码map集合*/
    private static Map<String, String> phonecodemap = new HashMap<>();
    /**
     * 修改密码
     * 1.前端传入旧密码（oldpwd）、新密码（newpwd）
     * 2.判断输入旧密码和系统旧密码是否相等
     * 4.修改密码
     */
    @ResponseBody
    @PutMapping("/user/updatepwd")
    public ResultVo updatepwd(HttpSession session, HttpServletRequest request) throws IOException {
        JSONObject json = JsonReader.receivePost(request);
        String oldpwd = json.getString("oldpwd");
        String newpwd = json.getString("newpwd");
        String userid = (String) session.getAttribute("userid");
        Login login = new Login();
        UserInfo userInfo = new UserInfo();
        login.setUserid(userid);
        Login login1 = loginService.userLogin(login);
        String oldpwds = new Md5Hash(oldpwd, "Campus-shops").toString();
        //如果旧密码相等
        if (oldpwds.equals(login1.getPassword())){
            //盐加密
            String passwords = new Md5Hash(newpwd, "Campus-shops").toString();
            login.setPassword(passwords);
            userInfo.setPassword(passwords).setUserid(login1.getUserid());
            Integer integer = loginService.updateLogin(login);
            Integer integer1 = userInfoService.UpdateUserInfo(userInfo);
            if (integer == 1 && integer1 == 1) {
                return new ResultVo(true, StatusCode.OK, "修改密码成功");
            }
            return new ResultVo(false, StatusCode.ERROR, "修改密码失败");
        }
        return new ResultVo(false, StatusCode.LOGINERROR, "当前密码错误");
    }

    /**
     * 发送邮件
     * @param session
     * @return
     */
    @ResponseBody
    @RequestMapping("/user/resetpwdemail")
    public ResultVo resetpwdemail( HttpSession session, @RequestBody Login login) throws IOException {
        String email=login.getEmail();
        String mobilephone=login.getMobilephone();
        Login login1 = new Login();
        if (!JustPhone.justPhone(mobilephone)) {//判断输入的手机号格式是否正确
            return new ResultVo(false,StatusCode.ERROR,"请输入正确格式的手机号");
        }
        //查询手机号和邮箱是否存在
        login1.setMobilephone(mobilephone);
        login1.setEmail(email);
        Login userIsExist = loginService.userLogin(login1);
        if (StringUtils.isEmpty(userIsExist)){//用户账号不存在
            return new ResultVo(false, StatusCode.ERROR,"该账号不存在，注意你的账号和邮箱必须一致哦~");
        }
        SimpleMailMessage msg = new SimpleMailMessage();    //构建一个邮件对象
        msg.setSubject("川师校园二手商城"); // 设置邮件主题
        msg.setFrom("2540560264@qq.com"); // 设置邮箱发送者
        msg.setTo(login.getEmail()); // 设置邮件接收者，可以有多个接收者
        msg.setSentDate(new Date());    // 设置邮件发送日期
        int ran = 0;
        for(int j = 0; j< 100; j++){
            ran = (int)((Math.random()*9+1)*100000);
            if(ran>100000) break;
        }
        msg.setText("这是当前的验证码哦~ 看清楚哦~:"+ ran +"   --管理员阿科");   // 设置邮件的正文
        session.setAttribute("mail",ran);
        javaMailSender.send(msg);
        return new ResultVo(true, StatusCode.OK, "发送邮件成功，请根据邮件输入验证码哦~");
    }

    /**
     * 展示用户头像昵称
     */
    @ResponseBody
    @PostMapping("/user/avatar")
    public ResultVo userAvatar( HttpSession session) {
        String userid = (String) session.getAttribute("userid");
        UserInfo userInfo = userInfoService.queryPartInfo(userid);
        return new ResultVo(true, StatusCode.OK, "查询头像成功",userInfo);
    }

    /**
     * 修改头像
     * */
    @PostMapping(value = "/user/updateuimg")
    @ResponseBody
    public JSONObject updateuimg(@RequestParam(value = "file", required = false) MultipartFile file, HttpSession session) throws IOException {
        JSONObject res = new JSONObject();
        JSONObject resUrl = new JSONObject();
        String filename = UUID.randomUUID().toString().replaceAll("-", "");
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());//获得文件扩展名
        String filenames = filename + "." + ext;//文件全名
        String pathname = "/campusshops/file/" + filenames;
        file.transferTo(new File(pathname));
        resUrl.put("src", "/pic/"+filenames);
        res.put("msg", "");
        res.put("code", 0);
        res.put("data", resUrl);
        String uimgUrl = "/pic/" + filenames;

        String userid=(String) session.getAttribute("userid");
        UserInfo userInfo = new UserInfo().setUserid(userid).setUimage(uimgUrl);
        userInfoService.UpdateUserInfo(userInfo);
        return res;
    }

    /**
     * 展示个人信息
     */
    @RequiresPermissions("user:userinfo")
    @GetMapping("/user/lookinfo")
    public String lookinfo(HttpSession session, ModelMap modelMap) {
        String userid = (String) session.getAttribute("userid");
        UserInfo userInfo = userInfoService.LookUserinfo(userid);
        modelMap.put("userInfo",userInfo);
        return "user/userinfo";
    }

    /**
     * 跳转到完善个人信息
     */
    @RequiresPermissions("user:userinfo")
    @GetMapping("/user/perfectinfo")
    public String perfectInfo(HttpSession session, ModelMap modelMap) {
        String userid = (String) session.getAttribute("userid");
        UserInfo userInfo = userInfoService.LookUserinfo(userid);
        modelMap.put("perfectInfo",userInfo);
        return "user/perfectinfo";
    }

    /**
     * 修改个人信息
     * 1.前端传入用户昵称（username）、用户邮箱（email）、性别（sex）、学校（school）、院系（faculty）、入学时间（startime）
     * 2.前端传入变更后的字段，未变更的不传入后台
     * 3.判断更改的用户名是否已存在
     * 4.修改个人信息
     */
    @ResponseBody
    @PostMapping("/user/updateinfo")
    public ResultVo updateInfo(@RequestBody UserInfo userInfo, HttpSession session) {
        String username = userInfo.getUsername();
        String userid = (String) session.getAttribute("userid");
        Login login = new Login();
        //如果传入用户名
        if (!StringUtils.isEmpty(username)){
            login.setUsername(username);
            Login login1 = loginService.userLogin(login);
            //如果该用户名对应有用户
            if (!StringUtils.isEmpty(login1)){
                return new ResultVo(false, StatusCode.ERROR, "该用户名已存在");
            }
            login.setUserid(userid);
            //修改登录表中用户名
            loginService.updateLogin(login);
        }
        userInfo.setUserid(userid);
        Integer integer1 = userInfoService.UpdateUserInfo(userInfo);
        if (integer1 == 1) {
            return new ResultVo(true, StatusCode.OK, "修改成功");
        }
        return new ResultVo(false, StatusCode.ERROR, "修改失败");
    }


    /**
     * 修改绑定手机号
     * 1.获取session中userid
     * 2.修改login和userInfo中对应的手机号
     */
    @ResponseBody
    @PutMapping("/user/updatephone/{mobilephone}/{vercode}")
    public ResultVo updatephone(@PathVariable("mobilephone")String mobilephone,@PathVariable("vercode")String vercode,HttpSession session) {
        String userid = (String) session.getAttribute("userid");
        String rel = phonecodemap.get(mobilephone);

        Login login = new Login().setUserid(userid).setMobilephone(mobilephone);
        UserInfo userInfo = new UserInfo().setUserid(userid).setMobilephone(mobilephone);
        Integer integer = loginService.updateLogin(login);
        Integer integer1 = userInfoService.UpdateUserInfo(userInfo);
        if (integer == 1 && integer1 == 1) {
            return new ResultVo(true, StatusCode.OK, "更换手机号成功");
        }
        return new ResultVo(false, StatusCode.SERVERERROR, "系统错误，更换失败");
    }


}

