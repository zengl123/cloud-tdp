package com.zenlin.cloud.tdp.utils;

import com.alibaba.fastjson.JSONObject;
import com.zenlin.cloud.tdp.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 描述:
 * 项目名:tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2018/4/8  12:01.
 */
@Component
public class RedisUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisUtils.class);

    /**
     * 写入缓存(Object对象)
     *
     * @param redisTemplate
     * @param key
     * @param value
     * @param expireTime    失效时间,单位秒,为空则不失效
     * @return
     */
    public boolean setObject(RedisTemplate redisTemplate, final String key, Object value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            if (null != expireTime) {
                redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("写入缓存失败,error={}", e.getMessage());
        }
        return result;
    }

    /**
     * 写入缓存(Object对象)
     *
     * @param redisTemplate
     * @param key
     * @param value
     * @param expireTime    失效时间,单位秒,为空则不失效
     * @return
     */
    public boolean setString(RedisTemplate redisTemplate, final String key, String value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<Serializable, String> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            if (null != expireTime) {
                redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("写入缓存失败,error={}", e.getMessage());
        }
        return result;
    }

    /**
     * 写入缓存(JSONObject对象)
     *
     * @param redisTemplate
     * @param key
     * @param value
     * @param expireTime    失效时间,单位秒,为空则不失效
     * @return
     */
    public boolean setJSONObject(RedisTemplate redisTemplate, final String key, JSONObject value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<Serializable, JSONObject> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            if (null != expireTime) {
                redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("写入缓存失败,error={}", e.getMessage());
        }
        return result;
    }

    /**
     * 写入缓存(Collection对象)
     *
     * @param redisTemplate
     * @param key
     * @param value
     * @param expireTime    失效时间,单位秒,为空则不失效
     * @return
     */
    public boolean setCollect(RedisTemplate redisTemplate, final String key, Collection value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Collection> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            if (null != expireTime) {
                redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("写入缓存失败,error={}", e.getMessage());
        }
        return result;
    }

    /**
     * 读取缓存(String对象)
     *
     * @param key
     * @return
     */
    public String getString(RedisTemplate redisTemplate, final String key) {
        String result;
        ValueOperations<Serializable, String> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        return result;
    }

    /**
     * 读取缓存(Object对象)
     *
     * @param key
     * @return
     */
    public Object getObject(RedisTemplate redisTemplate, final String key) {
        Object result;
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        return result;
    }

    /**
     * 读取缓存(Object对象)
     *
     * @param keyPattern
     * @return
     */
    public Collection getObjects(RedisTemplate redisTemplate, final String keyPattern) {
        Collection keys = redisTemplate.keys(keyPattern);
        return keys;
    }


    /**
     * 读取缓存(Object对象)
     *
     * @param key
     * @return
     */
    public JSONObject getJSONObject(RedisTemplate redisTemplate, final String key) {
        JSONObject result;
        ValueOperations<Serializable, JSONObject> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        return result;
    }

    /**
     * 读取缓存(Object对象)
     *
     * @param key
     * @return
     */
    public Collection getCollect(RedisTemplate redisTemplate, final String key) {
        Collection result;
        ValueOperations<Serializable, Collection> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        return result;
    }

    /**
     * 根据key删除对应的value
     *
     * @param key
     */
    public boolean removeByKey(RedisTemplate redisTemplate, final String key) {
        try {
            if (exists(redisTemplate, key)) {
                redisTemplate.delete(key);
                return true;
            } else {
                throw new BusinessException(key + "不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("根据key清除缓存异常,error={}", e.getMessage());
        }
        return false;
    }

    /**
     * 批量删除对应的value
     *
     * @param keys
     */
    public void removeByKeys(RedisTemplate redisTemplate, final String... keys) {
        for (String key : keys) {
            removeByKey(redisTemplate, key);
        }
    }

    /**
     * 批量删除key
     *
     * @param pattern 正则
     */
    public boolean removeByPattern(RedisTemplate redisTemplate, final String pattern) {
        try {
            Set<Serializable> keys = redisTemplate.keys(pattern);
            if (keys.size() > 0) {
                redisTemplate.delete(keys);
                return true;
            } else {
                throw new BusinessException(pattern + "不存在)");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("批量清除缓存异常,error={}", e.getMessage());
        }
        return false;
    }

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key
     * @return
     */
    public boolean exists(RedisTemplate redisTemplate, final String key) {
        return redisTemplate.hasKey(key);
    }

}
