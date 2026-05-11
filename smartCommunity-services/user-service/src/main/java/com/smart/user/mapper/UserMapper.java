package com.smart.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.user.entity.User;

import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper层
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
