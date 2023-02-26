package com.xwc1125.framework.protocol.annotation;

import java.lang.annotation.*;

/**
 * Description: 不需要进行验证
 *
 * @Author: xwc1125
 * @Date: 2019-03-24 22:18:51
 * @Copyright Copyright@2019
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Pass {

    String value() default "";

}
