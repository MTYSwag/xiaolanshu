package com.smart.user.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户登录DTO
 */

@Data
public class LoginDTO {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "手机号")
    private String phoneNumber;

    @Schema(description = "密码(明文)")
    private String password;



}
