package com.xwc1125.framework.base;

import com.google.gson.Gson;
import com.xwc1125.common.constant.ClientOsType;
import com.xwc1125.common.entity.*;
import com.xwc1125.common.util.date.DateFormat;
import com.xwc1125.common.util.date.DateUtils;
import com.xwc1125.common.util.ip.TcpUtils;
import com.xwc1125.common.util.json.JSON;
import com.xwc1125.common.crypto.aes.AESUtils;
import com.xwc1125.common.crypto.shift.ShiftUtils;
import com.xwc1125.common.util.string.StringUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2019-02-21 14:42
 * @Copyright Copyright@2019
 */
@Slf4j
public class DeCoderUtils {

    private static Gson gson = new Gson();

    // =======================3.0的接口===========================
    public static AppInfo getAppInfo(Map<String, String> deTreeMap, String aesKey) {
        return getAppInfo(deTreeMap, aesKey, 0);
    }

    /**
     * @param @param  deTreeMap
     * @param @return
     * @return AppInfo
     * @Title getAppInfo
     * @Description 接口的处理
     * @author xwc1125
     * @date 2016年3月28日 下午4:30:42
     */
    public static AppInfo getAppInfo(Map<String, String> deTreeMap, String aesKey, int keyTag) {
        String tag = "app";
        String enString = deTreeMap.get(tag);
        AppInfo appInfo3 = new AppInfo();
        if (StringUtils.isEmpty(enString)) {
            log.info("AppInfo数据为空");
            return appInfo3;
        }
        if (StringUtils.isNotEmpty(aesKey)) {
            try {
                String key = ShiftUtils.getKeyValue(aesKey, keyTag);
                enString = AESUtils.DecryptFromBase64(enString, key, null);
            } catch (Exception e) {
            }
        }
        log.info("【AppInfo】" + enString);
        appInfo3 = gson.fromJson(enString, AppInfo.class);
        if (appInfo3 != null) {
            String apikey = appInfo3.getApk();
            appInfo3.setApk(apikey);
            String companyKey = appInfo3.getCpk();
            appInfo3.setCpk(companyKey);
        }
        deTreeMap.remove(tag);
        log.debug(appInfo3.toString());
        return appInfo3;
    }

    public static SdkInfo getSdkInfo(Map<String, String> deTreeMap, String aesKey) {
        return getSdkInfo(deTreeMap, aesKey, 0);
    }

    /**
     * @param @param  deTreeMap
     * @param @return
     * @return SDKInfo
     * @Title getSdkInfo
     * @Description sdkInfo的解析
     * @author xwc1125
     * @date 2016年3月28日 下午4:31:52
     */
    public static SdkInfo getSdkInfo(Map<String, String> deTreeMap, String aesKey, int keyTag) {
        String tag = "sdk";
        String enString = deTreeMap.get(tag);
        SdkInfo sdkInfo3 = new SdkInfo();
        if (StringUtils.isEmpty(enString)) {
            log.info("SDKInfo数据为空");
            return sdkInfo3;
        }
        if (StringUtils.isNotEmpty(aesKey)) {
            try {
                String key = ShiftUtils.getKeyValue(aesKey, keyTag);
                enString = AESUtils.DecryptFromBase64(enString, key, null);
            } catch (Exception e) {
            }
        }
        log.info("【SDKInfo】" + enString);
        sdkInfo3 = gson.fromJson(enString, SdkInfo.class);
        log.debug(sdkInfo3.toString());
        deTreeMap.remove(tag);
        return sdkInfo3;
    }

    public static ClientInfo getClientInfo(Map<String, String> deTreeMap, String aesKey) {
        return getClientInfo(deTreeMap, aesKey, 0);
    }

    public static ClientInfo getClientInfo(Map<String, String> deTreeMap, String aesKey, int keyTag) {
        String tag = "client";
        String enString = deTreeMap.get(tag);
        ClientInfo clientInfo = new ClientInfo();
        if (StringUtils.isEmpty(enString)) {
            log.info("ClientInfo数据为空");
            return clientInfo;
        }
        if (StringUtils.isNotEmpty(aesKey)) {
            try {
                String key = ShiftUtils.getKeyValue(aesKey, keyTag);
                enString = AESUtils.DecryptFromBase64(enString, key, null);
            } catch (Exception e) {
            }
        }
        log.info("【ClientInfo】" + enString);
        clientInfo = gson.fromJson(enString, ClientInfo.class);
        deTreeMap.remove(tag);
        log.debug(clientInfo.toString());
        return clientInfo;
    }

    public static PhoneInfo getPhoneInfo(Map<String, String> deTreeMap, String aesKey) {
        return getPhoneInfo(deTreeMap, aesKey, 0);
    }

    /**
     * @param @param  deTreeMap
     * @param @return
     * @return PhoneInfo
     * @Title getPhoneInfo
     * @Description 手机卡的信息
     * @author xwc1125
     * @date 2016年3月28日 下午4:33:09
     */
    public static PhoneInfo getPhoneInfo(Map<String, String> deTreeMap, String aesKey,
                                         int keyTag) {
        String tag = "sim";
        String enString = deTreeMap.get(tag);
        PhoneInfo phoneInfo3 = new PhoneInfo();
        if (StringUtils.isEmpty(enString)) {
            log.info("PhoneInfo数据为空");
            return phoneInfo3;
        }
        if (StringUtils.isNotEmpty(aesKey)) {
            try {
                /**
                 * { rdCode:1459246660345, mac:null, imei:[868152020680070],
                 * simInfos:[ {imsi:460110013929801, iccid:89860315750109263575,
                 * mobile:null, cn:中国电信(仅限数据连接) — 中国电信, slotId:0, type:2,
                 * isDefalutData:true, isDefalutSms:false, rdCode:1459246660346 },
                 * {imsi:460110013929801, iccid:89860315750109263575, mobile:,
                 * cn:null, slotId:0, type:1, isDefalutData:false,
                 * isDefalutSms:false, rdCode:1459246660345}]}
                 */
                String key = ShiftUtils.getKeyValue(aesKey, keyTag);
                enString = AESUtils.DecryptFromBase64(enString, key, null);
            } catch (Exception e) {
            }
        }

        log.info("【PhoneInfo】" + enString);
        phoneInfo3 = gson.fromJson(enString, PhoneInfo.class);
        log.debug(phoneInfo3.toString());
        deTreeMap.remove(tag);
        return phoneInfo3;
    }

    /**
     * @param @param  treeMap
     * @param @return
     * @return DeviceInfo
     * @Title getIOSDeviceInfo
     * @Description IOS设备信息
     * @author xwc1125
     * @date 2016年4月25日 下午3:04:02
     */
    public static DeviceInfo getDeviceInfo(Map<String, String> dataTreeMap, String aesKey) {
        String tag = "device";
        String enString = dataTreeMap.get(tag);
        DeviceInfo deviceInfo3 = new DeviceInfo();
        if (StringUtils.isEmpty(enString)) {
            log.info("DeviceInfo数据为空");
            return deviceInfo3;
        }
        if (StringUtils.isNotEmpty(aesKey)) {
            try {
                String key = ShiftUtils.getKeyValue(aesKey, 0);
                enString = AESUtils.DecryptFromBase64(enString, key, null);
            } catch (Exception e) {
            }
        }
        log.info("【DeviceInfo】" + enString);
        deviceInfo3 = gson.fromJson(enString, DeviceInfo.class);
        dataTreeMap.remove(tag);
        log.debug(deviceInfo3.toString());
        return deviceInfo3;
    }

    public static CoreDataInfo getDataInfo(Map<String, String> deTreeMap, String aesKey) {
        return getDataInfo(deTreeMap, aesKey, 0);
    }

    /**
     * @param @param  deTreeMap
     * @param @return
     * @return DataInfo
     * @Title getDataInfo
     * @Description 接口3.0的数据解析
     * @author xwc1125
     * @date 2016年3月28日 下午4:34:31
     */
    public static CoreDataInfo getDataInfo(Map<String, String> deTreeMap, String aesKey,
                                           int keyTag) {
        String tag = "data";
        String enString = deTreeMap.get(tag);
        CoreDataInfo coreDataInfo = new CoreDataInfo();
        if (StringUtils.isEmpty(enString)) {
            log.info("CoreDataInfo数据为空");
            return coreDataInfo;
        }
        if (StringUtils.isNotEmpty(aesKey)) {
            try {
                String key = ShiftUtils.getKeyValue(aesKey, keyTag);
                enString = AESUtils.DecryptFromBase64(enString, key, null);
            } catch (Exception e) {
            }
        }

        log.info("【CoreDataInfo】" + enString);
        coreDataInfo = gson.fromJson(enString, CoreDataInfo.class);
        log.debug(coreDataInfo.toString());
        deTreeMap.remove(tag);
        return coreDataInfo;
    }

    // =========================网页============================

    /**
     * @param @param     deTreeMap
     * @param @return
     * @param appSecret
     * @return AppInfo
     * @Title getAppInfo
     * @Description 接口3.0的处理
     * @author xwc1125
     * @date 2016年3月28日 下午4:30:42
     */
    public static AppInfo getAppInfo_Web(TreeMap<String, String> deTreeMap, String appSecret) {
        String tag = "app";
        String enString = deTreeMap.get(tag);
        AppInfo appInfo3 = new AppInfo();
        if (StringUtils.isEmpty(enString, appSecret) || appSecret.length() < 16) {
            System.out.println("【Note】" + appInfo3);
            return appInfo3;
        }
        try {
            String deString = AESUtils.DecryptFromBase64(enString, appSecret.substring(0, 16), null);
            log.info("【AppInfo_Web解密】" + deString);
            Gson gson = new Gson();
            appInfo3 = gson.fromJson(deString, AppInfo.class);
        } catch (Exception e) {
            log.error("解密出错！");
        }
        deTreeMap.remove(tag);
        System.out.println("【Note】" + appInfo3);
        return appInfo3;
    }

    /**
     * @param @param     deTreeMap
     * @param @return
     * @param appSecrect
     * @return SDKInfo
     * @Title getSdkInfo
     * @Description sdkInfo3.0的解析
     * @author xwc1125
     * @date 2016年3月28日 下午4:31:52
     */
    public static SdkInfo getSdkInfo_Web(TreeMap<String, String> deTreeMap, String appSecrect) {
        String tag = "sdk";
        String enString = deTreeMap.get(tag);
        SdkInfo sdkInfo3 = new SdkInfo();
        if (StringUtils.isEmpty(enString, appSecrect) || appSecrect.length() < 16) {
            System.out.println("【Note】" + sdkInfo3);
            return sdkInfo3;
        }
        try {
            String deString = AESUtils.DecryptFromBase64(enString, appSecrect.substring(0, 16), null);
            log.info("【SDKInfo_Web解密】" + deString);
            Gson gson = new Gson();
            sdkInfo3 = gson.fromJson(deString, SdkInfo.class);
        } catch (Exception e) {
            log.error("解密出错！");
        }
        System.out.println("【Note】" + sdkInfo3);
        deTreeMap.remove(tag);
        return sdkInfo3;
    }

    /**
     * @param @param     treeMap
     * @param @return
     * @param appSecrect
     * @return CoreDataInfo
     * @Title getDataInfo_Web
     * @Description 解析data来自web
     * @author xwc1125
     * @date 2016年5月9日 上午9:25:45
     */
    public static CoreDataInfo getDataInfo_Web(TreeMap<String, String> deTreeMap, String appSecrect) {
        String tag = "data";
        String enString = deTreeMap.get(tag);
        CoreDataInfo map = new CoreDataInfo();
        if (StringUtils.isEmpty(enString) || appSecrect.length() < 16) {
            System.out.println("【Note】" + map);
            return map;
        }
        try {
            String deString = AESUtils.DecryptFromBase64(enString, appSecrect.substring(0, 16), null);
            log.info("【CoreDataInfo_Web解密】" + deString);
            map = jsonToRequestDataInfo(deString);
        } catch (Exception e) {
            log.error("解密出错！");
        }
        deTreeMap.remove(tag);
        System.out.println("【Note】" + map);
        return map;
    }

    /**
     * @param @param  jsonString
     * @param @return
     * @return KernelDataInfo
     * @Title jsonToRequestDataInfo
     * @Description 将json数据转换为KernelDataInfo3核心数据
     * @author xwc1125
     * @date 2016年5月6日 上午9:56:05
     */
    @SuppressWarnings({"rawtypes"})
    public static CoreDataInfo jsonToRequestDataInfo(String jsonString) {
        try {
            return JSON.unmarshal(jsonString, CoreDataInfo.class);
        } catch (Exception e) {
            log.error("Json解析有误", e);
        }
        return null;
    }

    /**
     * Description: 从map中获取数据
     * </p>
     *
     * @param requestMap
     * @return com.xwc1125.common.entity.RequestDataObj
     * @Author: xwc1125
     * @Date: 2019-04-22 18:24:33
     */
    public static RequestDataObj getRequestDataObj(HttpServletRequest httpServletRequest, Map<String, String> requestMap, String aesKey) {
        TcpInfo tcpInfo = null;
        try {
            tcpInfo = TcpUtils.getTCPInfo(httpServletRequest, false);
        } catch (Exception e) {

        }
        AppInfo appInfo3 = DeCoderUtils.getAppInfo(requestMap, aesKey);
        SdkInfo sdkInfo3 = DeCoderUtils.getSdkInfo(requestMap, aesKey);
        PhoneInfo phoneInfo3 = DeCoderUtils.getPhoneInfo(requestMap, aesKey);
        DeviceInfo deviceInfo3 = DeCoderUtils.getDeviceInfo(requestMap, aesKey);
        ClientInfo clientInfo = DeCoderUtils.getClientInfo(requestMap, aesKey);
        CoreDataInfo coreDataInfo3 = DeCoderUtils.getDataInfo(requestMap, aesKey);

        RequestDataObj requestDataObj = new RequestDataObj();
        requestDataObj.setTcpInfo(tcpInfo);
        requestDataObj.setApp(appInfo3);
        requestDataObj.setSdk(sdkInfo3);
        requestDataObj.setPhone(phoneInfo3);
        requestDataObj.setDevice(deviceInfo3);
        requestDataObj.setClient(clientInfo);
        requestDataObj.setData(coreDataInfo3);
        requestDataObj.setTreeMap(requestMap);
        requestDataObj.setAesKey(aesKey);
        String platform = appInfo3.getPlatform();
        requestDataObj.setOsType(ClientOsType.getOsTypeByName(platform));
        return requestDataObj;
    }

    /**
     * Description: 获取服务器和移动端的时间间隔
     * </p>
     *
     * @param appInfo3
     * @param sdkInfo3
     * @return long
     * @Author: xwc1125
     * @Date: 2019-04-25 15:06:15
     */
    public static long getInterMinu(AppInfo appInfo3, SdkInfo sdkInfo3) {
        // 手机端的时间和服务器端的时间间隔不能够超过10分钟，否则访问无效，提醒用户修改时间。
        // 同时一次访问在10分钟之类不能重复访问。
        long appTimstap = appInfo3.getR();
        long sdkTimstap = 0L;
        try {
            sdkTimstap = sdkInfo3.getR();
        } catch (Throwable e) {

        }
        long serverTimstap = System.currentTimeMillis();
        long interTimstap = Math.abs(appTimstap - serverTimstap);// 间隔的毫秒数
        long interS = interTimstap / 1000;// 间隔的秒数
        long interMinu = interS / 60;// 间隔的分钟数
        StringBuffer buffer = new StringBuffer();
        buffer.append("app时间戳："
                + DateUtils.FormatDate(new Date(appTimstap), DateFormat.YYYY_MM_dd8HH0mm0ss.getValue()));
        if (sdkTimstap > 0L) {
            buffer.append("==>sdk时间戳："
                    + DateUtils.FormatDate(new Date(sdkTimstap), DateFormat.YYYY_MM_dd8HH0mm0ss.getValue()));
        }
        buffer.append("==>Server时间戳："
                + DateUtils.FormatDate(new Date(serverTimstap), DateFormat.YYYY_MM_dd8HH0mm0ss.getValue()));
        buffer.append("==>时间间隔："
                + interMinu + "分钟");

        log.info(buffer.toString());
        return interMinu;
    }
}
