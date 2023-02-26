package com.xwc1125.framework.jwt;

import com.xwc1125.common.entity.DataInfo;

import java.io.Serializable;

/**
 * Description:
 * </p>
 *
 * @Author: xwc1125
 * @Date: 2019-04-18 14:57:47
 * @Copyright Copyright@2019
 */
public class JWTInfo implements Serializable, IJWTInfo {

    private String jwtId;
    private String userId;
    private String userName;
    private DataInfo dataInfo;

    public JWTInfo() {
    }

    @Override
    public String getJwtId() {
        return jwtId;
    }

    public void setJwtId(String jwtId) {
        this.jwtId = jwtId;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public DataInfo getDataInfo() {
        return dataInfo;
    }

    public void setDataInfo(DataInfo dataInfo) {
        this.dataInfo = dataInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        JWTInfo jwtInfo = (JWTInfo) o;

        if (userName != null ? !userName.equals(jwtInfo.userName) : jwtInfo.userName != null) {
            return false;
        }
        return userId != null ? userId.equals(jwtInfo.userId) : jwtInfo.userId == null;
    }
}
