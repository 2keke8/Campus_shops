package com.mapper;

import com.entity.UserRole;
import org.springframework.stereotype.Component;

@Component
public interface UserRoleMapper {
    /**插入角色*/
    Integer InsertUserRole(UserRole userRole);
    /**查询角色id*/
    Integer LookUserRoleId(String userid);
    /**修改用户的角色*/
    void UpdateUserRole(UserRole userRole);
}
