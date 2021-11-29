package com.service;

import com.mapper.UserPermMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserPermService {
    @Autowired
    private UserPermMapper userPermMapper;

    //查询用户的权限
    public List<String> LookPermsByUserid(Integer id){
        return userPermMapper.LookPermsByUserid(id);
    }
}
