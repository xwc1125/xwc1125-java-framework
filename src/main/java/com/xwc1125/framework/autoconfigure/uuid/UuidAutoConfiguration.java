package com.xwc1125.framework.autoconfigure.uuid;

import com.xwc1125.framework.autoconfigure.uuid.service.impl.UuidServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2019-02-21 11:32
 * @Copyright Copyright@2019
 */
//定义为配置类
@Configuration
//在web条件下成立
@ConditionalOnWebApplication
//启用UuidProperties配置功能，并加入到IOC容器中
@EnableConfigurationProperties({UuidProperties.class})
//导入UuidServiceImpl组件
@Import(UuidServiceImpl.class)
public class UuidAutoConfiguration {

}
