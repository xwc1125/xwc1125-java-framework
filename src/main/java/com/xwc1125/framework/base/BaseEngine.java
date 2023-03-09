package com.xwc1125.framework.base;

import com.xwc1125.common.constant.ClientOsType;
import com.xwc1125.common.crypto.sign.SignalUtils;
import com.xwc1125.common.entity.AppInfo;
import com.xwc1125.common.entity.DataInfo;
import com.xwc1125.common.entity.PhoneInfo;
import com.xwc1125.common.entity.RequestDataObj;
import com.xwc1125.common.entity.response.ResponseInfo;
import com.xwc1125.common.util.http.HttpUtils;
import com.xwc1125.common.util.json.JsonUtils;
import com.xwc1125.common.util.servlet.ServletUtils;
import com.xwc1125.common.util.string.StringUtils;
import com.xwc1125.framework.autoconfigure.jwt.JwtProperties;
import com.xwc1125.framework.autoconfigure.redis.service.RedisHelper;
import com.xwc1125.framework.jwt.IJWTInfo;
import com.xwc1125.framework.jwt.JWTException;
import com.xwc1125.framework.jwt.JWTHelper;
import com.xwc1125.framework.protocol.constants.AttributeConstants;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xwc1125
 * @ClassName BaseEngine
 * @Description
 * @date 2016年4月5日 下午6:34:13
 */
@Slf4j
@Service
public class BaseEngine {

    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    protected RedisHelper redisHelper;

    /**
     * 获取request
     */
    public HttpServletRequest getRequest() {
        return ServletUtils.getRequest();
    }

    /**
     * 获取response
     */
    public HttpServletResponse getResponse() {
        return ServletUtils.getResponse();
    }

    /**
     * 获取session
     */
    public HttpSession getSession() {
        return getRequest().getSession();
    }

    /**
     * <p>
     * Title: backJsonp
     * </p>
     * <p>
     * Description: 使用Jsonp返回数据
     * </p>
     * <p>
     * <p>
     * </p>
     *
     * @param response
     * @param jsonpCallback jsonp的回调参数
     * @param map
     * @throws IOException
     * @author xwc1125
     * @date 2017年2月7日 上午11:48:38
     */
    private boolean backJsonp(HttpServletResponse response, String jsonpCallback,
            Map<String, Object> map, String clientSecret, String clientId) throws IOException {
        // 是否使用jsonp返回数据
        if (StringUtils.isNotEmpty(jsonpCallback)) {
            Map<String, Object> map2 = map;
            String sign = SignalUtils.sign(clientId, map2, clientSecret);// 提供的回调地址+“？”+密钥进行加密
            map2.put("sign", sign);

            PrintWriter out = response.getWriter();
            String resultJSON = JsonUtils.getGson().toJson(map);
            System.out.println("jsonpCallbackData==>" + jsonpCallback + "(" + resultJSON + ")");
            out.println(jsonpCallback + "(" + resultJSON + ")");// 返回jsonp格式数据
            out.flush();
            out.close();
            return true;
        } else {
            return false;
        }
    }

    /**
     * <p>
     * Title: backRedirect
     * </p>
     * <p>
     * Description: 重定向返回给第三方
     * </p>
     * <p>
     * <p>
     * </p>
     *
     * @param response
     * @param redirectUrl
     * @param map
     * @return
     * @throws IOException
     * @author xwc1125
     * @date 2016年11月21日 下午6:05:42
     */
    private void backRedirect(HttpServletResponse response, String redirectUrl,
            Map<String, Object> map, String md5key) throws IOException {
        if (StringUtils.isNotEmpty(redirectUrl)) {
            Map<String, Object> map2 = map;
            String sign = SignalUtils.sign(redirectUrl, map2, md5key);// 提供的回调地址+“？”+密钥进行加密
            map2.put("sign", sign);// 验签结果

            // 重定向地址不为空
            String params = SignalUtils.getHttpParames(map2, null, 0);
            String redirectStr = redirectUrl + "?" + params;
            if (redirectUrl.contains("?")) {
                redirectStr = redirectUrl + "&" + params;
            }
            log.info("重定向=>" + redirectStr);
            response.sendRedirect(redirectStr);
        }
    }

    /**
     * <p>
     * Title: notifyPartner
     * </p>
     * <p>
     * Description: 通知第三方
     * </p>
     * <p>
     * <p>
     * </p>
     *
     * @param notifyUrl
     * @param map
     * @author xwc1125
     * @date 2016年11月21日 下午6:02:28
     */
    private void notify(String notifyUrl, Map<String, Object> map, String md5key) throws IOException {
        if (StringUtils.isNotEmpty(notifyUrl)) {
            Map<String, Object> map2 = map;
            String sign = SignalUtils.sign(notifyUrl, map2, md5key);// 提供的同步地址+“？”+密钥进行加密
            map2.put("sign", sign);// 验签结果

            log.info("===>notifyUrl" + notifyUrl);
            String respondString = HttpUtils.httpPost(notifyUrl, map2);
            log.info("同步通知的结果==》" + respondString);
        }
    }

    /**
     * @param @param appInfo
     * @param @param str需要被加密返回的数据
     * @param @return
     * @param @throws Exception
     * @return ResponseInfo
     * @Title backMiscallInfo
     * @Description 数据返回【通用】
     * @author xwc1125
     * @date 2016年6月28日 上午10:13:24
     */
    public ResponseInfo backInfo(AppInfo appInfo, DataInfo dataInfo, String aesKey, ClientOsType osType)
            throws Exception {
        if (osType == ClientOsType.ANDROID) {
            //android
            return CodeResultUtils.SuccDataAES(dataInfo, appInfo, aesKey);
        } else if (osType == ClientOsType.IOS) {
            return CodeResultUtils.SuccDataAES(dataInfo, appInfo, aesKey);
        } else if (osType == ClientOsType.WEB) {
            // web过来的进行不一样的加密返回
            return CodeResultUtils.SuccDataAES(dataInfo, aesKey);
        } else {
            return CodeResultUtils.SuccData(dataInfo);
        }
    }

    /**
     * 数据返回
     *
     * @param appInfo
     * @param dataInfos
     * @param aesKey
     * @param osType
     * @return
     * @throws Exception
     */
    public ResponseInfo backInfo(AppInfo appInfo, List<DataInfo> dataInfos, String aesKey, ClientOsType osType)
            throws Exception {
        if (osType == ClientOsType.ANDROID || osType == ClientOsType.IOS) {
            //android
            return CodeResultUtils.SuccDataAES(dataInfos, appInfo, aesKey);
        } else if (osType == ClientOsType.WEB) {
            // web过来的进行不一样的加密返回
            return CodeResultUtils.SuccDataAES(dataInfos, aesKey);
        } else {
            return CodeResultUtils.SuccData(dataInfos);
        }
    }

    /**
     * @param appInfo
     * @param str
     * @param aesKey
     * @param osType
     * @return
     * @throws Exception
     */
    public ResponseInfo backInfo(AppInfo appInfo, String str, String aesKey, ClientOsType osType) throws Exception {
        if (osType == ClientOsType.ANDROID || osType == ClientOsType.IOS) {
            //android
            return CodeResultUtils.SuccDataAES(str, appInfo, aesKey);
        } else if (osType == ClientOsType.WEB) {
            // web过来的进行不一样的加密返回
            return CodeResultUtils.SuccDataAES(str, aesKey);
        } else {
            return CodeResultUtils.SuccData(str);
        }
    }

    /**
     * @param @param phoneInfo3
     * @param @return
     * @return boolean
     * @Title isVituralDevice
     * @Description 判断是否是虚拟机
     * @author xwc1125
     * @date 2016年4月11日 下午5:35:40
     */
    public boolean isVituralDevice(PhoneInfo phoneInfo3) {
        log.info("判断是否是虚拟机");
        if (phoneInfo3 == null) {
            log.info("phoneInfo3为空不确定是否是虚拟机");
            return false;
        }
        List<String> list = phoneInfo3.getIes();
        if (list == null || list.size() == 0) {
            log.info("不确定是否是虚拟机");
            return false;
        }
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            String imei = list.get(i);
            imei = imei.replaceAll("0", "");
            if (StringUtils.isNotEmpty(imei)) {
                count++;
            }
        }
        if (count == 0) {
            log.info("是虚拟机");
            return true;
        }
        log.info("不是虚拟机");
        return false;
    }

    /**
     * description: 获取userUuid
     * </p>
     *
     * @param dataObj
     * @return java.lang.String
     * @author: sJun
     * @date: 2019-04-15 19:55:52
     */
    public String getUserUuid(RequestDataObj dataObj) throws JWTException {
        String token = ServletUtils.getRequest().getHeader(jwtProperties.getTokenHeader());
        IJWTInfo jwtInfo = null;
        jwtInfo = JWTHelper.parseToken(token, dataObj, jwtProperties.getRsaSecret());
        return jwtInfo.getUserId();
    }

    /**
     * 更新用户
     *
     * @param dataObj
     * @return
     */
    public String getUpdateBy(RequestDataObj dataObj, String userUuid) {
        String updateBy = dataObj.getClient().getClientId() + "_" + userUuid + "_" + dataObj.getOsType().msg + "_"
                + dataObj.getApp().getPk();
        return updateBy;
    }

    /**
     * 更新用户
     *
     * @param dataObj
     * @return
     */
    public String getUpdateBy(RequestDataObj dataObj) {
        try {
            return getUpdateBy(dataObj, getUserUuid(dataObj));
        } catch (JWTException jwtExection) {
            return null;
        }
    }

    /**
     * 更新用户
     *
     * @return
     */
    public String getUpdateBy() {
        RequestDataObj dataObj = null;
        try {
            dataObj = (RequestDataObj) getRequest().getAttribute(AttributeConstants.KEY_ATTRIBUTE);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        if (dataObj == null) {
            return "";
        }
        return getUpdateBy(dataObj);

    }

    /**
     * Description: 回滚
     * </p>
     *
     * @param
     * @return void
     * @Author: xwc1125
     * @Date: 2019-04-28 19:53:33
     */
    public void rollBack(String message) {
        log.error(message);
        throw new RuntimeException(message);
    }
}
