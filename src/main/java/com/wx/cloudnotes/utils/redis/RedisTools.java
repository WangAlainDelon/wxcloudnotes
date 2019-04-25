package com.wx.cloudnotes.utils.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class RedisTools {
    public static int SECONDS = 3600 * 24;//为key指定过期时间，单位是秒

    private RedisTemplate<Object, Object> redisTemplate;

    /**
     * 解决存入redis中的乱码问题
     */
    @Autowired(required = false)
    public void setRedisTemplate(RedisTemplate<Object, Object> redisTemplate) {
        RedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(stringSerializer);
        this.redisTemplate = redisTemplate;
    }
    /**==================common================*/
    /**
     * 指定缓存失效时间
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                /* redisTemplate.expire(key, time, TimeUnit.SECONDS);*/
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据key获取过期时间
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 判断key是否存在
     */
    public boolean isKeyExist(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /***
     * 删除缓存，可以传一个或多个值,如果是多个值的话默认数据类型是数组
     * @param keys
     * @return
     */
    public void deleteCache(String... keys) {
        if (keys != null && keys.length > 0) {
            if (keys.length == 1) {
                redisTemplate.delete(keys[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(keys));
            }
        }
    }


    /**
     * ==================String类================
     */



    /**
     * 普通的缓存放入
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 普通缓存获取
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /***
     * 添加string类型的数据并设置过期时间
     * @param key
     * @param value
     * @param times
     * @return
     */
    public boolean set(String key, String value, long times) {
        try {
            redisTemplate.opsForValue().set(key, value, times, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /***
     * 递增
     * @param key
     * @param delta
     * @return
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /***
     * 递减
     * @param key
     * @param delta
     * @return
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    /**==================Map类型================*/
    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    /**==================Set类型================*/
    /**
     * ==================List类型================
     */
    /**
     * 根据key来存放值
     * @param key
     * @param str
     * @return
     */
    public Long appendRightList(String key, String str) {
        Long aLong = redisTemplate.opsForList().rightPush(key, str);
        return aLong;
    }
    /***
     * 根据key查询list
     * @param key
     * @return
     */
    public List<String> getList(String key) {
        List<String> list = new ArrayList<String>();
        Long aLong = redisTemplate.opsForList().size(key);
        if (aLong == null || aLong == 0) {
            return null;
        }
        List<Object> range = redisTemplate.opsForList().range(key, 0, aLong);
        for (Object o : range) {
            list.add((String) o);
        }
        return list;
    }

    /**
     * 根据用户名删除redis中笔记本的信息
     *
     * @param key
     * @param count
     * @param value
     * @return
     */
    public Long deleteValueOfList(String key, int count, String value) {
        Long lrem = redisTemplate.opsForList().remove(key, count, value);
        return lrem;
    }
}
