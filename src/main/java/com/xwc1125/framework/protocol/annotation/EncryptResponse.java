package com.xwc1125.framework.protocol.annotation;

import java.lang.annotation.*;

/**
 * Description: 加密注解
 * </p>
 * 加了此注解的接口将进行数据加密返回
 *
 * @Author: xwc1125
 * @Date: 2019-03-24 22:19:34
 * @Copyright Copyright@2019
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EncryptResponse {

    String value() default "";

    boolean isEncrypt() default true;

}
