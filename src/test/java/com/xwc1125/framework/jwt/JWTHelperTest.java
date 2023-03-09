package com.xwc1125.framework.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.xwc1125.common.entity.DataInfo;
import com.xwc1125.common.util.json.JSON;
import junit.framework.TestCase;

/**
 * @Description:
 * @Author: xwc1125
 * @Copyright Copyright@2023
 */
public class JWTHelperTest extends TestCase {

    public void testGenerateToken() {
        JWTInfo jwtInfo = new JWTInfo();
        jwtInfo.setUserName("123");
        try {
            String token = JWTHelper.generateToken(jwtInfo, "123456".getBytes(), 3600);
            IJWTInfo infoFromToken = JWTHelper.parseToken(token, "123456".getBytes());
            System.out.println(infoFromToken);
        } catch (JWTException e) {
            throw new RuntimeException(e);
        }

    }

    public void testDataInfo() {
        DataInfo dataInfo = new DataInfo();
        try {
            String value = JSON.getObjectWriter().writeValueAsString(dataInfo);
            DataInfo dataInfo1 = JSON.getObjectMapper().readValue(value, DataInfo.class);
            System.out.println(dataInfo1);
            DataInfo dataInfo2 = JSON.getObjectMapper().readValue("\"{\\\"t\\\":1678324178908}\"", DataInfo.class);
            System.out.println(dataInfo2);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
