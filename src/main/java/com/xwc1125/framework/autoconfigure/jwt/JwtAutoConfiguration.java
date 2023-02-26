package com.xwc1125.framework.autoconfigure.jwt;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2019-03-23 21:44
 * @Copyright Copyright@2019
 */
//定义为配置类
@Configuration
//在web条件下成立
@ConditionalOnWebApplication
//启用Properties配置功能，并加入到IOC容器中
@EnableConfigurationProperties({JwtProperties.class})
public class JwtAutoConfiguration {
}
