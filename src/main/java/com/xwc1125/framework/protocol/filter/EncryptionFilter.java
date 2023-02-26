package com.xwc1125.framework.protocol.filter;

import com.xwc1125.common.entity.RequestDataObj;
import com.xwc1125.common.entity.TcpInfo;
import com.xwc1125.common.entity.response.ResponseInfo;
import com.xwc1125.common.util.date.TimeUtils;
import com.xwc1125.common.util.ip.TcpUtils;
import com.xwc1125.common.crypto.sign.SignalUtils;
import com.xwc1125.common.util.spring.SpringUtils;
import com.xwc1125.common.util.string.StringUtils;
import com.xwc1125.common.util.treemap.TreeMapUtils;
import com.xwc1125.framework.autoconfigure.redis.IRedisHelper;
import com.xwc1125.framework.base.CodeResultUtils;
import com.xwc1125.framework.base.DeCoderUtils;
import com.xwc1125.framework.protocol.algorithm.AesEncryptAlgorithm;
import com.xwc1125.framework.protocol.algorithm.EncryptAlgorithm;
import com.xwc1125.framework.protocol.config.EncryptionConfig;
import com.xwc1125.framework.protocol.wrapper.DecryptionRequestWrapper;
import com.xwc1125.framework.protocol.wrapper.EncryptionResponseWrapper;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.TreeMap;

/**
 * @Description: 数据加解密过滤器
 * @Author: xwc1125
 * @Date: 2019-03-24 22:16
 * @Copyright Copyright@2019
 */
@Slf4j
public class EncryptionFilter implements Filter {
    private IRedisHelper redisHelper = SpringUtils.getBean(IRedisHelper.class);
    private EncryptionConfig encryptionConfig;

    private EncryptAlgorithm encryptAlgorithm = new AesEncryptAlgorithm();

    /**
     * Description: 配置和加密算法使用默认的
     * </p>
     *
     * @param
     * @return
     * @Author: xwc1125
     * @Date: 2019-03-25 10:09:57
     */
    public EncryptionFilter() {
        this.encryptionConfig = new EncryptionConfig();
    }

    /**
     * Description: 添加配置，加密算法使用默认
     * </p>
     *
     * @param config
     * @return
     * @Author: xwc1125
     * @Date: 2019-03-25 10:09:42
     */
    public EncryptionFilter(EncryptionConfig config) {
        this.encryptionConfig = config;
    }

    /***
     * Description: 加载加密配置和加密算法
     * </p>
     * @param config
     * @param encryptAlgorithm
     *
     * @return
     * @Author: xwc1125
     * @Date: 2019-03-25 10:10:14
     */
    public EncryptionFilter(EncryptionConfig config, EncryptAlgorithm encryptAlgorithm) {
        this.encryptionConfig = config;
        this.encryptAlgorithm = encryptAlgorithm;
    }

    /**
     * Description: 设置AES密码
     * </p>
     *
     * @param key
     * @return
     * @Author: xwc1125
     * @Date: 2019-03-25 10:10:39
     */
    public EncryptionFilter(String key) {
        EncryptionConfig config = new EncryptionConfig();
        config.setKey(key);
        this.encryptionConfig = config;
    }

    /**
     * Description: 初始化配置
     * </p>
     *
     * @param key
     * @param responseEncryptUriList
     * @param requestDecryptUriList
     * @param responseCharset
     * @param debug
     * @return
     * @Author: xwc1125
     * @Date: 2019-03-25 10:10:59
     */
    public EncryptionFilter(String key, List<String> responseEncryptUriList, List<String> requestDecryptUriList,
                            String responseCharset, boolean debug) {
        this.encryptionConfig = new EncryptionConfig(key, responseEncryptUriList, requestDecryptUriList, responseCharset, debug);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setCharacterEncoding("UTF-8");
        String uri = req.getRequestURI();
        // TODO 请求的数据，此处都能够获取到，数据统计记录此处可以使用
        if (StringUtils.isNotEmpty(uri) && encryptionConfig.getNoLogsList().contains(uri)) {
            chain.doFilter(req, resp);
            return;
        } else {
            log.debug("RequestURI: {}", uri);
            TcpInfo tcpInfo = TcpUtils.getTCPInfo(req, false);
            log.info("请求头信息：" + tcpInfo.getOs());
        }

        // 调试模式不加解密
        if (encryptionConfig.isDebug()) {
            chain.doFilter(req, resp);
            return;
        }

        // 通行判断
        boolean passStatus = this.contains(encryptionConfig.getRequestPassUriList(), uri, req.getMethod());
        if (passStatus) {
            chain.doFilter(req, resp);
            return;
        }
        // 是否全局加密
        if (encryptionConfig.isGlobalEncrypt()) {
            log.debug("全局加密");
            globalEncrypt(response, chain, req, resp, uri);
        } else {
            log.debug("全局不加密");
            globalEncryptNo(response, chain, req, resp, uri);
        }
    }

    /**
     * 全局不进行加密
     *
     * @param response
     * @param chain
     * @param req
     * @param resp
     * @param uri
     * @throws IOException
     */
    private void globalEncryptNo(ServletResponse response, FilterChain chain, HttpServletRequest req, HttpServletResponse resp, String uri) throws IOException, ServletException {
        log.debug("请求的uri=" + uri);
        EncryptionResponseWrapper responseWrapper = null;
        DecryptionRequestWrapper reqestWrapper = null;
        // 判断链接是否有加解密状态
        boolean decryptionStatus = this.contains(encryptionConfig.getRequestDecryptUriList(), uri, req.getMethod());
        boolean encryptionStatus = this.contains(encryptionConfig.getResponseEncryptUriList(), uri, req.getMethod());
        // 没有加解密操作
        if (decryptionStatus == false && encryptionStatus == false) {
            log.debug("没有加解密操作");
            chain.doFilter(req, resp);
            return;
        }
        // 配置了需要解密才处理
        if (decryptionStatus) {
            reqestWrapper = getEncryptionRequestWrapper(req, resp);
        }
        if (encryptionStatus) {
            responseWrapper = new EncryptionResponseWrapper(resp);
        }

        // 同时需要加解密
        if (encryptionStatus && decryptionStatus) {
            log.debug("同时需要加解密");
            chain.doFilter(reqestWrapper, responseWrapper);
        } else if (encryptionStatus) {
            //只需要响应加密
            log.debug("只需要响应加密");
            chain.doFilter(req, responseWrapper);
        } else if (decryptionStatus) {
            //只需要请求解密
            log.debug("只需要请求解密");
            chain.doFilter(reqestWrapper, resp);
        }

        // 配置了需要加密才处理
        if (encryptionStatus) {
            log.debug("配置了需要加密才处理");
            encryptResponseWrapper(response, responseWrapper, null);
        }
    }

    /**
     * Description: 全局加密
     * </p>
     *
     * @param response
     * @param chain
     * @param req
     * @param resp
     * @param uri
     * @return void
     * @Author: xwc1125
     * @Date: 2019-04-25 14:26:35
     */
    private void globalEncrypt(ServletResponse response, FilterChain chain, HttpServletRequest req, HttpServletResponse resp, String uri) throws IOException, ServletException {
        log.debug("全局加密的uri=" + uri);
        EncryptionResponseWrapper responseWrapper = null;
        DecryptionRequestWrapper reqestWrapper = null;
        // 全局加密
        // 判断是否不用解密，不要加密返回
        boolean noDecryptionStatus = this.contains(encryptionConfig.getRequestNoDecryptUriList(), uri, req.getMethod());
        boolean noEncryptionStatus = this.contains(encryptionConfig.getResponseNoEncryptUriList(), uri, req.getMethod());
        // 不用解密也不用加密
        if (noDecryptionStatus && noEncryptionStatus) {
            log.debug("不用解密也不用加密");
            chain.doFilter(req, resp);
            return;
        }
        String aesKey = null;
        if (!noDecryptionStatus) {
            // 需要解密
            log.debug("需要解密");
            reqestWrapper = getEncryptionRequestWrapper(req, resp);
            if (reqestWrapper == null) {
                return;
            }
            try {
                aesKey = reqestWrapper.getRequestDataObj().getAesKey();
            } catch (Exception e) {
            }
        }
        if (!noEncryptionStatus) {
            responseWrapper = new EncryptionResponseWrapper(resp);
        }
        // 同时需要加解密
        if (!noDecryptionStatus && !noEncryptionStatus) {
            log.debug("同时需要加解密");
            chain.doFilter(reqestWrapper, responseWrapper);
        } else if (!noEncryptionStatus) {
            //只需要响应加密
            log.debug("只需要响应加密");
            chain.doFilter(req, responseWrapper);
        } else if (!noDecryptionStatus) {
            //只需要请求解密
            log.debug("只需要请求解密");
            chain.doFilter(reqestWrapper, resp);
        }
        // 配置了需要加密才处理
        if (!noEncryptionStatus) {
            log.debug("配置了需要加密才处理aesKey=" + aesKey);
            encryptResponseWrapper(response, responseWrapper, aesKey);
        }
    }

    /***
     * Description: 加密响应
     * </p>
     * @param response
     * @param responseWrapper
     *
     * @return void
     * @Author: xwc1125
     * @Date: 2019-03-29 11:42:36
     */
    private void encryptResponseWrapper(ServletResponse response, EncryptionResponseWrapper responseWrapper, String aesKey) throws IOException {
        String responseData = responseWrapper.getResponseData();
        log.debug("ResponseData: {}", responseData);
        ServletOutputStream out = null;
        try {
            // 加密数据
            if (StringUtils.isNotEmpty(aesKey)) {
                responseData = encryptAlgorithm.encrypt(responseData, aesKey);
            } else {
                responseData = encryptAlgorithm.encrypt(responseData, encryptionConfig.getKey());
            }
            log.debug("EncryptResponseData: {}", responseData);
            byte[] responseDataBytes = responseData.getBytes(encryptionConfig.getResponseCharset());
            // 设置length会导致部分接口返回数据不全,用bytes的长度代替字符串的长度
            response.setContentLength(responseDataBytes.length);
            response.setCharacterEncoding(encryptionConfig.getResponseCharset());
            out = response.getOutputStream();
            out.write(responseDataBytes);
            log.debug("响应数据结束");
        } catch (Exception e) {
            log.error("响应数据加密失败", e);
            throw new RuntimeException(e);
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

    /**
     * Description: 解密请求
     * </p>
     *
     * @param req
     * @param resp
     * @return com.xwc1125.framework.protocol.wrapper.EncryptionReqestWrapper
     * @Author: xwc1125
     * @Date: 2019-03-29 11:42:40
     */
    private DecryptionRequestWrapper getEncryptionRequestWrapper(HttpServletRequest req, HttpServletResponse resp) {
        boolean isToMap = encryptionConfig.getIsToMap();
        DecryptionRequestWrapper requestWrapper = new DecryptionRequestWrapper(req, isToMap);
        TreeMap<String, String> requestMap = null;
        String requestData = null;
        if (isToMap) {
            requestMap = requestWrapper.getRequestMap();
            log.debug("RequestMap: {}", requestMap);
        } else {
            requestData = requestWrapper.getRequestData();
            log.debug("RequestData: {}", requestData);
        }
        try {
            if (isToMap) {
                requestWrapper = isProtocol(req, resp, requestWrapper, requestMap);
            } else {
                String decryptRequestData = encryptAlgorithm.decrypt(requestData, encryptionConfig.getKey());
                log.debug("DecryptRequestData: {}", decryptRequestData);
                // 解密数据，传递到下一接口
                requestWrapper.setRequestData(decryptRequestData);
            }
        } catch (Exception e) {
            log.error("请求数据解密失败", e);
            respondPrint(resp, CodeResultUtils.ErrKey());
            return null;
        }
        return requestWrapper;
    }

    /**
     * Description: 协议解析
     * </p>
     *
     * @param req
     * @param resp
     * @param requestWrapper
     * @param requestMap
     * @return wrapper.protocol.com.xwc1125.framework.DecryptionRequestWrapper
     * @Author: xwc1125
     * @Date: 2019-04-25 14:59:46
     */
    private DecryptionRequestWrapper isProtocol(HttpServletRequest req, HttpServletResponse resp, DecryptionRequestWrapper requestWrapper, TreeMap<String, String> requestMap) throws Exception {
        SignalUtils.CalculateHeader(req, requestMap);
        if (requestMap == null) {
            log.error("TreeMap数据为空");
            respondPrint(resp, CodeResultUtils.ErrParams());
            return null;
        }
        String TAG_KEYString = "rsa";
        String aesRSAEn = requestMap.get(TAG_KEYString);
        String aesKey = null;
        if (StringUtils.isNotEmpty(aesRSAEn)) {
            aesKey = encryptAlgorithm.decrypt(aesRSAEn, encryptionConfig.getKey());
            requestMap.remove(TAG_KEYString);
        }
        // 因为数据在头部中，所有没有加入鉴权中
        TreeMap<String, String> filterSignMap = new TreeMap<>();
        if (encryptionConfig.getFilterSignList() != null && encryptionConfig.getFilterSignList().size() > 0) {
            for (int i = 0; i < encryptionConfig.getFilterSignList().size(); i++) {
                filterSignMap.put(encryptionConfig.getFilterSignList().get(i), requestMap.get(encryptionConfig.getFilterSignList().get(i)));
                requestMap.remove(encryptionConfig.getFilterSignList().get(i));
            }
        }
        String api = req.getRequestURI();
        api = TcpUtils.regexApi(api);
        if (!SignalUtils.AuthBySign(requestMap, SignalUtils.getSignKey(api, aesKey))) {
            log.error(CodeResultUtils.UnSign().toString());
            respondPrint(resp, CodeResultUtils.UnSign());
            return null;
        }
        if (filterSignMap != null && filterSignMap.size() > 0) {
            TreeMapUtils.treeMap2Map(filterSignMap, requestMap, true);
        }
        RequestDataObj requestDataObj = DeCoderUtils.getRequestDataObj(req, requestMap, aesKey);

        if (!encryptionConfig.getRepeatList().contains(api)) {
            // 单次访问的数据存入Redis进行URl只能单词访问。防刷
            String urlParams = api + "_" + requestDataObj.getApp().toString() + "_" + requestDataObj.getSdk().toString() + "_"
                    + requestDataObj.getPhone().toString() + "_" + requestDataObj.getDevice().toString() + "_" + requestDataObj.getCoreDataInfo().toString();
            // 签名时间和服务器时间相差10分钟以上则认为是过期请求，此时间可以配置
            long interMinu = DeCoderUtils.getInterMinu(requestDataObj.getApp(), requestDataObj.getSdk());
            if (interMinu > encryptionConfig.getSignExpireTime()) {
                log.error(CodeResultUtils.ErrBadTimstap().toString());
                respondPrint(resp, CodeResultUtils.ErrBadTimstap());
                return null;
            }
            try {
                if (redisHelper.exists(urlParams)) {
                    respondPrint(resp, CodeResultUtils.ErrHadRequest());
                    return null;
                }
                setredis(urlParams);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        requestWrapper.setRequestDataObj(requestDataObj);
        return requestWrapper;
    }

    /**
     * @param @param urlParams
     * @return void
     * @Title setredis
     * @Description 防刷设置
     * @author xwc1125
     * @date 2016年7月13日 下午2:48:07
     */
    private void setredis(String urlParams) {
        // =============url进行缓存防刷保存================
        long tenMinute = encryptionConfig.getSignExpireTime() * TimeUtils.SEC_MINUTE;
        redisHelper.set(urlParams, "", tenMinute);
        log.debug("设置防刷成功");
        // =============url进行缓存防刷保存结束================
    }

    private void respondPrint(HttpServletResponse resp, ResponseInfo responseInfo) {
        respondPrint(resp, responseInfo.toString());
    }

    private void respondPrint(HttpServletResponse resp, String msg) {
        PrintWriter print = null;
        try {
            print = resp.getWriter();
            print.write(msg);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private boolean contains(List<String> list, String uri, String methodType) {
        if (list.contains(uri)) {
            return true;
        }
        String prefixUri = methodType.toLowerCase() + ":" + uri;
        log.debug("contains uri: {}", prefixUri);
        if (list.contains(prefixUri)) {
            return true;
        }
        return false;
    }

    @Override
    public void destroy() {

    }
}
