package com.smart.community.common.core.enums.user;

import lombok.Getter;

/**
 * 用户状态枚举
 */
@Getter
public enum UserStatusEnum {
    /**
     * 正常
     */
    NORMAL(1, "正常"),

    /**
     * 禁用
     */
    DISABLED(0, "禁用");

    private final int code;
    private final String desc;

    UserStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
