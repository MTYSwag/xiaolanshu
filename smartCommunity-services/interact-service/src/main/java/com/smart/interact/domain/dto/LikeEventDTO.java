package com.smart.interact.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 点赞事件DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class LikeEventDTO {

    @Schema(description = "点赞用户ID")
    private Long userId;

    @Schema(description = "点赞笔记ID")
    private Long contentId;

    @Schema(description = "点赞状态(1 点赞 0 取消点赞)")
    private Integer status;

    @Schema(description = "点赞时间")
    private LocalDateTime timestamp;
}
