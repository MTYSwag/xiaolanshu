package com.smart.content.domain.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 发送到 Kafka 的数据结构
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteEventDTO {

    @Schema(description = "笔记ID")
    private Long noteId;

    @Schema(description = "笔记发布者的用户id")
    private Long userId;

    @Schema(description = "笔记标题")
    private String title;

    @Schema(description = "笔记内容")
    private String content;

    @Schema(description = "所发布笔记的图片列表")
    private List<String> imageUrls;

    @Schema(description = "笔记所属话题列表")
    private List<String> topics;

    @Schema(description = "笔记发布（创建）时间")
    private LocalDateTime createTime;
}
