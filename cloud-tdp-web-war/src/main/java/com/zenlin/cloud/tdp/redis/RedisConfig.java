package com.zenlin.cloud.tdp.redis;


import com.zenlin.cloud.tdp.utils.RedisTempleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 描述:redis配置
 * 项目名:tdp-data-interface
 *
 * @author ZENLIN
 * @date 2018/1/22  9:24
 */
@Configuration
@EnableCaching
public class RedisConfig {
    @Value("${spring.redis.database0}")
    private int database0;
    @Value("${spring.redis.database1}")
    private int database1;
    @Value("${spring.redis.database10}")
    private int database10;
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.timeout}")
    private int timeout;
    @Value("${spring.redis.pool.max-idle}")
    private int maxIdle;
    @Value("${spring.redis.pool.min-idle}")
    private int minIdle;
    @Value("${spring.redis.pool.max-active}")
    private int maxActive;
    @Value("${spring.redis.pool.max-wait}")
    private long maxWait;

    @Autowired
    private RedisTempleUtil redisTempleUtil;

    @Bean(name = "redisTemplate")
    public RedisTemplate redisTemplate() {
        return redisTempleUtil.getRedisTemplate(redisTempleUtil.connectionFactory(host, port, password, maxIdle, minIdle, maxActive, database0, maxWait));
    }

    @Bean(name = "redis1Template")
    public RedisTemplate redis1Template() {
        return redisTempleUtil.getRedisTemplate(redisTempleUtil.connectionFactory(host, port, password, maxIdle, minIdle, maxActive, database1, maxWait));
    }

    @Bean(name = "redis10Template")
    public RedisTemplate redis10Template() {
        return redisTempleUtil.getRedisTemplate(redisTempleUtil.connectionFactory(host, port, password, maxIdle, minIdle, maxIdle, database10, maxWait));
    }
}

