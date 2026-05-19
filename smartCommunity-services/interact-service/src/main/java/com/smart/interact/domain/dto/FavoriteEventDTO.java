package com.smart.interact.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 收藏事件DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteEventDTO {
    @Schema(description = "用户id'")
    private Long userId;

    @Schema(description = "笔记id")
    private Long noteId;

    @Schema(description = "收藏状态（1收藏 0取消收藏）")
    private Integer status;

    @Schema(description = "事件触发时间")
    private LocalDateTime timestamp;
}