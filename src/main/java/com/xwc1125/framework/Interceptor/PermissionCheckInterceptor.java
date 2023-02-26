package com.xwc1125.framework.Interceptor;

import com.xwc1125.common.annotation.PermissionCheck;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description: 权限校验的逻辑就是你有权限你就可以访问，没有就不允许访问，本质其实就是一个拦截器。
 * 我们首先需要拿到注解，然后获取注解上的字段进行校验，校验通过返回true，否则返回false
 * @Author: xwc1125
 * @Date: 2021/2/3 14:38
 * @Copyright Copyright@2021
 */
public class PermissionCheckInterceptor extends HandlerInterceptorAdapter {

    private static PermissionCallback callback;

    public static void registerPermissionCallback(PermissionCallback callback) {
        PermissionCheckInterceptor.callback = callback;
    }

    /**
     * 处理器处理之前调用
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        PermissionCheck permission = findPermissionCheck(handlerMethod);

        //如果没有添加权限注解则直接跳过允许访问
        if (permission == null) {
            return true;
        }

        //获取注解中的值
        String resourceKey = permission.resourceKey();

        if (callback != null) {
            return callback.validate(resourceKey);
        }
        return false;
    }

    /**
     * 根据handlerMethod返回注解信息
     *
     * @param handlerMethod 方法对象
     * @return PermissionCheck注解
     */
    private PermissionCheck findPermissionCheck(HandlerMethod handlerMethod) {
        //在方法上寻找注解
        PermissionCheck permission = handlerMethod.getMethodAnnotation(PermissionCheck.class);
        if (permission == null) {
            //在类上寻找注解
            permission = handlerMethod.getBeanType().getAnnotation(PermissionCheck.class);
        }

        return permission;
    }
}
