package com.smart.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.community.common.core.constants.user.UserConstants;
import com.smart.community.common.core.constants.user.UserFollowConstants;
import com.smart.community.common.core.enums.user.UserRoleEnum;
import com.smart.community.common.core.enums.user.UserStatusEnum;
import com.smart.community.common.core.exception.BusinessException;
import com.smart.community.common.security.utils.JwtUtils;
import com.smart.user.config.context.UserContext;
import com.smart.user.domain.dto.LoginDTO;
import com.smart.user.domain.dto.RegisterDTO;
import com.smart.user.domain.vo.CurrentUserInfoVO;
import com.smart.user.domain.vo.UserFollowVO;
import com.smart.user.domain.vo.UserProfileVO;
import com.smart.user.entity.User;
import com.smart.user.entity.UserFollow;
import com.smart.user.mapper.UserFollowMapper;
import com.smart.user.mapper.UserMapper;
import com.smart.user.service.UserService;
import com.smart.user.config.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final JwtUtils jwtUtils;
    private final UserFollowMapper userFollowMapper;

    // ==================== 用户相关 ====================

    @Override
    public void register(RegisterDTO registerDTO) {
        String username = registerDTO.getUsername();
        String phoneNumber = registerDTO.getPhoneNumber();

        User user = new User();
        if (StrUtil.isNotBlank(username)) {
            if (this.count(new LambdaQueryWrapper<User>().eq(User::getUsername, username)) > 0) {
                throw new BusinessException(UserConstants.USERNAME_EXISTS);
            }
            user = registerUserWithUsername(registerDTO);
        }
        if (StrUtil.isNotBlank(phoneNumber)) {
            if (this.count(new LambdaQueryWrapper<User>().eq(User::getPhoneNumber, phoneNumber)) > 0) {
                throw new BusinessException(UserConstants.PHONE_EXISTS);
            }
            user = registerUserWithPhone(registerDTO);
        }
        this.save(user);
    }

    private User registerUserWithUsername(RegisterDTO registerDTO) {
        String salt = PasswordUtil.generateSalt();
        User user = new User();
        BeanUtil.copyProperties(registerDTO, user);
        return user
                .setPassword(PasswordUtil.encrypt(registerDTO.getPassword(), salt))
                .setSalt(salt)
                .setCreateTime(LocalDateTime.now())
                .setStatus(UserStatusEnum.NORMAL.getCode())
                .setRole(UserRoleEnum.NORMAL.getCode());
    }

    private User registerUserWithPhone(RegisterDTO registerDTO) {
        String salt = PasswordUtil.generateSalt();
        User user = new User();
        BeanUtil.copyProperties(registerDTO, user);
        return user
                .setUsername(UserConstants.DEFAULT_USERNAME_PREFIX + RandomUtil.randomString(9))
                .setPassword(PasswordUtil.encrypt(registerDTO.getPassword(), salt))
                .setSalt(salt)
                .setCreateTime(LocalDateTime.now())
                .setStatus(UserStatusEnum.NORMAL.getCode())
                .setRole(UserRoleEnum.NORMAL.getCode());
    }

    @Override
    public Map<String, String> login(LoginDTO loginDTO) {
        Map<String, String> loginResult = new HashMap<>();

        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        User user = this.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (Objects.isNull(user)) {
            throw new BusinessException(UserConstants.USER_NOT_FOUND);
        }
        String encrypted = PasswordUtil.encrypt(password, user.getSalt());
        if (!encrypted.equals(user.getPassword())) {
            throw new BusinessException(UserConstants.PASSWORD_ERROR);
        }
        String token = jwtUtils.generateToken(String.valueOf(user.getId()));

        loginResult.put(UserConstants.TOKEN, token);
        loginResult.put(UserConstants.USER_ID, user.getId().toString());
        return loginResult;
    }

    @Override
    public CurrentUserInfoVO getCurrentUser() {
        Long userId = UserContext.getUserId();
        User user = this.getById(userId);
        CurrentUserInfoVO userInfoVO = new CurrentUserInfoVO();
        BeanUtil.copyProperties(user, userInfoVO);
        return userInfoVO;
    }

    @Override
    public UserProfileVO getUserProfile(Long userId) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(UserConstants.USER_NOT_FOUND);
        }
        long followCount = this.countFollow(userId);
        long fansCount = this.countFans(userId);
        Long currentUserId = UserContext.getUserId();
        Boolean isFollowed = (currentUserId != null)
                ? this.isFollowed(currentUserId, userId)
                : null;

        CurrentUserInfoVO currentUserInfoVO = new CurrentUserInfoVO();
        BeanUtil.copyProperties(user, currentUserInfoVO);
        return UserProfileVO.builder()
                .currentUserInfoVO(currentUserInfoVO)
                .followCount(followCount)
                .fansCount(fansCount)
                .isFollowed(isFollowed)
                .build();
    }

    @Override
    public List<CurrentUserInfoVO> getFansInfoByIds(List<Long> fanIds) {
        if (CollUtil.isEmpty(fanIds)) {
            return Collections.emptyList();
        }

        return this.listByIds(fanIds)
                .stream()
                .map(userInfo -> BeanUtil.copyProperties(userInfo, CurrentUserInfoVO.class))
                .collect(Collectors.toList());
    }

    // ==================== 关注关系相关 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void follow(Long userId, Long followUserId) {
        if (userId.equals(followUserId)) {
            throw new BusinessException(UserFollowConstants.CANNOT_FOLLOW_SELF);
        }
        UserFollow follow = new UserFollow();
        follow.setUserId(userId);
        follow.setFollowUserId(followUserId);
        follow.setCreateTime(LocalDateTime.now());
        try {
            userFollowMapper.insert(follow);
        } catch (Exception e) {
            throw new BusinessException(UserFollowConstants.ALREADY_FOLLOWED);
        }
    }

    @Override
    @Transactional
    public void unfollow(Long userId, Long followUserId) {
        boolean removed = userFollowMapper.delete(new LambdaQueryWrapper<UserFollow>()
                .eq(UserFollow::getUserId, userId)
                .eq(UserFollow::getFollowUserId, followUserId)) > 0;
        if (!removed) {
            throw new BusinessException(UserFollowConstants.NOT_FOLLOWED);
        }
    }

    @Override
    public long countFollow(Long userId) {
        return userFollowMapper.selectCount(new LambdaQueryWrapper<UserFollow>().eq(UserFollow::getUserId, userId));
    }

    @Override
    public long countFans(Long userId) {
        return userFollowMapper.selectCount(new LambdaQueryWrapper<UserFollow>().eq(UserFollow::getFollowUserId, userId));
    }

    @Override
    public boolean isFollowed(Long userId, Long targetUserId) {
        if (userId == null || targetUserId == null) {
            return false;
        }
        return userFollowMapper.selectCount(new LambdaQueryWrapper<UserFollow>()
                .eq(UserFollow::getUserId, userId)
                .eq(UserFollow::getFollowUserId, targetUserId)) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IPage<UserFollowVO> getFansPage(Long userId, int pageNum, int pageSize) {

        Page<UserFollow> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<UserFollow>()
                .eq(UserFollow::getFollowUserId, userId)
                .orderByDesc(UserFollow::getCreateTime);

        IPage<UserFollow> followPage = userFollowMapper.selectPage(page, wrapper);

        List<Long> fanIds = followPage.getRecords().stream()
                .map(UserFollow::getUserId)
                .collect(Collectors.toList());

        Map<Long, CurrentUserInfoVO> fansInfoMap = this.getFansInfoByIds(fanIds).stream()
                .collect(Collectors.toMap(CurrentUserInfoVO::getId, o -> o));

        return followPage.convert(follow -> UserFollowVO.builder()
                .currentUserInfoVO(fansInfoMap.get(follow.getUserId()))
                .followTime(follow.getCreateTime())
                .build());
    }
}