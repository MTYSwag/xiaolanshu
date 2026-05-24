package com.smart.shop.domain.req;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateProductReq {

    @Schema(description = "商品ID")
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
}
