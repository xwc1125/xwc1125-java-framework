package com.xwc1125.framework.protocol.algorithm;

import com.xwc1125.common.crypto.aes.AESUtils;

/**
 * @Description: AES 加密算法
 * @Author: xwc1125
 * @Date: 2019-03-24 22:11
 * @Copyright Copyright@2019
 */
public class AesEncryptAlgorithm implements EncryptAlgorithm {
    @Override
    public String encrypt(String content, String encryptKey) throws Exception {
        return AESUtils.Encrypt2Base64(content, encryptKey, null);
    }

    @Override
    public String decrypt(String encryptStr, String decryptKey) throws Exception {
        return AESUtils.DecryptFromBase64(encryptStr, decryptKey, null);
    }
}
