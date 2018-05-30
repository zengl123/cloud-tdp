package com.zenlin.cloud.tdp.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 描述:
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/5/5  19:36.
 */
@Component
public class RedisTempleUtil {

    public RedisTemplate getRedisTemplate(RedisConnectionFactory connectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setConnectionFactory(connectionFactory);
        //如果key是String 需要配置一下StringSerializer,不然key会乱码 /XX/XX
        template.setValueSerializer(jackson2JsonRedisSerializer);
        return template;
    }


    public RedisConnectionFactory connectionFactory(String hostName, int port, String password, int maxIdle, int minIdle, int maxTotal, int index, long maxWaitMillis) {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setHostName(hostName);
        jedisConnectionFactory.setPassword(password);
        jedisConnectionFactory.setPort(port);
        if (!StringUtils.isEmpty(password)) {
            jedisConnectionFactory.setPassword(password);
        }
        if (index != 0) {
            jedisConnectionFactory.setDatabase(index);
        }
        jedisConnectionFactory.setPoolConfig(poolConfig(maxIdle, minIdle, maxTotal, maxWaitMillis));
        // 初始化连接pool
        jedisConnectionFactory.afterPropertiesSet();
        RedisConnectionFactory factory = jedisConnectionFactory;
        return factory;
    }

    public JedisPoolConfig poolConfig(int maxIdle, int minIdle, int maxTotal, long maxWaitMillis) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxWaitMillis(maxWaitMillis);
        return poolConfig;
    }
}
