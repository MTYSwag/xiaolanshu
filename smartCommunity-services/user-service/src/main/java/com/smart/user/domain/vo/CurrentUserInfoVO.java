package com.smart.user.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 当前登录用户信息VO
 */
@Data
public class CurrentUserInfoVO {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "手机号")
    private String phoneNumber;

    @Schema(description = "生日")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @Schema(description = "简介")
    private String introduction;

    @Schema(description = "状态(0:禁用,1:正常)")
    private Integer status;

    @Schema(description = "性别(0:女,1:男、00：未知)")
    private Integer gender;

    @Schema(description = "角色(0:管理员,1:普通用户)")
    private Integer role;
}
