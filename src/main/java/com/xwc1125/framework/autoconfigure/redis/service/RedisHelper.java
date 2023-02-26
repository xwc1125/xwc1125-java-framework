package com.xwc1125.framework.autoconfigure.redis.service;

import com.xwc1125.framework.autoconfigure.redis.IRedisHelper;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Cacheable：每次一执行该方法之前，都会在缓存中查找该key是否有所对应的缓存， 如果有则在缓存中取出且不再进入该方法。如果没有则执行该方法，将返回值存到redis中。
 * @CachePut：和@Cacheable不同，每次都会调用该方法，然后将返回值存到redis中。
 * @CacheEvict：删除该key及所对应的value。
 * @Author: xwc1125
 * @Date: 2019-02-26 13:58
 * @Copyright Copyright@2019
 */
@Service
public class RedisHelper<T> implements IRedisHelper<T> {

    /**
     * redisTemplate.opsForValue();//操作字符串
     * redisTemplate.opsForHash();//操作hash
     * redisTemplate.opsForList();//操作list
     * redisTemplate.opsForSet();//操作set
     * redisTemplate.opsForZSet();//操作有序set
     */
    private RedisTemplate redisTemplate;
    private StringRedisTemplate stringRedisTemplate;

    public RedisHelper(RedisTemplate redisTemplate, StringRedisTemplate stringRedisTemplate) {
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    @Override
    public StringRedisTemplate getStringRedisTemplate() {
        return stringRedisTemplate;
    }

    @Override
    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get(String key) {
        return (T) redisTemplate.opsForValue().get(key);
    }

    @Override
    public void set(String key, T data) {
        redisTemplate.opsForValue().set(key, data);
    }

    @Override
    public void set(String key, T data, Long expireTime) {
        ValueOperations operations = redisTemplate.opsForValue();
        operations.set(key, data);
        redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
    }

    @Override
    public String getString(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    @Override
    public void setString(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void setString(String key, String value, Long expireTime) {
        ValueOperations operations = stringRedisTemplate.opsForValue();
        operations.set(key, value);
        stringRedisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
    }

    @Override
    public void delete(String... keys) {
        for (String key : keys) {
            redisTemplate.delete(key);
        }
    }

    /**
     * 删除
     *
     * @param key
     */
    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void deletePattern(String pattern) {
        Set keys = redisTemplate.keys(pattern);
        if (keys.size() > 0) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * hash putall
     *
     * @param key
     * @param hashValue
     */
    @Override
    public void hmSet(String key, Map<String, Object> hashValue) {
        HashOperations<String, Object, Object> hash = this.redisTemplate.opsForHash();
        hash.putAll(key, hashValue);
    }

    @Override
    public void hmSet(String key, Object hashKey, Object value) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        hash.put(key, hashKey, value);
    }

    @Override
    public Object hmGet(String key, Object hashKey) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        return hash.get(key, hashKey);
    }

    /**
     * hash get
     *
     * @param key
     * @return
     */
    @Override
    public Object hmGet(String key) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        return hash.entries(key);
    }

    public Object hdel(String key, Object... hashKey) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        return hash.delete(key, hashKey);
    }

    @Override
    public void lPush(String key, Object v) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        list.leftPush(key, v);
    }

    /**
     * rightPush
     *
     * @param key
     * @param v
     */
    @Override
    public void rPush(String key, Object v) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        list.rightPush(key, v);
    }

    @Override
    public List<Object> lRange(String key, long l, long l1) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        return list.range(key, l, l1);
    }

    /**
     * leftPop
     *
     * @param key
     * @return
     */
    @Override
    public Object lPop(String key) {
        ListOperations<String, String> operations = redisTemplate.opsForList();
        return operations.leftPop(key);
    }

    /**
     * rightPop
     *
     * @param key
     * @return
     */
    @Override
    public Object rPop(String key) {
        ListOperations<String, String> operations = redisTemplate.opsForList();
        return operations.rightPop(key);
    }

    /**
     * setIfAbsent
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public boolean setIfAbsent(String key, String value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * setIfAbsent
     *
     * @param key
     * @param value
     * @param expire
     * @return
     */
    @Override
    public boolean setIfAbsent(String key, String value, long expire) {
        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
        return connection.set(key.getBytes(), value.getBytes(), Expiration.seconds(expire), RedisStringCommands.SetOption.ifAbsent());
    }

    @Override
    public void add(String key, T value) {
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        set.add(key, value);
    }

    @Override
    public Set<Object> getMembers(String key) {
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        return set.members(key);
    }

    @Override
    public void zAdd(String key, Object value, double scoure) {
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        zset.add(key, value, scoure);
    }

    @Override
    public Set<Object> rangeByScore(String key, double scoure, double scoure1) {
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        return zset.rangeByScore(key, scoure, scoure1);
    }

    /**
     * 获取所有的keys
     *
     * @param pattern
     * @return
     */
    @Override
    public Set<String> getKeys(String pattern) {
        Set<String> list = redisTemplate.keys(pattern);
        return list;
    }

    /**
     * Description: 自增1
     * </p>
     *
     * @param key
     * @return java.lang.Long
     * @Author: xwc1125
     * @Date: 2019-04-23 16:03:07
     */
    public Long incr(String key) {
        return redisTemplate.boundValueOps(key).increment(1);
    }

    /**
     * Description: 加
     * </p>
     *
     * @param key
     * @param num
     * @return java.lang.Long
     * @Author: xwc1125
     * @Date: 2019-04-23 16:04:00
     */
    public Long incrBy(String key, int num) {
        return redisTemplate.boundValueOps(key).increment(num);
    }

    /**
     * Description: 如果不存在就直接加1，如果未过期，那么加1，已过期，重新开始计算
     * </p>
     *
     * @param key
     * @param seconds
     * @return java.lang.Long
     * @Author: xwc1125
     * @Date: 2019-04-23 16:23:28
     */
    public Long incr(final String key, final Long seconds) {
        if (exists(key)) {
            Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            if (expire > 0) {
                return incr(key);
            }
        }
        redisTemplate.opsForValue().set(key, 1, seconds, TimeUnit.SECONDS);
        return 1L;
    }

    /**
     * Description: 自减1
     * </p>
     *
     * @param key
     * @return java.lang.Long
     * @Author: xwc1125
     * @Date: 2019-04-23 16:04:40
     */
    public Long decr(String key) {
        return redisTemplate.boundValueOps(key).decrement(1);
    }

    /**
     * Description: 减法
     * </p>
     *
     * @param key
     * @param num
     * @return java.lang.Long
     * @Author: xwc1125
     * @Date: 2019-04-23 16:05:15
     */
    public Long decrBy(String key, int num) {
        return redisTemplate.boundValueOps(key).decrement(num);
    }
}
