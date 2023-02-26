package com.xwc1125.framework.autoconfigure.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.lang.reflect.Field;
import java.time.Duration;

/**
 * @Description: Redis缓存配置类
 * @Author: xwc1125
 * @Date: 2019-02-26 11:42
 * @Copyright Copyright@2019
 */
@Configuration
@EnableCaching
public class RedisConfigure extends CachingConfigurerSupport {

    /***
     * Description: keyGenerator是默认的key生成器
     * </p>
     * 包名+类名+方法名+所有参数生成key。
     * @param
     *
     * @return org.springframework.cache.interceptor.KeyGenerator
     * @Author: xwc1125
     * @Date: 2019-02-26 13:55:33
     */
    @Override
    @Bean
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(".");
            sb.append(method.getName());
            for (Object obj : params) {
                sb.append(".");
                sb.append(obj.toString());
            }
            return sb.toString();
        };
    }

    /***
     * Description: 配置项目model的key的生成策略。用 包名+方法名+返回类名+model的Id。
     * </p>
     * @param
     *
     * @return org.springframework.cache.interceptor.KeyGenerator
     * @Author: xwc1125
     * @Date: 2019-02-26 13:56:27
     */
    @Bean
    public KeyGenerator modelKeyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(".");
            Class<?> clazz = method.getReturnType();
            sb.append(clazz.getSimpleName());
            sb.append(".");
            try {
                for (Object obj : params) {
                    if (obj instanceof Long) {
                        sb.append(obj.toString());
                    } else {
                        Class<?> superclass = obj.getClass().getSuperclass();
                        Field field = superclass.getDeclaredField("id");
                        if (field != null) {
                            field.setAccessible(true);
                            sb.append(field.get(obj).toString());
                        }
                    }
                }
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
            return sb.toString();
        };
    }

    /***
     * Description: 缓存管理器
     * </p>
     * @param redisConnectionFactory
     *
     * @return org.springframework.cache.CacheManager
     * @Author: xwc1125
     * @Date: 2019-02-26 13:57:08
     */
    @SuppressWarnings("rawtypes")
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // 生成一个默认配置，通过config对象即可对缓存进行自定义配置
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        // 设置缓存的默认过期时间，也是使用Duration设置, 设置为30分钟
        config = config.entryTtl(Duration.ofMinutes(30))
                // 不缓存空值
                .disableCachingNullValues();
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(config)
                .transactionAware()
                .build();
    }

    @Primary
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate template = new StringRedisTemplate(factory);
        //设置序列化工具
        setSerializer(template);
        template.afterPropertiesSet();
        return template;
    }

    private void setSerializer(StringRedisTemplate template) {
        @SuppressWarnings({"rawtypes", "unchecked"})
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setValueSerializer(jackson2JsonRedisSerializer);
    }
}
