package com.xwc1125.framework.protocol.algorithm;

/**
 * @Description: 加密算法接口
 * @Author: xwc1125
 * @Date: 2019-03-24 22:10
 * @Copyright Copyright@2019
 */
public interface EncryptAlgorithm {

    /**
     * 加密
     *
     * @param content
     * @param encryptKey
     * @return
     * @throws Exception
     */
    public String encrypt(String content, String encryptKey) throws Exception;

    /**
     * 解密
     *
     * @param encryptStr
     * @param decryptKey
     * @return
     * @throws Exception
     */
    public String decrypt(String encryptStr, String decryptKey) throws Exception;
}
