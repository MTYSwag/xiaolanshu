-- 恢复库存（订单超时取消时调用），容错：key 不存在或值异常时用 SET 兜底
-- KEYS[1]: stock:product:{productId}
-- ARGV[1]: 恢复数量
local val = redis.call('GET', KEYS[1])
if val == false or tonumber(val) == nil then
    redis.call('SET', KEYS[1], ARGV[1])
    return tonumber(ARGV[1])
end
return redis.call('INCRBY', KEYS[1], ARGV[1])
