package com.zenlin.cloud.tdp.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 描述:
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/4/25  15:17.
 */
@Component
@EnableCaching
public class JedisConfig {
    @Value("${spring.redis.database}")
    private int database;
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

    @Bean
    public JedisPoolConfig getRedisConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        return config;
    }

    @Bean(name = "jedisPool")
    public JedisPool getJedisPool() {
        JedisPoolConfig config = getRedisConfig();
        JedisPool pool = new JedisPool(config, host, port, timeout, null, database);
        return pool;
    }

    @Bean(name = "jedis1Pool")
    public JedisPool getJedis1Pool() {
        JedisPoolConfig config = getRedisConfig();
        JedisPool pool = new JedisPool(config, host, port, timeout, null, database1);
        return pool;
    }
}
