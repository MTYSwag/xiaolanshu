-- 原子校验并删除 submit token（防重提交）
-- KEYS[1]: submit:token:{userId}:{token}
-- ARGV[1]: 期望值 "1"
-- 返回: 1=校验通过并已删除, 0=重复提交
if redis.call('GET', KEYS[1]) == ARGV[1] then
    return redis.call('DEL', KEYS[1])
else
    return 0
end