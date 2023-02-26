package com.xwc1125.framework.Interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2019-03-04 15:51
 * @Copyright Copyright@2019
 */
@Slf4j
@Component
public class SecurityInterceptor extends HandlerInterceptorAdapter {

    /**
     * Description: 在 controller 接收请求、处理 request 之前执行，
     * 返回值为 boolean，返回值为 true 时接着执行 postHandle() 和 afterCompletion() 方法；
     * 如果返回 false 则 中断 执行。
     * </p>
     *
     * @param request
     * @param response
     * @param object
     * @return boolean
     * @Author: xwc1125
     * @Date: 2019-03-04 16:13:36
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
        log.debug("执行拦截器的preHandle方法");
//        HttpSession session = request.getSession();
//        if (session.getAttribute(WebSecurityConfig.SESSION_KEY) != null) {
//            return true;
//        }
//
//        // 跳转登录
//        String url = "/login";
//        response.sendRedirect(url);
        return true;
    }

    /**
     * Description: 在 controller 处理请求之后， ModelAndView 处理前执行，可以对 响应结果 进行修改。
     * </p>
     *
     * @param request
     * @param response
     * @param object
     * @param modelAndView
     * @return void
     * @Author: xwc1125
     * @Date: 2019-03-04 16:14:16
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object object, ModelAndView modelAndView) throws Exception {
        log.debug("执行拦截器的postHandle方法");
    }

    /**
     * Description: 在 DispatchServlet 对本次请求处理完成，即生成 ModelAndView 之后执行。
     * </p>
     *
     * @param request
     * @param response
     * @param object
     * @param e
     * @return void
     * @Author: xwc1125
     * @Date: 2019-03-04 16:14:32
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object object, Exception e) throws Exception {
        log.debug("执行拦截器的afterCompletion方法");
    }
}
