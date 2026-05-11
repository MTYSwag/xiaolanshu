package com.smart.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.user.entity.UserFollow;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户关注表Mapper接口
 * 用于操作用户关注表的数据库操作
 */
@Mapper
public interface UserFollowMapper extends BaseMapper<UserFollow> {
}
