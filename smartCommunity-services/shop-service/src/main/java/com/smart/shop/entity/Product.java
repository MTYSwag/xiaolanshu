package com.smart.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类 T_Product表
 */
@Data
@TableName("t_product")
public class Product {

    @Schema(description = "商品ID")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "店铺ID/博主ID")
    private Long shopId;

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "商品描述")
    private String description;

    @Schema(description = "商品价格")
    private BigDecimal price;

    @Schema(description = "商品库存")
    private Integer stock;

    @Schema(description = "商品图片URL")
    private String imageUrl;

    @Schema(description = "商品状态（1上架 0下架）")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}