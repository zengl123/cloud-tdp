package com.zenlin.cloud.tdp.utils;

import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 描述:
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/4/25  10:02.
 */
@Ignore
public class JedisUtil {
    @Autowired
    private JedisPool jedisPool;

    /**
     * @param jedisPool
     * @param key
     * @return
     */
    public String get(JedisPool jedisPool, String key) {
        Jedis jedis = jedisPool.getResource();
        String get = jedis.get(key);
        jedis.close();
        return get;
    }

    /**
     * @param key
     * @return
     */
    public String get(String key) {
        Jedis jedis = jedisPool.getResource();
        String get = jedis.get(key);
        jedis.close();
        return get;
    }

    /**
     * @param jedisPool
     * @param key
     * @param value
     * @return
     */
    public String set(JedisPool jedisPool, String key, String value) {
        Jedis jedis = jedisPool.getResource();
        String set = jedis.set(key, value);
        jedis.close();
        return set;
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public String set(String key, String value) {
        Jedis jedis = jedisPool.getResource();
        String set = jedis.set(key, value);
        jedis.close();
        return set;
    }

    /**
     * @param jedisPool
     * @param key
     * @param value
     * @param expire
     * @return
     */
    public String setex(JedisPool jedisPool, String key, String value, int expire) {
        Jedis jedis = jedisPool.getResource();
        String set = jedis.setex(key, expire, value);
        jedis.close();
        return set;
    }

    /**
     * @param key
     * @param value
     * @param expire
     * @return
     */
    public String setex(String key, String value, int expire) {
        Jedis jedis = jedisPool.getResource();
        String set = jedis.setex(key, expire, value);
        jedis.close();
        return set;
    }


    /**
     * 删除一个key
     *
     * @param jedisPool
     * @param key
     * @return
     */
    public String deleted(JedisPool jedisPool, String key) {
        Jedis jedis = jedisPool.getResource();
        Long del = jedis.del(key);
        jedis.close();
        return String.valueOf(del);
    }

    /**
     * 删除一个key
     *
     * @param key
     * @return
     */
    public String deleted(String key) {
        Jedis jedis = jedisPool.getResource();
        Long del = jedis.del(key);
        jedis.close();
        return String.valueOf(del);
    }

    /**
     * 确认一个key是否存在
     *
     * @param jedisPool
     * @param key
     * @return
     */
    public boolean exists(JedisPool jedisPool, String key) {
        Jedis jedis = jedisPool.getResource();
        Boolean exists = jedis.exists(key);
        jedis.close();
        return exists;
    }

    /**
     * 确认一个key是否存在
     *
     * @param key
     * @return
     */
    public boolean exists(String key) {
        Jedis jedis = jedisPool.getResource();
        Boolean exists = jedis.exists(key);
        jedis.close();
        return exists;
    }


    /**
     * 返回当前数据库中key的数目
     *
     * @param jedisPool
     * @return
     */
    public Long size(JedisPool jedisPool) {
        Jedis jedis = jedisPool.getResource();
        Long exists = jedis.dbSize();
        jedis.close();
        return exists;
    }

    /**
     * 返回当前数据库中key的数目
     *
     * @return
     */
    public Long size() {
        Jedis jedis = jedisPool.getResource();
        Long exists = jedis.dbSize();
        jedis.close();
        return exists;
    }

    /**
     * 获得一个key的活动时间
     *
     * @param jedisPool
     * @param key
     * @return
     */
    public Long ttl(JedisPool jedisPool, String key) {
        Jedis jedis = jedisPool.getResource();
        Long ttl = jedis.ttl(key);
        jedis.close();
        return ttl;
    }


    /**
     * 获得一个key的活动时间
     *
     * @param key
     * @return
     */
    public Long ttl(String key) {
        Jedis jedis = jedisPool.getResource();
        Long ttl = jedis.ttl(key);
        jedis.close();
        return ttl;
    }


    /**
     * 按索引查询
     *
     * @param jedisPool
     * @param index
     * @return
     */
    public String select(JedisPool jedisPool, int index) {
        Jedis jedis = jedisPool.getResource();
        String select = jedis.select(index);
        jedis.close();
        return select;
    }

    /**
     * 按索引查询
     *
     * @param index
     * @return
     */
    public String select(int index) {
        Jedis jedis = jedisPool.getResource();
        String select = jedis.select(index);
        jedis.close();
        return select;
    }

    /**
     * 删除当前选择数据库中的所有key
     *
     * @param jedisPool
     * @return
     */
    public String flushDB(JedisPool jedisPool) {
        Jedis jedis = jedisPool.getResource();
        String flushDB = jedis.flushDB();
        jedis.close();
        return flushDB;
    }

    /**
     * 删除当前选择数据库中的所有key
     *
     * @return
     */
    public String flushDB() {
        Jedis jedis = jedisPool.getResource();
        String flushDB = jedis.flushDB();
        jedis.close();
        return flushDB;
    }

    /**
     * 删除所有数据库中的所有key
     *
     * @return
     */
    public String flushAll() {
        Jedis jedis = new Jedis();
        String flushDB = jedis.flushAll();
        jedis.close();
        return flushDB;
    }

    /**
     * @param jedisPool
     * @param key
     * @param value
     * @return
     */
    public Long append(JedisPool jedisPool, String key, String value) {
        Jedis jedis = jedisPool.getResource();
        Long append = jedis.append(key, value);
        jedis.close();
        return append;
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public Long append(String key, String value) {
        Jedis jedis = jedisPool.getResource();
        Long append = jedis.append(key, value);
        jedis.close();
        return append;
    }
}

