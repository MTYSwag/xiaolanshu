package com.smart.user.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * USER_FOLLOW表实体类
 */

@Data
@TableName("user_follow")
public class UserFollow {
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 关注者用户ID(粉丝)
     */
    private Long userId;
    /**
     * 被关注用户ID(博主)
     */
    private Long followUserId;
    /**
     * 关注时间
     * 用于记录用户关注时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
