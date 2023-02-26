package com.xwc1125.framework.autoconfigure.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2019-03-08 18:51
 * @Copyright Copyright@2019
 */
@Data
@EnableCaching
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String tokenHeader;
    private Long expire;
    private Long loginExpire;
    private String rsaSecret;
}
