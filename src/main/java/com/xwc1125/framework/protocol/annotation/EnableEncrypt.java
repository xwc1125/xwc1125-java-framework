package com.xwc1125.framework.protocol.annotation;

import com.xwc1125.framework.protocol.autoconfigure.EncryptAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Description: 启用加密Starter
 * </p>
 * 在Spring Boot启动类上加上此注解
 * <pre class="code">
 * &#064;SpringBootApplication
 * &#064;EnableEncrypt
 * public class App {
 *     public static void main(String[] args) {
 *         SpringApplication.run(App.class, args);
 *     }
 * }
 * <pre>
 * @Author: xwc1125
 * @Date: 2019-03-24 22:21:23
 * @Copyright Copyright@2019
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({EncryptAutoConfiguration.class})
public @interface EnableEncrypt {

}
