package com.xwc1125.framework.autoconfigure.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2019-02-26 13:59
 * @Copyright Copyright@2019
 */
public interface IRedisHelper<T> {

    /**
     * 获取标准的RedisTemplate
     *
     * @return
     */
    RedisTemplate getRedisTemplate();

    /**
     * 获取StringRedisTemplate
     *
     * @return
     */
    StringRedisTemplate getStringRedisTemplate();

    /**
     * 判断key是否存证
     *
     * @param key
     * @return
     */
    boolean exists(String key);

    /**
     * 根据key获取
     *
     * @param key
     * @return
     */
    T get(String key);

    /**
     * 设置
     *
     * @param key
     * @param data
     */
    void set(String key, T data);

    /**
     * 设置
     *
     * @param key
     * @param data
     * @param expireTime 秒
     * @return void
     * @Author: xwc1125
     * @Date: 2019-02-26 14:45:05
     */
    void set(String key, T data, Long expireTime);

    /**
     * 获取字符串
     *
     * @param key
     * @return
     */
    String getString(String key);

    /**
     * 设置字符串
     *
     * @param key
     * @param value
     */
    void setString(String key, String value);

    /**
     * 设置
     *
     * @param key
     * @param value
     * @param expireTime 秒
     */
    void setString(String key, String value, Long expireTime);

    /**
     * 批量删除对应的value
     *
     * @param keys
     * @return void
     * @Author: xwc1125
     * @Date: 2019-02-26 14:34:52
     */
    void delete(String... keys);

    /**
     * 删除
     *
     * @param key
     */
    void delete(String key);

    /***
     * Description: 批量删除key
     * </p>
     * @param pattern
     *
     * @return void
     * @Author: xwc1125
     * @Date: 2019-02-26 14:35:20
     */
    void deletePattern(String pattern);

    /**
     * hash putall
     *
     * @param key
     * @param hashValue
     */
    void hmSet(String key, Map<String, Object> hashValue);

    /***
     * Description: 哈希 添加
     * </p>
     * @param key
     * @param hashKey
     * @param value
     *
     * @return void
     * @Author: xwc1125
     * @Date: 2019-02-26 14:37:45
     */
    void hmSet(String key, Object hashKey, Object value);

    /**
     * Description: 哈希获取数据
     * </p>
     *
     * @param key
     * @param hashKey
     * @return java.lang.Object
     * @Author: xwc1125
     * @Date: 2019-02-26 14:38:42
     */
    Object hmGet(String key, Object hashKey);

    /**
     * hash get
     *
     * @param key
     * @return
     */
    Object hmGet(String key);

    /***
     * leftPush操作
     * @param key
     * @param v
     *
     * @return void
     * @Author: xwc1125
     * @Date: 2019-02-26 14:39:03
     */
    void lPush(String key, Object v);

    /**
     * rightPush
     *
     * @param key
     * @param v
     */
    void rPush(String key, Object v);

    /**
     * Description: 列表获取
     * </p>
     *
     * @param key
     * @param l
     * @param l1
     * @return java.util.List<java.lang.Object>
     * @Author: xwc1125
     * @Date: 2019-02-26 14:40:15
     */
    List<Object> lRange(String key, long l, long l1);

    /**
     * leftPop
     *
     * @param key
     * @return
     */
    Object lPop(String key);

    /**
     * rightPop
     *
     * @param key
     * @return
     */
    Object rPop(String key);

    /**
     * setIfAbsent
     *
     * @param key
     * @param value
     * @return
     */
    boolean setIfAbsent(String key, String value);

    /**
     * setIfAbsent
     *
     * @param key
     * @param value
     * @param expire
     * @return
     */
    boolean setIfAbsent(String key, String value, long expire);

    /***
     * Description: 集合添加
     * </p>
     * @param key
     * @param value
     *
     * @return void
     * @Author: xwc1125
     * @Date: 2019-02-26 14:42:11
     */
    void add(String key, T value);

    /**
     * Description: 集合获取
     * </p>
     *
     * @param key
     * @return java.util.Set<java.lang.Object>
     * @Author: xwc1125
     * @Date: 2019-02-26 14:42:39
     */
    Set<Object> getMembers(String key);

    /***
     * Description: 有序集合添加
     * </p>
     * @param key
     * @param value
     * @param scoure
     *
     * @return void
     * @Author: xwc1125
     * @Date: 2019-02-26 14:43:02
     */
    void zAdd(String key, Object value, double scoure);

    /***
     * Description: 有序集合获取
     * </p>
     * @param key
     * @param scoure
     * @param scoure1
     *
     * @return java.util.Set<java.lang.Object>
     * @Author: xwc1125
     * @Date: 2019-02-26 14:43:21
     */
    Set<Object> rangeByScore(String key, double scoure, double scoure1);

    /**
     * 获取所有的keys
     *
     * @param pattern
     * @return
     */
    Set<String> getKeys(String pattern);

}
