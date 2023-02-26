package com.xwc1125.framework.jwt;

import com.xwc1125.common.entity.DataInfo;

/**
 * Description: jwtInfo
 * </p>
 *
 * @Author: xwc1125
 * @Date: 2019-04-04 14:54:10
 * @Copyright Copyright@2019
 */
public interface IJWTInfo {

    /**
     * 获取jwt的ID
     *
     * @return
     */
    String getJwtId();

    /**
     * 获取用户ID
     *
     * @return
     */
    String getUserId();

    /**
     * 获取用户名
     *
     * @return
     */
    String getUserName();

    /**
     * 附加内容
     *
     * @return
     */
    DataInfo getDataInfo();
}
