package com.xwc1125.framework.constant;

import com.xwc1125.common.util.i18n.MessageUtils;
import com.xwc1125.common.util.string.StringUtils;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2019-04-01 16:28
 * @Copyright Copyright@2019
 */
public enum BusinessCode implements BizCode {

    ERR_UNAUTHORIZED(HttpCode.FAIL_REQUEST.CODE_401, "Unauthorized"),
    ERR_SIGN(HttpCode.FAIL_REQUEST.CODE_402, "Sign is error"),
    ERR_UNKOWN(HttpCode.FAIL_SERVIER.CODE_500, "系统操作繁忙"),
    ERR_UNKOWN_MOBILE(40001, "手机号格式出错"),
    ERR_PAY_INSUFFICIENT(40002, "余额不足"),
    ERR_KEY(40003, "密钥出错"),
    ERR_CONTENT(40004, "内容有误"),
    ERR_UNKOWN_CPINFO(40005, "CPInfo缺失"),
    ERR_PAY(40006, "支付失败"),
    ERR_PAY_POWER(40007, "无支付权限"),
    ERR_CM(40008, "定制方出错"),
    ERR_IP(40009, "IP受限"),
    ERR_BAD_TIMSTAP(40010, "时间戳出错"),
    ERR_HAD_REQUEST(40011, "操作过于频繁"),
    ERR_PARAMS(40012, "参数出错"),
    ERR_LOCK_PIN(40013, "Pin is locked, try again later"),
    ERR_LOCK_USER(40014, "User is locked, try again later"),
    ERR_REQUEST(40015, "Bad request"),
    ERR_PAY_PASSWORD(40016, "支付密码有误"),
    ;

    private final Integer value;
    private final String msg;

    BusinessCode(Integer value, String msg) {
        this.value = value;
        this.msg = msg;
    }

    /**
     * Return the integer value of this status code.
     */
    @Override
    public Integer value() {
        return this.value;
    }

    @Override
    public String msg() {
        String message = null;
        try {
            message = MessageUtils.message("HTTPCODE_" + this.value, null);
        } catch (Exception e) {
        }
        if (StringUtils.isEmpty(message)) {
            message = msg;
        }
        return message;
    }

    @Override
    public String toString() {
        return this.value.toString();
    }
}
