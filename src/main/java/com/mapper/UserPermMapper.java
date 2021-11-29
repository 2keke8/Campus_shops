package com.mapper;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserPermMapper {
    /**查询用户的权限*/
    List<String> LookPermsByUserid(Integer roleid);
}
