package com.xwc1125.framework.protocol.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description: 加解密配置类
 * @Author: xwc1125
 * @Date: 2019-03-24 22:13
 * @Copyright Copyright@2019
 */
@Data
@ConfigurationProperties(prefix = "spring.encrypt")
public class EncryptionConfig {

    /**
     * 开启调试模式，调试模式下不进行加解密操作，用于像Swagger这种在线API测试场景
     */
    private boolean debug = false;
    /**
     * 默认全部都开启加解密
     */
    private boolean globalEncrypt = true;
    /**
     * AES加密Key
     */
    private String key = "1234567890123456";
    /**
     * 响应数据编码
     */
    private String responseCharset = "UTF-8";
    /**
     * 签名过期时间（分钟）
     */
    private Long signExpireTime = 10L;
    /**
     * 是否是map请求
     */
    private Boolean isToMap = true;

    /**
     * 需要对响应内容进行加密的接口URI<br>
     * 比如：/user/list<br>
     * 不支持@PathVariable格式的URI
     */
    private List<String> responseEncryptUriList = new ArrayList<>();
    /**
     * 不进行加密返回
     */
    private List<String> responseNoEncryptUriList = new ArrayList<>();

    /**
     * 需要对请求内容进行解密的接口URI<br>
     * 比如：/user/list<br>
     * 不支持@PathVariable格式的URI
     */
    private List<String> requestDecryptUriList = new ArrayList<>();
    /**
     * 不进行解密
     */
    private List<String> requestNoDecryptUriList = new ArrayList<>();
    /**
     * 直接通行
     */
    private List<String> requestPassUriList = new ArrayList<>();

    /**
     * 不进行签名的内容
     */
    private List<String> filterSignList = new ArrayList<>();
    /**
     * filter中不打印日志
     */
    private List<String> noLogsList = new ArrayList<>();
    /**
     * 可以重复请求，是在加密请求情况下使用
     */
    private List<String> repeatList = new ArrayList<>();
    /**
     * 过滤器拦截模式
     */
    private String[] urlPatterns = new String[]{"/*"};

    /**
     * 过滤器执行顺序
     */
    private int order = 1;

    public EncryptionConfig() {
    }

    public EncryptionConfig(String key, List<String> responseEncryptUriList, List<String> requestDecryptUriList,
            String responseCharset, boolean debug, String[] urlPatterns, int order) {
        this.key = key;
        this.responseEncryptUriList = responseEncryptUriList;
        this.requestDecryptUriList = requestDecryptUriList;
        this.responseCharset = responseCharset;
        this.debug = debug;
        this.urlPatterns = urlPatterns;
        this.order = order;
    }

    public EncryptionConfig(String key, List<String> responseEncryptUriList, List<String> requestDecryptUriList,
            String responseCharset, boolean debug) {
        this.key = key;
        this.responseEncryptUriList = responseEncryptUriList;
        this.requestDecryptUriList = requestDecryptUriList;
        this.responseCharset = responseCharset;
        this.debug = debug;
    }
}
