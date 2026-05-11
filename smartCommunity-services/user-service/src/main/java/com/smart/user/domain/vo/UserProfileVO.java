package com.smart.user.domain.vo;

import com.smart.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户个人主页VO
 */
@Data
@Builder
public class UserProfileVO {

    @Schema(description = "用户信息")
    private CurrentUserInfoVO currentUserInfoVO;

    @Schema(description = "关注数量")
    private Long followCount;

    @Schema(description = "粉丝数量")
    private Long fansCount;

    @Schema(description = "是否关注")
    private Boolean isFollowed;

}
