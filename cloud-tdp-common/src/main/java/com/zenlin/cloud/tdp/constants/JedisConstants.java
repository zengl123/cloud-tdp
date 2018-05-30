package com.zenlin.cloud.tdp.constants;

/**
 * 描述:
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/4/25  10:14.
 */
public interface JedisConstants {
    String JEDIS_HOST = "spring.redis.host";
    String JEDIS_PORT = "spring.redis.port";
    String JEDIS_PASSWORD = "spring.redis.password";
    String JEDIS_MAX_TOTAL = "spring.redis.pool.max-active";
    String JEDIS_MAX_IDLE = "spring.redis.pool.max-idle";
    String JEDIS_MIN_IDLE = "spring.redis.pool.min-idle";
    String JEDIS_MAX_WAIT = "spring.redis.pool.max-wait";
}
