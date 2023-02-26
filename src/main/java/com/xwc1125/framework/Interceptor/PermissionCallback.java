package com.xwc1125.framework.Interceptor;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2021/2/3 14:43
 * @Copyright Copyright@2021
 */
public interface PermissionCallback {

    /**
     * 校验资源的合法性
     *
     * @param res
     * @return
     */
    public boolean validate(String res);
}
