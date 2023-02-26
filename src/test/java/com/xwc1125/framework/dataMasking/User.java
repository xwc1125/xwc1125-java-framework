package com.xwc1125.framework.dataMasking;

import java.io.Serializable;
import lombok.Data;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2022/8/28 12:20
 * @Copyright Copyright@2022
 */
@Data
public class User implements Serializable {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 姓名
     */
    @DataMasking(maskFunc = DataMaskingFunc.ALL_MASK)
    private String name;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 邮箱
     */
    @DataMasking(maskFunc = DataMaskingFunc.ALL_MASK)
    private String email;

}
