package com.xwc1125.framework.protocol.autoconfigure;

import com.xwc1125.framework.protocol.algorithm.EncryptAlgorithm;
import com.xwc1125.framework.protocol.config.EncryptionConfig;
import com.xwc1125.framework.protocol.filter.EncryptionFilter;
import com.xwc1125.framework.protocol.init.ApiEncryptDataInit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description: 加解密自动配置
 * </p>
 *
 * @Author: xwc1125
 * @Date: 2019-03-24 22:22:52
 * @Copyright Copyright@2019
 */
@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties(EncryptionConfig.class)
public class EncryptAutoConfiguration {

    @Autowired
    private EncryptionConfig encryptionConfig;

    @Autowired(required = false)
    private EncryptAlgorithm encryptAlgorithm;

    /**
     * 不要用泛型注册Filter,泛型在Spring Boot 2.x版本中才有
     *
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
//    @Bean
    public FilterRegistrationBean filterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        if (encryptAlgorithm != null) {
            registration.setFilter(new EncryptionFilter(encryptionConfig, encryptAlgorithm));
        } else {
            registration.setFilter(new EncryptionFilter(encryptionConfig));
        }
        registration.addUrlPatterns(encryptionConfig.getUrlPatterns());
        registration.setName("EncryptionFilter");
        registration.setOrder(encryptionConfig.getOrder());
        return registration;
    }

    @Bean
    public ApiEncryptDataInit apiEncryptDataInit() {
        return new ApiEncryptDataInit();
    }
}
