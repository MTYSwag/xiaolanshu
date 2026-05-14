package com.smart.content.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 包含完整信息的VO类
 */
@Data
public class NoteDetailVO {

    @Schema(description = "笔记ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户昵称")
    private String userName;

    @Schema(description = "笔记标题")
    private String title;

    @Schema(description = "笔记内容")
    private String content;

    @Schema(description = "笔记图片URL列表")
    private List<String> imageUrls;

    @Schema(description = "笔记标签列表")
    private List<String> topics;

    @Schema(description = "是否删除(0:未删除, 1:已删除)")
    private Integer isDeleted;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
