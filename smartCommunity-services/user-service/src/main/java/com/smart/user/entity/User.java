package com.smart.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 用户实体类USER表
 */

@Data
@TableName("user")
@Schema(description = "用户实体类USER表")
@Accessors(chain = true)
public class User {
    /**
     * 用户ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码(加盐哈希)
     */
    private String password;
    /**
     * 盐值
     */
    private String salt;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 头像URL
     */
    private String avatar;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    /**
     * 手机号
     */
    private String phoneNumber;
    /**
     * 生日
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    /**
     * 简介
     */
    private String introduction;
    /**
     * 状态(0:禁用,1:正常)
     */
    private Integer status;
    /**
     * 性别(0:女,1:男、00：未知)
     */
    private Integer gender;
    /**
     * 角色(0:管理员,1:普通用户)
     */
    private Integer role;
}
