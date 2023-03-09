package com.xwc1125.framework.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.xwc1125.common.crypto.rsa.RSAUtils;
import com.xwc1125.common.entity.AppInfo;
import com.xwc1125.common.entity.CoreDataInfo;
import com.xwc1125.common.entity.DataInfo;
import com.xwc1125.common.entity.DeviceInfo;
import com.xwc1125.common.entity.RequestDataObj;
import com.xwc1125.common.util.date.DateUtils;
import com.xwc1125.common.util.json.JSON;
import com.xwc1125.common.util.string.StringUtils;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Description: Jwt
 * </p>
 *
 * @Author: xwc1125
 * @Date: 2019-04-09 18:49:15
 * @Copyright Copyright@2019
 */
@Slf4j
public class JWTHelper {

    public static String generateToken(IJWTInfo jwtInfo, String base64PriKey, long expire) throws JWTException {
        byte[] bytes = getBytes(base64PriKey);
        return generateToken(jwtInfo, bytes, expire);
    }

    /**
     * Description: 生成jwt token
     * </p>
     *
     * @param jwtInfo
     * @param priKey
     * @param expire 过期时间（秒）
     * @return java.lang.String
     * @Author: xwc1125
     * @Date: 2019-04-18 14:51:52
     */
    public static String generateToken(IJWTInfo jwtInfo, byte priKey[], long expire) throws JWTException {
        /**
         * iss: jwt签发者
         * sub: jwt所面向的用户
         * aud: 接收jwt的一方
         * exp: jwt的过期时间，这个过期时间必须要大于签发时间
         * nbf: 定义在什么时间之前，该jwt都是不可用的.
         * iat: jwt的签发时间
         * jti: jwt的唯一身份标识，主要用来作为一次性token,从而回避重放攻击。
         */
        //指定签名的时候使用的签名算法，也就是header那部分，jjwt已经将这部分内容封装好了
        //生成JWT的时间
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        //生成令牌
        Builder builder = JWT.create();
        if (jwtInfo.getUserId() != null) {
            builder = builder.withClaim(JWTConstants.JWT_KEY_USER_ID, jwtInfo.getUserId());
        }
        if (jwtInfo.getUserName() != null) {
            builder = builder.withClaim(JWTConstants.JWT_KEY_USER_NAME, jwtInfo.getUserName());
        }
        if (jwtInfo.getDataInfo() != null) {
            builder = builder.withClaim(JWTConstants.JWT_KEY_DATAINFO, jwtInfo.getDataInfo());
        }
        builder = builder.withExpiresAt(DateUtils.addSeconds(now, expire));
        if (StringUtils.isNotEmpty(jwtInfo.getJwtId())) {
            builder = builder.withJWTId(jwtInfo.getJwtId());
        }
        //设置签名
        String token = builder.sign(Algorithm.HMAC256(new String(priKey)));

        return token;
    }

    /**
     * Description: 由字符串生成加密key
     * </p>
     *
     * @param base64Key
     * @return javax.crypto.SecretKey
     * @Author: xwc1125
     * @Date: 2019-04-18 14:38:52
     */
    public static SecretKey getSecretKey(String base64Key) {
        byte[] encodedKey = getBytes(base64Key);
        return getSecretKey(encodedKey);
    }

    public static byte[] getBytes(String base64Key) {
        return Base64.decodeBase64(base64Key);
    }

    /**
     * Description: 生成Key
     * </p>
     *
     * @param encodedKey
     * @return javax.crypto.SecretKey
     * @Author: xwc1125
     * @Date: 2019-04-18 14:38:45
     */
    private static SecretKey getSecretKey(byte[] encodedKey) {
        // 根据给定的字节数组使用AES加密算法构造一个密钥，使用 encodedKey中的始于且包含 0 到前 leng 个字节这是当然是所有。
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }

    /**
     * Description: 获取token中的用户信息
     * </p>
     *
     * @param token
     * @param priKey
     * @return jwt.com.xwc1125.framework.IJWTInfo
     * @Author: xwc1125
     * @Date: 2019-04-18 14:52:50
     */
    public static IJWTInfo parseToken(String token, byte[] priKey) throws JWTException {
        try {
            JWTInfo jwtInfo = new JWTInfo();
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(new String(priKey))).build();
            DecodedJWT decodedJWT = jwtVerifier.verify(token);
            Claim dataInfoClaim = decodedJWT.getClaim(JWTConstants.JWT_KEY_DATAINFO);
            if (!dataInfoClaim.isNull()) {
                String dataInfoStr = StringUtils.getObjectValue(dataInfoClaim);
                DataInfo dataInfo = JSON.getObjectMapper().readValue(dataInfoStr, DataInfo.class);
                jwtInfo.setDataInfo(dataInfo);
            }

            jwtInfo.setJwtId(decodedJWT.getId());
            Claim userIdClaim = decodedJWT.getClaim(JWTConstants.JWT_KEY_USER_ID);
            if (!userIdClaim.isNull()) {
                jwtInfo.setUserId(userIdClaim.asString());
            }
            Claim userNameClaim = decodedJWT.getClaim(JWTConstants.JWT_KEY_USER_NAME);
            if (!userNameClaim.isNull()) {
                jwtInfo.setUserName(userNameClaim.asString());
            }

            return jwtInfo;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new JWTException(JWTConstants.JWT_CODE.ERR_TOKEN_DE);
        }
    }

    /**
     * Description: 获取token中的用户信息
     * </p>
     *
     * @param token
     * @param base64Key
     * @return jwt.com.xwc1125.framework.IJWTInfo
     * @Author: xwc1125
     * @Date: 2019-04-18 15:43:38
     */
    public static IJWTInfo parseToken(String token, String base64Key) throws JWTException {
        return parseToken(token, getBytes(base64Key));
    }

    /**
     * Description: 获取用户ID
     * </p>
     *
     * @param token
     * @param priKey
     * @return java.lang.String
     * @Author: xwc1125
     * @Date: 2019-04-18 16:03:35
     */
    public static String getUserUuid(String token, byte[] priKey) throws JWTException {
        IJWTInfo ijwtInfo = parseToken(token, priKey);
        return ijwtInfo.getUserId();
    }

    /**
     * Description: 获取用户Uuid
     * </p>
     *
     * @param token
     * @param base64Key
     * @return java.lang.String
     * @Author: xwc1125
     * @Date: 2019-04-18 16:02:43
     */
    public static String getUserUuid(String token, String base64Key) throws JWTException {
        IJWTInfo ijwtInfo = parseToken(token, getBytes(base64Key));
        return ijwtInfo.getUserId();
    }

    public static byte[] priKey;

    /**
     * Description: 获取jwt内容
     * </p>
     *
     * @param dataObj
     * @return jwt.com.xwc1125.framework.IJWTInfo
     * @Author: xwc1125
     * @Date: 2019-04-08 15:15:25
     */
    public static IJWTInfo parseToken(String token, RequestDataObj dataObj, RedisTemplate redisTemplate)
            throws JWTException {
        if (priKey == null || priKey.length == 0) {
            try {
                priKey = RSAUtils.toBytes(redisTemplate.opsForValue().get(JWTConstants.REDIS_USER_PRI_KEY).toString());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new JWTException(JWTConstants.JWT_CODE.ERR_DATA);
            }
        }
        return parseToken(token, dataObj, priKey);
    }

    public static IJWTInfo parseToken(String token, RequestDataObj dataObj, String basePriKey) throws JWTException {
        return parseToken(token, dataObj, getBytes(basePriKey));
    }

    public static IJWTInfo parseToken(String token, RequestDataObj dataObj, byte[] priKey) throws JWTException {
        DataInfo dataInfo = null;
        IJWTInfo ijwtInfo = null;
        try {
            ijwtInfo = parseToken(token, priKey);
            dataInfo = ijwtInfo.getDataInfo();
        } catch (JWTException e) {
            log.error(e.getMessage(), e);
            if (e.getCode() == JWTConstants.JWT_CODE.ERR_DATA.value) {
                throw e;
            }
            throw new JWTException(JWTConstants.JWT_CODE.ERR_TOKEN);
        }
        AppInfo appInfo = dataObj.getApp();
        DeviceInfo deviceInfo = dataObj.getDevice();
        CoreDataInfo coreDataInfo = dataObj.getData();
        String pk = appInfo.getPk();
        String uuid = deviceInfo.getUid();
        String telegramId = coreDataInfo.getString("telegramId");

        String pk1 = dataInfo.getStr("pk");
        String uuid1 = dataInfo.getStr("uuid");
        String telegramId1 = dataInfo.getStr("telegramId");
        Integer type = dataInfo.getInt("type");
        String userUuid = ijwtInfo.getUserId();
        if (StringUtils.isEmpty(pk, pk1, uuid, uuid1)) {
            throw new JWTException(JWTConstants.JWT_CODE.ERR_PARAMS);
        }
        if (!pk.equals(pk1)) {
            throw new JWTException(JWTConstants.JWT_CODE.ERR_DIFF_PK);
        }
        if (!uuid.equals(uuid)) {
            throw new JWTException(JWTConstants.JWT_CODE.ERR_DIFF_UUID);
        }
        if (StringUtils.isEmpty(telegramId)) {
            throw new JWTException(JWTConstants.JWT_CODE.ERR_EMPTY_TGID);
        }
        if (!telegramId.equals(telegramId1)) {
            throw new JWTException(JWTConstants.JWT_CODE.ERR_DIFF_TGID);
        }
        if (type == null || type != 1) {
            throw new JWTException(JWTConstants.JWT_CODE.ERR_TYPE);
        }
        return ijwtInfo;
    }
}
