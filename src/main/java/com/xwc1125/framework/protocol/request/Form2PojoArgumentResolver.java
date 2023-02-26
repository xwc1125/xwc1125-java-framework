package com.xwc1125.framework.protocol.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xwc1125.common.entity.RequestDataObj;
import com.xwc1125.common.util.servlet.ServletUtils;
import com.xwc1125.common.crypto.sign.SignalUtils;
import com.xwc1125.framework.base.DeCoderUtils;
import com.xwc1125.framework.protocol.constants.AttributeConstants;
import com.xwc1125.framework.protocol.wrapper.DecryptionRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2019-03-30 12:51
 * @Copyright Copyright@2019
 */
@Slf4j
public class Form2PojoArgumentResolver implements HandlerMethodArgumentResolver {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestData.class);
    }

    @Nullable
    @Override
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        // 返回对应的参数类型的数据
        DecryptionRequestWrapper request =
                webRequest.getNativeRequest(DecryptionRequestWrapper.class);
        if (request != null) {
            RequestDataObj requestDataObj = request.getRequestDataObj();
            log.info("请求内容：" + requestDataObj);
            ServletUtils.getRequest().setAttribute(AttributeConstants.KEY_ATTRIBUTE, requestDataObj);
            return requestDataObj;
        } else {
            try {
                HttpServletRequest httpServletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
                RequestDataObj requestDataObj = null;
                Map<String, String> requestMap;
                if (httpServletRequest instanceof MultipartHttpServletRequest) {
                    requestMap = SignalUtils.CalculateSign(httpServletRequest);
                    log.info("请求内容[start]：" + requestMap);
                    requestDataObj = DeCoderUtils.getRequestDataObj(httpServletRequest, requestMap, null);
                } else {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(httpServletRequest.getInputStream()));
                    String str = "";
                    String wholeStr = "";
                    while ((str = reader.readLine()) != null) {
                        //一行一行的读取body体里面的内容；
                        wholeStr += str;
                    }
                    log.info("请求内容[start]：" + wholeStr);
                    try {
                        requestDataObj = objectMapper.readValue(wholeStr, RequestDataObj.class);
                    } catch (Exception e) {
                        requestMap = SignalUtils.paramToMap(wholeStr);
                        requestDataObj = DeCoderUtils.getRequestDataObj(httpServletRequest, requestMap, null);
                    }
                }
                log.info("请求内容[format]：" + requestDataObj);
                ServletUtils.getRequest().setAttribute(AttributeConstants.KEY_ATTRIBUTE, requestDataObj);
                return requestDataObj;
            } catch (Exception e) {
                throw new IllegalArgumentException("Unable analysis  requestDataObj");
            }
        }
    }
}
