package com.smart.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.smart.user.domain.dto.LoginDTO;
import com.smart.user.domain.dto.RegisterDTO;
import com.smart.user.domain.vo.CurrentUserInfoVO;
import com.smart.user.domain.vo.UserFollowVO;
import com.smart.user.domain.vo.UserProfileVO;

import java.util.List;
import java.util.Map;

/**
 * 用户业务接口（用户模块聚合根，统一管理用户与关注关系）
 */

public interface UserService {

    // ==================== 用户相关 ====================

    /**
     * 用户注册
     * @param registerDTO 注册DTO
     */
    void register(RegisterDTO registerDTO);

    /**
     * 用户登录
     * @param loginDTO 登录DTO
     * @return 登录成功（包含token）
     */
    Map<String, String> login(LoginDTO loginDTO);

    /**
     * 获取当前登录用户信息
     * @return 当前登录用户信息VO
     */
    CurrentUserInfoVO getCurrentUser();

    /**
     * 获取用户个人信息
     * @param userId 用户ID
     * @return 用户个人信息VO
     */
    UserProfileVO getUserProfile(Long userId);

    /**
     * 获取用户粉丝信息
     * @param fanIds 粉丝ID列表
     * @return 粉丝信息VO列表
     */
    List<CurrentUserInfoVO> getFansInfoByIds(List<Long> fanIds);

    // ==================== 关注关系相关 ====================

    /**
     * 关注用户
     * @param userId 用户ID
     * @param followUserId 关注用户ID
     */
    void follow(Long userId, Long followUserId);

    /**
     * 取消关注用户
     * @param userId 用户ID
     * @param followUserId 关注用户ID
     */
    void unfollow(Long userId, Long followUserId);

    /**
     * 获取用户关注数量
     * @param userId 用户ID
     * @return 关注数量
     */
    long countFollow(Long userId);

    /**
     * 获取用户粉丝数量
     * @param userId 用户ID
     * @return 粉丝数量
     */
    long countFans(Long userId);

    /**
     * 判断用户是否关注目标用户
     * @param userId 用户ID
     * @param targetUserId 目标用户ID
     * @return 是否关注目标用户
     */
    boolean isFollowed(Long userId, Long targetUserId);

    /**
     * 获取用户粉丝列表
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 粉丝列表VO
     */
    IPage<UserFollowVO> getFansPage(Long userId, int pageNum, int pageSize);
}
