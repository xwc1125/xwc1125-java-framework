package com.xwc1125.framework.base;

import com.xwc1125.common.entity.AppInfo;
import com.xwc1125.common.entity.DataInfo;
import com.xwc1125.common.entity.response.ResponseInfo;
import com.xwc1125.common.util.i18n.LanguageUtils;
import com.xwc1125.common.util.json.JSON;
import com.xwc1125.common.crypto.aes.AESUtils;
import com.xwc1125.common.crypto.shift.ShiftUtils;
import com.xwc1125.common.util.string.StringUtils;
import com.xwc1125.framework.constant.BizCode;
import com.xwc1125.framework.constant.BusinessCode;
import com.xwc1125.framework.constant.HttpCode;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Title: CodeResultUtils
 * </p>
 * <p>
 * Description: 服务器返回出去的数据格式,基本类
 * </p>
 * <p>
 * </p>
 *
 * @author xwc1125
 * @date 2015-7-15下午1:08:30
 */
@Slf4j
public class CodeResultUtils {

    /**
     * @param @param  code
     * @param @param  msg
     * @param @param  obj
     * @param @return
     * @return ResponseInfo
     * @Title CodeResult
     * @Description 基本返回格式
     * @author xwc1125
     * @date 2016年5月23日 下午5:22:53
     */
    public static ResponseInfo CodeResult(Integer code, String msg, Object obj) {
        log.debug("code=" + code + ",msg=" + msg + ",obj=" + obj);
        ResponseInfo codeResult = new ResponseInfo();
        codeResult.setCode(code);
        String bizCode = HttpCode.PRE_KEY_HTTP_CODE + code;
        String message = LanguageUtils.getMessage(bizCode, msg);
        codeResult.setMsg(message);
        if (obj != null) {
            codeResult.setData(obj);
        }
        try {
            log.info("【服务器数据返回】" + JSON.marshal(codeResult));
        } catch (Exception e) {
            log.info("【服务器数据返回】" + codeResult);
        }
        return codeResult;
    }

    public static ResponseInfo CodeResult(Integer code, String msg) {
        return CodeResult(code, msg, null);
    }

    public static ResponseInfo CodeResult(BizCode bizCode) {
        return CodeResult(bizCode.value(), bizCode.msg());
    }

    // ======================================成功的======================================

    /**
     * @param @param  obj
     * @param @return
     * @return ResponseInfo
     * @Title SuccData
     * @Description 成功访问，有数据返回
     * @author xwc1125
     * @date 2016年5月23日 下午6:06:07
     */
    public static ResponseInfo SuccData(Object obj) {
        return CodeResult(HttpCode.SUCC.CODE_200, "Success", obj);
    }

    @SuppressWarnings("rawtypes")
    public static ResponseInfo SuccMap(Map obj) {
        return CodeResult(HttpCode.SUCC.CODE_200, "Success", obj);
    }

    public static ResponseInfo SuccDataAES(DataInfo dataInfo3, AppInfo appInfo3, String aesKey) {
        return SuccDataAES(dataInfo3.toString(), appInfo3, aesKey, 0);
    }

    public static ResponseInfo SuccDataAES(List<DataInfo> dataInfo3s, AppInfo appInfo3, String aesKey) {
        return SuccDataAES(dataInfo3s.toString(), appInfo3, aesKey, 0);
    }

    /**
     * @param @param  obj
     * @param @param  appInfo3
     * @param @param  tag
     * @param @return
     * @param @throws Exception
     * @return ResponseInfo
     * @Title SuccDataAES
     * @Description 返回的数据是【Aes(appPk+";"+Object,2)】密钥是AES公钥
     * @author xwc1125
     * @date 2016年4月9日 下午7:56:25
     */
    public static ResponseInfo SuccDataAES(String str, AppInfo appInfo3, String aesKey, int keyTag) {
        String appPk = appInfo3.getPk();
        String dataFormat = appPk + ";" + str;
        String key = ShiftUtils.getKeyValue(aesKey, keyTag);
        return SuccDataAES(dataFormat, key);
    }

    public static ResponseInfo SuccDataAES(String string, AppInfo appInfo3, String aesKey) {
        return SuccDataAES(string, appInfo3, aesKey, 0);
    }

    public static ResponseInfo SuccDataAES(List<DataInfo> dataInfo3s, AppInfo appInfo3, String aesKey, int keyTag) {
        return SuccDataAES(dataInfo3s.toString(), appInfo3, aesKey, keyTag);
    }

    //======================================

    public static ResponseInfo SuccDataAES(DataInfo dataInfo3, String key) {
        return SuccDataAES(dataInfo3.toString(), key);
    }

    public static ResponseInfo SuccDataAES(List<DataInfo> dataInfo3s, String key) {
        return SuccDataAES(dataInfo3s.toString(), key);
    }

    public static ResponseInfo SuccDataAES(String str, String key) {
        try {
            log.info("【原始数据】" + str);
            log.info("【Key】" + key);
            if (key == null) {
                log.info("【密钥为空】");
            } else if (key.length() < 16) {
                log.error("【密钥长度不够】" + key.length());
            } else {
                key = key.substring(0, 16);
            }

            String enData = AESUtils.Encrypt2Base64(str, key, null);
            return CodeResult(HttpCode.SUCC.CODE_200, "Success", enData);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return CodeResultUtils.UnKnown();
    }

    /**
     * <p>
     * Title: SuccNull
     * </p>
     * <p>
     * Description: 成功访问，但无数据返回
     * </p>
     *
     * @return
     * @author xwc1125
     * @date 2015-7-15下午1:15:20
     */
    public static ResponseInfo SuccNull() {
        return CodeResult(HttpCode.SUCC.CODE_201, "Success", "");
    }

    // ========================================Fail的=========================================

    /**
     * <p>
     * Title: Fail
     * </p>
     * <p>
     * Description: 返回Fail
     *
     * @return
     * @author xwc1125
     * @date 2015-7-15下午1:10:57
     */
    public static ResponseInfo Fail() {
        return CodeResult(HttpCode.FAIL_REQUEST.CODE_400, "Failure");
    }

    public static ResponseInfo Fail(String msg) {
        return CodeResult(HttpCode.FAIL_REQUEST.CODE_400, msg);
    }

    public static ResponseInfo FailData(Object obj) {
        return CodeResult(HttpCode.FAIL_REQUEST.CODE_400, "Failure", obj);
    }

    @SuppressWarnings("rawtypes")
    public static ResponseInfo FailMap(Map hashMap) {
        return CodeResult(HttpCode.FAIL_REQUEST.CODE_400, "Failure", hashMap);
    }

    public static ResponseInfo FailJson(DataInfo dataInfo3) {
        return CodeResult(HttpCode.FAIL_REQUEST.CODE_400, "Failure", dataInfo3.toString());
    }

    // ==================================停止的======================================

    public static ResponseInfo UnSign() {
        return CodeResult(BusinessCode.ERR_SIGN);
    }

    /**
     * <p>
     * Title: Unauthorized
     * </p>
     * <p>
     * Description: 鉴权失败
     *
     * @return
     * @author xwc1125
     * @date 2015-7-15下午1:14:04
     */
    public static ResponseInfo Unauthorized() {
        return CodeResult(BusinessCode.ERR_UNAUTHORIZED);
    }

    public static ResponseInfo Unauthorized(String message) {
        return CodeResult(HttpCode.FAIL_REQUEST.CODE_401, message);
    }

    public static ResponseInfo Unauthorized(Object object) {
        return CodeResult(HttpCode.FAIL_REQUEST.CODE_401, "Unauthorized", object);
    }

    /**
     * <p>
     * Title: UnKnown
     * </p>
     * <p>
     * Description: 系统操作繁忙
     * </p>
     *
     * @return
     * @author xwc1125
     * @date 2015-7-15下午1:15:42
     */
    public static ResponseInfo UnKnown() {
        return CodeResult(BusinessCode.ERR_UNKOWN);
    }

    /**
     * @param @return
     * @return ResponseInfo
     * @Title UnMobile
     * @Description 手机号格式出错
     * @author xwc1125
     * @date 2016年5月23日 下午6:08:11
     */
    public static ResponseInfo UnMobile() {
        return CodeResult(BusinessCode.ERR_UNKOWN_MOBILE);
    }

    /**
     * @param @return
     * @return ResponseInfo
     * @Title StopErrPay
     * @Description 余额不足
     * @author xwc1125
     * @date 2016年5月18日 下午5:09:34
     */
    public static ResponseInfo ErrPay() {
        return CodeResult(BusinessCode.ERR_PAY_INSUFFICIENT);
    }

    /**
     * description: 支付密码有误
     * </p>
     *
     * @param
     * @return com.xwc1125.common.entity.response.ResponseInfo
     * @author: sJun
     * @date: 2019-05-09 14:32:48
     */
    public static ResponseInfo ErrPayPassword() {
        return CodeResult(BusinessCode.ERR_PAY_PASSWORD);
    }

    /**
     * @param @return
     * @return ResponseInfo
     * @Title StopErrKey
     * @Description 密钥出错
     * @author xwc1125
     * @date 2016年5月18日 下午5:09:34
     */
    public static ResponseInfo ErrKey() {
        return CodeResult(BusinessCode.ERR_KEY);
    }

    /**
     * <p>
     * Title: StopErrContent
     * </p>
     * <p>
     * Description: 内容有误
     *
     * @return
     * @author xwc1125
     * @date 2015-7-15下午1:18:40
     */
    public static ResponseInfo ErrContent() {
        return CodeResult(BusinessCode.ERR_CONTENT);
    }

    /**
     * @param @return
     * @return ResponseInfo
     * @Title UnCpInfo
     * @Description "CPInfo缺失"
     * @author xwc1125
     * @date 2016年5月23日 下午6:23:37
     */
    public static ResponseInfo UnCpInfo() {
        return CodeResult(BusinessCode.ERR_UNKOWN_CPINFO);
    }

    /**
     * @param @return
     * @return ResponseInfo
     * @Title StopErrPayFail
     * @Description "支付失败"
     * @author xwc1125
     * @date 2016年5月23日 下午6:26:15
     */
    public static ResponseInfo ErrPayFail() {
        return CodeResult(BusinessCode.ERR_PAY);
    }

    /**
     * @param @return
     * @return ResponseInfo
     * @Title StopErrPayPower
     * @Description 无支付权限
     * @author xwc1125
     * @date 2016年5月23日 下午6:35:29
     */
    public static ResponseInfo ErrPayPower() {
        return CodeResult(BusinessCode.ERR_PAY_POWER);
    }

    /**
     * @param @return
     * @return ResponseInfo
     * @Title StopErrCM
     * @Description 定制方出错
     * @author xwc1125
     * @date 2016年5月24日 下午3:04:32
     */
    public static ResponseInfo ErrErrCM() {
        return CodeResult(BusinessCode.ERR_CM);
    }

    public static ResponseInfo ErrErrIP() {
        return CodeResult(BusinessCode.ERR_IP);
    }

    /**
     * @param @return
     * @return ResponseInfo
     * @Title StopBadTimstap
     * @Description 时间戳出错
     * @author xwc1125
     * @date 2016年6月24日 下午5:28:04
     */
    public static ResponseInfo ErrBadTimstap() {
        return CodeResult(BusinessCode.ERR_BAD_TIMSTAP);
    }

    /**
     * @param @return
     * @return ResponseInfo
     * @Title StopHadRequest
     * @Description 操作频繁：已经访问过了
     * @author xwc1125
     * @date 2016年6月24日 下午5:36:16
     */
    public static ResponseInfo ErrHadRequest() {
        return CodeResult(BusinessCode.ERR_HAD_REQUEST);
    }

    public static ResponseInfo ErrBadRequest() {
        return CodeResult(BusinessCode.ERR_REQUEST);
    }

    public static ResponseInfo ErrBadRequest(String message) {
        return CodeResult(BusinessCode.ERR_REQUEST.value(), message);
    }

    public static ResponseInfo ErrParams() {
        return CodeResult(BusinessCode.ERR_PARAMS);
    }

    public static ResponseInfo ErrParams(String msg) {
        return CodeResult(BusinessCode.ERR_PARAMS.value(), msg);
    }

    public static ResponseInfo ErrLockPin() {
        return CodeResult(BusinessCode.ERR_LOCK_PIN);
    }

    public static ResponseInfo ErrLockUser() {
        return CodeResult(BusinessCode.ERR_LOCK_USER);
    }

    //===========================返回Jsonp=======================

    /**
     * 返回Jsonp格式数据
     *
     * @param response
     * @param jsonpCallback
     * @param responseInfo
     */
    public static ResponseInfo JsonpResult(HttpServletResponse response, String jsonpCallback, ResponseInfo responseInfo) {
        if (StringUtils.isEmpty(jsonpCallback)) {
            return responseInfo;
        }
        String string = JsonpResult(response, jsonpCallback, responseInfo.toString());
        if (StringUtils.isEmpty(string)) {
            return null;
        }
        return responseInfo;
    }

    /**
     * 返回Jsonp
     *
     * @param response
     */
    public static String JsonpResult(HttpServletResponse response, String jsonpCallback, String msg) {
        if (StringUtils.isEmpty(jsonpCallback)) {
            return msg;
        }
        try {
            response.reset();
            response.setContentType("text/html;charset=UTF-8");//中文乱码的解决方案
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            PrintWriter out = response.getWriter();
            System.out.println(
                    "jsonpCallbackData==>" + jsonpCallback + "(" + msg + ")");
            out.println(jsonpCallback + "(" + msg + ")");// 返回jsonp格式数据
            out.flush();
            out.close();
            return null;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return msg;
    }
}
