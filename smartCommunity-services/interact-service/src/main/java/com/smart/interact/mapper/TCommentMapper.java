package com.smart.interact.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.interact.entity.TComment;
import org.apache.ibatis.annotations.Mapper;

/**
 * T_COMMENT表 Mapper层（评论）
 */
@Mapper
public interface TCommentMapper extends BaseMapper<TComment> {
}
