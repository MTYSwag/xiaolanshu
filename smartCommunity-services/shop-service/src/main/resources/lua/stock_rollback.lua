-- 回滚库存（补偿扣减失败时使用）
-- KEYS[1]: stock:product:{productId}
-- ARGV[1]: 回滚数量
redis.call('INCRBY', KEYS[1], ARGV[1])
return redis.call('GET', KEYS[1])