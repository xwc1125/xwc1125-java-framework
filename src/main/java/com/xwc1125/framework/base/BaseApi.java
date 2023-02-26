package com.xwc1125.framework.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xwc1125.common.constant.ClientOsType;
import com.xwc1125.common.entity.*;
import com.xwc1125.common.entity.response.ResponseInfo;
import com.xwc1125.common.util.date.TimeUtils;
import com.xwc1125.common.util.ip.TcpUtils;
import com.xwc1125.common.crypto.rsa.RSAUtils;
import com.xwc1125.common.util.servlet.ServletUtils;
import com.xwc1125.common.crypto.sign.SignalUtils;
import com.xwc1125.common.util.treemap.TreeMapUtils;
import com.xwc1125.framework.autoconfigure.redis.IRedisHelper;
import com.xwc1125.framework.protocol.config.EncryptionConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2019-02-21 14:35
 * @Copyright Copyright@2019
 */
@Slf4j
@Service
@CacheConfig(cacheNames = "BaseApi")
public abstract class BaseApi<T> {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    protected static boolean isRedisOrigin = false;
    @Autowired
    private IRedisHelper redisHelper;
    @Autowired
    private EncryptionConfig encryptionConfig;

    protected abstract String getPrivateKey();

    /**
     * Description: protocol
     * </p>
     *
     * @param filterProcotolList
     * @param filterSignList
     * @param logMsg
     * @param isAntiBrush
     * @param osType
     * @param authCallback
     * @param callback
     * @return com.xwc1125.common.entity.ResponseInfo
     * @Author: xwc1125
     * @Date: 2019-02-26 15:03:58
     */
    public T protocol(List<String> filterProcotolList, List<String> filterSignList,
                      String logMsg, boolean isAntiBrush, ClientOsType osType, AuthCallback authCallback, Callback<T> callback) {
        if (osType == ClientOsType.ANDROID || osType == ClientOsType.IOS) {
            Object obj = mobileProtocol(filterProcotolList, filterSignList, logMsg, isAntiBrush, callback);
            return (T) obj;
        } else if (osType == ClientOsType.WEB) {
            Object obj = webProtocol(filterProcotolList, filterSignList, logMsg, isAntiBrush, authCallback, callback);
            return (T) obj;
        } else {
            return callbackProtocol(logMsg, callback);
        }
    }

    /**
     * @param @param  request
     * @param @param  response
     * @param @param  model
     * @param @param  filterProcotolList 过滤的List,此数据不进行解密处理
     * @param @param  filterSignList 不进行验签
     * @param @param  stackTraceElement
     * @param @param  logMsg 日志内容
     * @param @return isAntiBrush 是否开启防刷
     * @param @param  callback 回调
     * @param @return
     * @return MyResponseInfo
     * @Title droidProtocol_V3
     * @Description Android 协议解析方法的公用部分
     * @author xwc1125
     * @date 2016年4月5日 下午10:16:28
     */
    public Object mobileProtocol(List<String> filterProcotolList, List<String> filterSignList,
                                 String logMsg, boolean isAntiBrush, Callback callback) {
        // ======================公用部分=========================//
        System.out.println("\n\n\n\n");
        log.info("=================Android入口========================");
        HttpServletRequest request = ServletUtils.getRequest();
        ServletUtils.getResponse().setContentType("text/html; charset=utf-8");
        log.info("【droidProtocol启动......】");
        TcpInfo tcpInfo = TcpUtils.getTCPInfo(request, false);
        log.info("请求头信息：" + tcpInfo.getOs());
        log.info("【TcpInfo】【" + logMsg + "】" + "\n" + tcpInfo);
        TreeMap<String, String> treeMap = SignalUtils.CalculateSign(request);
        log.info("【TreeMap】【" + logMsg + "】" + "\n" + treeMap);
        log.info("Protocol解密......");
        if (treeMap == null) {
            log.error("TreeMap数据为空");
            return callback.onFailure(CodeResultUtils.ErrParams());
        }

        // sdkcpEngine.SDKCPStatisticsAES(treeMap, 0);// sdk统计，打点统计也会在里面

        // ======================公用部分结束======================//

        // ========================验签部分=========================//
        String aesKey = null;
        RequestDataObj requestDataObj = null;
        if (!encryptionConfig.isDebug()) {
            String TAG_KEYString = "rsa";
            String aesRSAEn = treeMap.get(TAG_KEYString);
            if (StringUtils.isEmpty(aesRSAEn)) {

            } else {
                try {
                    aesKey = RSAUtils.decryptByPrivateKeyFromBase64(aesRSAEn, getPrivateKey(), "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 因为数据在头部中，所有没有加入鉴权中
            if (filterSignList == null) {
                filterSignList = new ArrayList<String>();
            }
            TreeMap<String, String> filterSignMap = new TreeMap<String, String>();
            if (filterSignList != null && filterSignList.size() > 0) {
                for (int i = 0; i < filterSignList.size(); i++) {
                    filterSignMap.put(filterSignList.get(i), treeMap.get(filterSignList.get(i)));
                    treeMap.remove(filterSignList.get(i));
                }
            }

            if (!SignalUtils.AuthBySign(treeMap, SignalUtils.getSignKey(tcpInfo.getApi(), aesKey))) {
                return CodeResultUtils.ErrParams();
            }
            if (filterSignMap != null && filterSignMap.size() > 0) {
                TreeMapUtils.treeMap2Map(filterSignMap, treeMap, true);
            }
            // ========================验签部分结束======================//
            log.info("sign鉴权成功......");
            requestDataObj = DeCoderUtils.getRequestDataObj(request, treeMap, aesKey);
            log.info("decode数据......");
            if (isAntiBrush) {
                // 单次访问的数据存入Redis进行URl只能单词访问。防刷
                String urlParams = tcpInfo.getApi() + "_" + requestDataObj.getApp().toString() + "_" + requestDataObj.getSdk().toString() + "_"
                        + requestDataObj.getPhone().toString() + "_" + requestDataObj.getDevice().toString() + "_" + requestDataObj.getCoreDataInfo().toString();
                try {
                    if (isRedisOrigin) {
                        if (redisHelper.exists(urlParams)) {
                            return callback.onFailure(CodeResultUtils.ErrHadRequest());
                        }
                    } else {
                        if (redisHelper.exists(urlParams)) {
                            return callback.onFailure(CodeResultUtils.ErrHadRequest());
                        }
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                // =================防刷结束================
                long interMinu = DeCoderUtils.getInterMinu(requestDataObj.getApp(), requestDataObj.getSdk());
                if (interMinu > 10) {
                    return callback.onFailure(CodeResultUtils.ErrBadTimstap());
                }
                setredis(urlParams);
                // =============url进行缓存防刷保存结束================
                log.info("防刷结束......");
            }
        } else {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
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
                    treeMap = SignalUtils.paramToTreeMap(wholeStr);
                    requestDataObj = DeCoderUtils.getRequestDataObj(request, treeMap, null);
                }
            } catch (Exception e) {

            }
        }

        // 提取附件
        Map<String, MultipartFile> fileMaps = null;
        if (request instanceof MultipartHttpServletRequest) {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            fileMaps = multipartRequest.getFileMap();
        }
        log.info("附件提取结束......");
        // Api接口访问数据统计
        // sdkApiStatEngine.ApiStat(sdkInfo3, tcpInfo, appInfo3);
        // =============Api接口访问数据统计部分结束================
        log.info("【droidProtocol结束......】");
        log.info("【即将进入核心业务逻辑】");
        ResponseInfo object = (ResponseInfo) callback.onSuccess(requestDataObj);
        if (object != null) {
            return object;
        }
        if (fileMaps == null) {
            object = (ResponseInfo) callback.onSuccess(requestDataObj, fileMaps);
        }
        if (object != null) {
            return object;
        }
        object = (ResponseInfo) callback.onSuccess(tcpInfo, treeMap, requestDataObj.getOsType());
        if (object != null) {
            return object;
        }
        return callback.onSuccess(tcpInfo, null, requestDataObj.getApp(), requestDataObj.getSdk(), requestDataObj.getPhone(),
                requestDataObj.getDevice(), requestDataObj.getCoreDataInfo(), treeMap, fileMaps, requestDataObj.getOsType());
    }

    /**
     * @param logMsg   日志内容
     * @param callback 回调
     * @return MyResponseInfo
     * @Title apiMethodCom
     * @Description api方法的公用部分
     * @author xwc1125
     * @date 2016年4月5日 下午10:16:28
     */
    public T callbackProtocol(String logMsg, Callback<T> callback) {
        // ======================公用部分=========================//
        ClientOsType osType = ClientOsType.UNKNOWN;
        System.out.println("\n\n\n\n");
        log.info("=================callbackProtocol入口========================");
        HttpServletRequest request = ServletUtils.getRequest();
        HttpServletResponse response = ServletUtils.getResponse();
        response.setContentType("text/html; charset=utf-8");
        log.info("【callbackProtocol启动......】");
        TcpInfo tcpInfo = TcpUtils.getTCPInfo(request, false);
        log.info("【TcpInfo】【" + logMsg + "】"
                + "\n" + tcpInfo);
        TreeMap<String, String> treeMap = SignalUtils.CalculateSign(request);
        log.info("【TreeMap】【" + logMsg + "】" + "\n" + treeMap);
        log.info("protocol解密......");
        if (treeMap == null) {
            log.error("TreeMap数据为空");
            return callback.onFailure(null);
        }
        log.info("【callbackProtocol_V3结束......】");
        log.info("【即将进入核心业务逻辑】");
        T object = callback.onSuccess(tcpInfo, treeMap, osType);
        if (object != null) {
            return object;
        }
        CoreDataInfo coreDataInfo = new CoreDataInfo();
        TreeMapUtils.treeMap2CoreDataInfo3(treeMap, coreDataInfo, false);
        return callback.onSuccess(tcpInfo, null, null, null, null, null, coreDataInfo,
                treeMap, null, osType);
    }

    /**
     * <p>
     * Title: webProtocol_V3
     * </p>
     * <p>
     * Description: web协议解析方法公用类
     * </p>
     * <p>
     * <p>
     * </p>
     *
     * @param filterProcotolList 过滤的List,此数据不进行解密处理
     * @param filterSignList     不进行验签
     * @param logMsg             日志内容
     * @param isAntiBrush        是否开启防刷
     * @param callback
     * @return
     * @author xwc1125
     * @date 2017年4月11日 下午3:05:56
     */
    public Object webProtocol(List<String> filterProcotolList, List<String> filterSignList,
                              String logMsg, Boolean isAntiBrush, AuthCallback authCallback, Callback callback) {
        // ======================公用部分=========================//
        ClientOsType osType = ClientOsType.WEB;
        System.out.println("\n\n\n\n");
        log.info("=================Web入口========================");
        HttpServletRequest request = ServletUtils.getRequest();
        HttpServletResponse response = ServletUtils.getResponse();
        response.setContentType("text/html; charset=utf-8");
        log.info("【webProtocol启动......】");
        TcpInfo tcpInfo = TcpUtils.getTCPInfo(request, false);
        log.info("【TcpInfo】【" + logMsg + "】"
                + "\n" + tcpInfo);

        TreeMap<String, String> treeMap = SignalUtils.CalculateSign(request);
        log.info("【TreeMap】【" + logMsg + "】" + "\n" + treeMap);
        log.info("protocol解密数据......");
        if (treeMap == null) {
            log.error("TreeMap数据为空");
            return callback.onFailure(CodeResultUtils.ErrParams());
        }

        // sdkcpEngine.SDKCPStatisticsAES(treeMap, 0);// sdk统计，打点统计也会在里面

        // ======================公用部分结束======================//

        String clientId = treeMap.get("client_id");
        if (StringUtils.isEmpty(clientId)) {
            log.error("ClientId为空");
            return CodeResultUtils.Unauthorized();
        }
        ClientInfo clientInfo = null;
        if (authCallback != null) {
            log.info("ClientInfo信息的获取......");
            clientInfo = authCallback.auth(clientId);
        }

        if (clientInfo == null) {
            log.error("clientInfo为空");
            return CodeResultUtils.Unauthorized();
        }
        String appSecrect = clientInfo.getClientSecret();
        if (StringUtils.isEmpty(appSecrect) || appSecrect.length() < 16) {
            log.error("密钥的长度太小" + appSecrect.length());
            return CodeResultUtils.ErrParams("密钥的长度太小");
        }
        // ========================验签部分=========================//
        String aesKey = "";

        TreeMap<String, String> filterSignMap = new TreeMap<String, String>();
        if (filterSignList != null && filterSignList.size() > 0) {
            for (int i = 0; i < filterSignList.size(); i++) {
                filterSignMap.put(filterSignList.get(i), treeMap.get(filterSignList.get(i)));
                treeMap.remove(filterSignList.get(i));
            }
        }
        if (!SignalUtils.AuthBySign(treeMap, SignalUtils.getSignKey(tcpInfo.getApi(), appSecrect))) {
            return CodeResultUtils.ErrParams();
        }
        if (filterSignMap != null && filterSignMap.size() > 0) {
            TreeMapUtils.treeMap2Map(filterSignMap, treeMap, true);
        }
        // ========================验签部分结束======================//
        log.info("sign鉴权成功......");

        AppInfo appInfo3 = DeCoderUtils.getAppInfo_Web(treeMap, appSecrect);
        SdkInfo sdkInfo3 = DeCoderUtils.getSdkInfo_Web(treeMap, appSecrect);
        PhoneInfo phoneInfo3 = DeCoderUtils.getPhoneInfo(treeMap, aesKey);
        DeviceInfo deviceInfo3 = DeCoderUtils.getDeviceInfo(treeMap, aesKey);
        CoreDataInfo coreDataInfo3 = DeCoderUtils.getDataInfo_Web(treeMap, appSecrect);
        log.info("decode数据解密......");

        if (isAntiBrush) {
            // ==================防刷开始==========================
            // 单次访问的数据存入Redis进行URl只能单词访问。防刷
            String urlParams = tcpInfo.getApi() + "_" + appInfo3.toString() + "_" + sdkInfo3.toString() + "_"
                    + phoneInfo3.toString() + "_" + deviceInfo3.toString() + "_" + coreDataInfo3.toString();
            try {
                if (redisHelper.exists(urlParams)) {
                    return callback.onFailure(CodeResultUtils.ErrHadRequest());
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            long interMinu = DeCoderUtils.getInterMinu(appInfo3, sdkInfo3);
            if (interMinu > 10) {
                return callback.onFailure(CodeResultUtils.ErrBadTimstap());
            }
            setredis(urlParams);
            // =================防刷结束================
            log.info("防刷结束......");
        }

        // Api接口访问数据统计
        // sdkApiStatEngine.ApiStat(sdkInfo3, tcpInfo, appInfo3);
        // =============Api接口访问数据统计部分结束================
        log.info("【webProtocol结束......】");
        log.info("【即将进入核心业务逻辑】");
        ResponseInfo object = (ResponseInfo) callback.onSuccess(tcpInfo, treeMap, osType);
        if (object != null) {
            return object;
        }
        return callback.onSuccess(tcpInfo, clientInfo, appInfo3, sdkInfo3, phoneInfo3,
                deviceInfo3, coreDataInfo3, treeMap, null, osType);
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
        try {
            long tenMinute = 10 * TimeUtils.SEC_MINUTE;
            redisHelper.set(urlParams, "", tenMinute);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
        // =============url进行缓存防刷保存结束================
    }
}
