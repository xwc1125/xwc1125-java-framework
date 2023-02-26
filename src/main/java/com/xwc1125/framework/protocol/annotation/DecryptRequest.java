package com.xwc1125.framework.protocol.annotation;

import java.lang.annotation.*;

/**
 * Description: 解密注解
 * </p>
 * 加了此注解的接口将进行数据解密操作
 *
 * @Author: xwc1125
 * @Date: 2019-03-24 22:18:51
 * @Copyright Copyright@2019
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DecryptRequest {

    String value() default "";

    boolean isDecrypt() default true;

    /**
     * 是否可重复请求
     *
     * @return
     */
    boolean isRepeat() default false;
}
