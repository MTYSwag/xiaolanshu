-- 原子增加库存（补偿扣减失败时使用）
-- KEYS[1]: stock:product:{productId}
-- ARGV[1]: 增加数量

local key = KEYS[1]
local quantity = tonumber(ARGV[1])
redis.call('INCRBY', key, quantity)
return redis.call('GET', key)
