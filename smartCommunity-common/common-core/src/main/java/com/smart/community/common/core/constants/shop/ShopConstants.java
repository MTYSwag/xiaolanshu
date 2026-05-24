package com.smart.community.common.core.constants.shop;

/**
 * 店铺相关常量
 */
public final class ShopConstants {
    /**
     * 商品不存在或无权操作
     */
    public static final String PRODUCT_NOT_EXIST_OR_NO_PERMISSION = "商品不存在或无权操作";

    /**
     * 商品状态（1上架）
     */
    public static final Integer PRODUCT_STATUS_ON_SHELF = 1;

    /**
     * 商品状态（0下架）
     */
    public static final Integer PRODUCT_STATUS_OFF_SHELF = 0;

    /**
     * 商品名称已存在,无法重复创建
     */
    public static final String PRODUCT_NAME_EXIST = "商品名称已存在,无法重复创建";
    /**
     * 无权操作该商品
     */
    public static final String NO_PERMISSION_TO_OPERATE = "无权操作该商品";

    /**
     * 该商品已在橱窗中
     */
    public static final String PRODUCT_EXIST_IN_SHOWCASE = "该商品已在橱窗中";

    /**
     * 笔记关联商品成功
     */
    public static final String NOTE_PRODUCT_ASSOC_SUCCESS = "笔记关联商品成功";

    /**
     * 笔记取消关联商品成功
     */
    public static final String NOTE_PRODUCT_ASSOC_REMOVE_SUCCESS = "笔记取消关联商品成功";

    /**
     * 该笔记已关联此商品
     */
    public static final String NOTE_PRODUCT_ASSOC_EXIST = "该笔记已关联此商品";

    /**
     * 该笔记未关联此商品
     */
    public static final String NOTE_PRODUCT_ASSOC_NOT_EXIST = "该笔记未关联此商品";

    /**
     * 商品详情缓存前缀
     */
    public static final String PRODUCT_CACHE_PREFIX = "product:detail:";

    /**
     * 热门商品排名缓存键
     */
    public static final String HOT_RANK_KEY = "product:hot:rank";
    /**
     * 缓存过期时间（秒）
     */
    public static final long CACHE_EXPIRE_SECONDS = 3600; // 1小时

    /**
     * 购物车缓存键缀
     */
    public static final String CART_KEY_PREFIX = "cart:user:";

    /**
     * 商品不存在或已下架
     */
    public static final String PRODUCT_NOT_EXIST_OR_OFF_SHELF = "商品不存在或已下架";

    /**
     * 购物车中不存在该商品
     */
    public static final String CART_PRODUCT_NOT_EXIST = "购物车中不存在该商品";

    /**
     * 购物车中没有选中商品
     */
    public static final String CART_PRODUCT_NOT_CHECKED = "购物车中没有选中商品";

    /**
     * 订单不存在
     */
    public static final String PRE_ORDER_NOT_EXIST = "订单不存在";
    /**
     * 订单状态不允许取消
     */
    public static final String PRE_ORDER_STATUS_NOT_ALLOW_CANCEL = "订单状态不允许取消";

    /**
     * 缺少提交token
     */
    public static final String MISSING_SUBMIT_TOKEN = "缺少提交token";

    /**
     * （正式订单的redis键前缀）submit:token:
     */
    public static final String ORDER_SUBMIT_TOKEN_KEY_PREFIX = "submit:order:token:";

    // ==================== 订单相关 ====================

    /** 请勿重复提交订单 */
    public static final String DUPLICATE_SUBMIT_ERROR = "请勿重复提交订单";
    /** 预订单已过期或已处理 */
    public static final String PRE_ORDER_EXPIRED_OR_PROCESSED = "预订单已过期或已处理";
    /** 库存不足 */
    public static final String STOCK_INSUFFICIENT = "库存不足";

    /** 商品库存缓存 key 前缀 */
    public static final String STOCK_KEY_PREFIX = "stock:product:";
    /**
     * lua 通过脚本常量：1
     */
    public static final String LUA_AGREE_ONE = "1";
}
