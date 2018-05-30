package com.zenlin.cloud.tdp.example;

import com.alibaba.fastjson.JSONObject;
import com.zenlin.cloud.tdp.utils.JedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.JedisPool;

/**
 * 描述:
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/4/25  11:22.
 */
@Component
@RestControllerAdvice
@RestController
@RequestMapping(value = "/tdp/jedis/")
public class JedisExample {
    @Autowired
    private JedisUtil jedisUtil;
    @Autowired
    @Qualifier(value = "jedis1Pool")
    private JedisPool jedisPool;

    @PostMapping(value = "set")
    public String set(@RequestBody JSONObject params) {
        String key = params.getString("key");
        String value = params.getString("value");
        String set = jedisUtil.set(jedisPool, key, value);
        String set1 = jedisUtil.set(key, value);
        return set + set1;
    }

    @GetMapping(value = "get/{key}")
    public String get(@PathVariable(value = "key") String key) {
        String get = jedisUtil.get(jedisPool, key);
        String get1 = jedisUtil.get(key);
        return get + get1;
    }

    @GetMapping(value = "deleted/{key}")
    public String deleted(@PathVariable(value = "key") String key) {
        String del = jedisUtil.deleted(jedisPool, key);
        return del;
    }

    @GetMapping(value = "size")
    public Long size() {
        Long size = jedisUtil.size(jedisPool);
        return size;
    }

    @GetMapping(value = "ttl/{key}")
    public Long ttl(@PathVariable(value = "key") String key) {
        Long ttl = jedisUtil.ttl(jedisPool, key);
        return ttl;
    }

    @GetMapping(value = "select/{index}")
    public String select(@PathVariable(value = "index") Integer index) {
        String select = jedisUtil.select(jedisPool, index);
        return select;
    }

    @GetMapping(value = "flushDB")
    public String flushDB() {
        String flushDB = jedisUtil.flushDB(jedisPool);
        return flushDB;
    }

    @GetMapping(value = "flushAll")
    public String flushAll() {
        String flushAll = jedisUtil.flushAll();
        return flushAll;
    }

    @PostMapping(value = "append")
    public Long append(@RequestBody JSONObject params) {
        String key = params.getString("key");
        String value = params.getString("value");
        Long append = jedisUtil.append(jedisPool, key, value);
        return append;
    }
}
