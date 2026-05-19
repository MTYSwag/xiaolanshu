package com.smart.interact.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.interact.entity.TFavorite;
import org.apache.ibatis.annotations.Mapper;

/**
 * T_FAVORITE表Mapper层（收藏）
 */
@Mapper
public interface TFavoriteMapper extends BaseMapper<TFavorite> {
}
