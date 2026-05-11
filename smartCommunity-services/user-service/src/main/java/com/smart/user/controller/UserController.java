package com.smart.user.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.smart.community.common.core.result.Result;
import com.smart.user.config.context.UserContext;
import com.smart.user.domain.dto.LoginDTO;
import com.smart.user.domain.dto.RegisterDTO;
import com.smart.user.domain.vo.CurrentUserInfoVO;
import com.smart.user.domain.vo.UserFollowVO;
import com.smart.user.domain.vo.UserProfileVO;
import com.smart.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "用户模块")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "使用用户名和密码注册新用户")
    public Result<Void> register(@RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return Result.success();
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "使用用户名和密码登录系统，返回登录成功后的token（JWT令牌）和用户信息")
    public Result<Map<String, String>> login(@RequestBody LoginDTO loginDTO) {
        return Result.success(userService.login(loginDTO));
    }

    @GetMapping("/getCurrentUser")
    @Operation(summary = "获取当前登录用户信息")
    public Result<CurrentUserInfoVO> getCurrentUser(){
        return Result.success(userService.getCurrentUser());
    }

    @GetMapping("/follow/{userId}")
    @Operation(summary = "关注用户")
    public Result<Void> follow(@PathVariable Long userId) {
        userService.follow(UserContext.getUserId(), userId);
        return Result.success();
    }

    @GetMapping("/unfollow/{userId}")
    @Operation(summary = "取消关注用户")
    public Result<Void> unfollow(@PathVariable Long userId) {
        userService.unfollow(UserContext.getUserId(), userId);
        return Result.success();
    }

    @GetMapping("/profile/{userId}")
    @Operation(summary = "获取用户个人主页")
    public Result<UserProfileVO> profile(@PathVariable Long userId) {
        return Result.success(userService.getUserProfile(userId));
    }

    @GetMapping("/fans")
    @Operation(summary = "粉丝列表分页")
    public Result<IPage<UserFollowVO>> fans(@RequestParam Long userId,
                                            @RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        return Result.success(userService.getFansPage(userId, page, size));
    }
}