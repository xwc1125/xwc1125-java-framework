package com.xwc1125.framework.dataMasking;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2022/8/28 12:18
 * @Copyright Copyright@2022
 */
public interface DataMaskingOperation {

    /**
     * 默认脱敏替换符
     */
    String MASK_CHAR = "*";

    /**
     * 数据脱敏
     *
     * @param content 原文数据
     * @param maskChar 脱敏替换符
     * @return
     */
    String mask(String content, String maskChar);

}

