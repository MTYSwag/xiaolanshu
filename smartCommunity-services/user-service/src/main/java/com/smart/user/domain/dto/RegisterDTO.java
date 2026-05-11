package com.smart.user.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 用户注册DTO
 */
@Data
public class RegisterDTO {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码(明文)")
    private String password;

    @Schema(description = "手机号")
    private String phoneNumber;

    @Schema(description = "生日")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @Schema(description = "性别(0:女,1:男、00：未知)")
    private Integer gender;
}
