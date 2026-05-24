-- 原子扣减商品库存
-- KEYS[1]: stock:product:{productId}
-- ARGV[1]: 扣减数量
-- 返回: >=0 剩余库存, -1 库存不足
local productKey = KEYS[1]   -- "stock:product:1001"
local quantity = tonumber(ARGV[1])
local stock = tonumber(redis.call('GET', productKey) or '0')
if stock >= quantity then
    redis.call('DECRBY', productKey, quantity)
    return stock - quantity
else
    return -1
end