package com.smart.community.common.core.enums.user;

import lombok.Getter;
import lombok.Setter;

/**
 * 用户角色枚举
 */
@Getter
public enum UserRoleEnum {
    /**
     * 管理员
     */
    ADMIN(0, "管理员"),
    /**
     * 普通用户
     */
    NORMAL(1, "普通用户");

    private final int code;
    private final String desc;

    UserRoleEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
