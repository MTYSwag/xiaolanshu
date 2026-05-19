package com.smart.file.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_file")
public class TFile {

    @Schema(description = "文件ID")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "原始文件名")
    private String originalName;

    @Schema(description = "对象文件名,MinIO 中的对象名（UUID+后缀）")
    private String objectName;

    @Schema(description = "文件URL")
    private String fileUrl;

    @Schema(description = "文件大小(字节)")
    private Long fileSize;

    @Schema(description = "文件类型")
    private String contentType;

    @Schema(description = "文件MD5哈希值")
    private String fileHash;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}