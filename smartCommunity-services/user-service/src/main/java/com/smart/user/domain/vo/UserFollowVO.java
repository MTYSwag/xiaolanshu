package com.smart.user.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserFollowVO {

    /**
     * 当前用户信息VO
     */
    @Schema(description = "当前用户信息VO")
    private CurrentUserInfoVO currentUserInfoVO;
    /**
     * 关注时间
     */
    @Schema(description = "关注时间")
    private LocalDateTime followTime;
}
