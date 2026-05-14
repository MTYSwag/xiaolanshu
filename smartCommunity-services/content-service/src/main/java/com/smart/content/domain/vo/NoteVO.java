package com.smart.content.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 笔记VO
 */
@Data
public class NoteVO {
    /**
     * 笔记ID
     */
    private Long id;
    /**
     * 笔记标题
     */
    private String title;
    /**
     * 笔记内容
     */
    private String content;
    /**
     * 用户昵称
     */
    private String userName;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
