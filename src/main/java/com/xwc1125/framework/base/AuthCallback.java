package com.xwc1125.framework.base;

import com.xwc1125.common.entity.ClientInfo;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2019-02-21 15:40
 * @Copyright Copyright@2019
 */
public abstract class AuthCallback {

    /**
     * Description: 通过clientID获取客户信息
     * </p>
     *
     * @param clientId
     * @return com.chain.common.entity.ClientInfo
     * @Author: xwc1125
     * @Date: 2019-02-26 11:24:35
     */
    public abstract ClientInfo auth(String clientId);
}
