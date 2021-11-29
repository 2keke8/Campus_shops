package com.config;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;


/**
 * @Description: 服务器错误处理
 * @BelongsProject: shops
 * @BelongsPackage: com.config
 * @Author: KeYu-Zhao
 * @CreateTime: 2021-11-11 18:15
 * @Email: 2540560264@qq.com
 * @Version: 1.0
 */
public class MyErrorConfig implements ErrorPageRegistrar{
    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {
        ErrorPage error400Page=new ErrorPage(HttpStatus.BAD_REQUEST,"/error/400" );
        ErrorPage error401Page=new ErrorPage(HttpStatus.UNAUTHORIZED,"/error/401");
        ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/error/404");
        ErrorPage error500Page=new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR,"/error500");
        registry.addErrorPages(error400Page,error401Page,error500Page,error404Page);
    }
}
