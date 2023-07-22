package com.zhang.bi.manager;

import com.zhang.bi.common.ErrorCode;
import com.zhang.bi.exception.BusinessException;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 专门提供 RedisLimiter 限流基础服务（通用）
 */
@Service
public class RedisLimiterManager {

    @Resource
    private RedissonClient redissonClient;

    /**
     *限流
     *
     * @param key 区分不同的限流器
     */
    public void doRateLimit(String key){
        //创建一个限流器，每秒最多请求两次
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);
        //每次操作请求令牌数
        boolean canOp = rateLimiter.tryAcquire(1);
        if(!canOp){
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }

    }
}
