package com.xwc1125.framework.protocol.init;

import com.xwc1125.common.util.ip.TcpUtils;
import com.xwc1125.common.util.spring.AnnotationUtils;
import com.xwc1125.framework.protocol.annotation.DecryptRequest;
import com.xwc1125.framework.protocol.annotation.EncryptResponse;
import com.xwc1125.framework.protocol.annotation.Pass;
import com.xwc1125.framework.protocol.config.EncryptionConfig;
import com.xwc1125.framework.protocol.constants.HttpMethodTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Description: 初始化注解
 * </p>
 *
 * @Author: xwc1125
 * @Date: 2019-03-29 10:54:08
 * @Copyright Copyright@2019
 */
@Slf4j
public class ApiEncryptDataInit implements ApplicationContextAware {

    @Autowired
    private EncryptionConfig encryptionConfig;

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        Map<String, Object> beanMap = ctx.getBeansWithAnnotation(RestController.class);
        initData(beanMap);
        beanMap = ctx.getBeansWithAnnotation(Controller.class);
        initData(beanMap);
    }

    private void initData(Map<String, Object> beanMap) {
        if (beanMap != null) {
            for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
                String key = entry.getKey();
                log.debug("[controller]" + key);
                Object bean = entry.getValue();
                Class<?> clz = bean.getClass();
                Method[] methods = clz.getMethods();
                for (Method method : methods) {
                    // 不加密返回
                    if (AnnotationUtils.isAnnotationPresent(method, Pass.class)) {
                        // 注解中的URI优先级高
                        String uri = AnnotationUtils.getAnnotation(method, Pass.class).value();
                        if (!StringUtils.hasText(uri)) {
                            uri = getApiUri(clz, method);
                        }
                        log.debug("Pass URI: {}", uri);
                        encryptionConfig.getRequestPassUriList().add(uri);
                    }
                    // ===============响应===============
                    // 加密返回
                    if (AnnotationUtils.isAnnotationPresent(method, EncryptResponse.class)) {
                        // 注解中的URI优先级高
                        String uri = AnnotationUtils.getAnnotation(method, EncryptResponse.class).value();
                        boolean isEncrypt = AnnotationUtils.getAnnotation(method, EncryptResponse.class).isEncrypt();
                        if (!StringUtils.hasText(uri)) {
                            uri = getApiUri(clz, method);
                        }
                        log.debug("Decrypt URI: {},isEncrypt: {}", uri, isEncrypt);
                        if (isEncrypt) {
                            encryptionConfig.getResponseEncryptUriList().add(uri);
                        } else {
                            encryptionConfig.getResponseNoEncryptUriList().add(uri);
                        }
                    }
                    // ===============请求===============
                    // 解密
                    if (AnnotationUtils.isAnnotationPresent(method, DecryptRequest.class)) {
                        String uri = AnnotationUtils.getAnnotation(method, DecryptRequest.class).value();
                        boolean isDecrypt = AnnotationUtils.getAnnotation(method, DecryptRequest.class).isDecrypt();
                        boolean isRepeat = AnnotationUtils.getAnnotation(method, DecryptRequest.class).isRepeat();
                        if (!StringUtils.hasText(uri)) {
                            uri = getApiUri(clz, method);
                        }
                        log.debug("Decrypt URI: {},isDecrypt: {}, isRepeat: {}", uri, isDecrypt, isRepeat);
                        if (isDecrypt) {
                            encryptionConfig.getRequestDecryptUriList().add(uri);
                        } else {
                            encryptionConfig.getRequestNoDecryptUriList().add(uri);
                        }
                        if (isRepeat) {
                            encryptionConfig.getRepeatList().add(uri);
                        }
                    }
                }
            }
        }
    }

    /**
     * Description: 获取Uri
     * </p>
     *
     * @param clz
     * @param method
     * @return java.lang.String
     * @Author: xwc1125
     * @Date: 2019-03-29 10:53:19
     */
    private String getApiUri(Class<?> clz, Method method) {
        String methodType = "";
        StringBuilder uri = new StringBuilder();

        if (AnnotationUtils.isAnnotationPresent(clz, RequestMapping.class)) {
            uri.append(formatUri(AnnotationUtils.getAnnotation(clz, RequestMapping.class).value()[0]));
        }

        if (AnnotationUtils.isAnnotationPresent(method, GetMapping.class)) {
            methodType = HttpMethodTypeEnum.GET.value();
            uri.append(formatUri(AnnotationUtils.getAnnotation(method, GetMapping.class).value()[0]));
        } else if (AnnotationUtils.isAnnotationPresent(method, PostMapping.class)) {
            methodType = HttpMethodTypeEnum.POST.value();
            uri.append(formatUri(AnnotationUtils.getAnnotation(method, PostMapping.class).value()[0]));
        } else if (AnnotationUtils.isAnnotationPresent(method, RequestMapping.class)) {
            RequestMapping requestMapping = AnnotationUtils.getAnnotation(method, RequestMapping.class);
            try {
                RequestMethod m = requestMapping.method()[0];
                methodType = m.name().toLowerCase();
            } catch (Exception e) {
            }
            uri.append(formatUri(requestMapping.value()[0]));
        } else if (AnnotationUtils.isAnnotationPresent(method, PutMapping.class)) {
            methodType = HttpMethodTypeEnum.PUT.value();
            uri.append(formatUri(AnnotationUtils.getAnnotation(method, PutMapping.class).value()[0]));
        } else if (AnnotationUtils.isAnnotationPresent(method, DeleteMapping.class)) {
            methodType = HttpMethodTypeEnum.DELETE.value();
            uri.append(formatUri(AnnotationUtils.getAnnotation(method, DeleteMapping.class).value()[0]));
        }

        return methodType + TcpUtils.regexApi(uri.toString());
    }

    private String formatUri(String uri) {
        if (uri.startsWith("/")) {
            return uri;
        }
        uri = "/" + uri;
        return TcpUtils.regexApi(uri);
    }
}
