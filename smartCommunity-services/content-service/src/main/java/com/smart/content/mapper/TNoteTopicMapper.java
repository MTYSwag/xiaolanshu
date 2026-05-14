package com.smart.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.content.entity.TNoteTopic;
import org.apache.ibatis.annotations.Mapper;

/**
 * T_NOTE_TOPIC表Mapper接口（笔记主题关联）
 */
@Mapper
public interface TNoteTopicMapper extends BaseMapper<TNoteTopic> {
}
