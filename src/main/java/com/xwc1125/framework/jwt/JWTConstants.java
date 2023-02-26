package com.xwc1125.framework.jwt;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2019-03-08 14:02
 * @Copyright Copyright@2019
 */
public class JWTConstants {
    public static final String JWT_KEY_USER_ID = "userId";
    public static final String JWT_KEY_USER_NAME = "userName";
    public static final String JWT_KEY_DATAINFO = "dataInfo";

    public static final String REDIS_USER_PRI_KEY = "BCOS:AUTH:JWT:PRI";

    /**
     * Description: jweCode
     * </p>
     *
     * @Author: xwc1125
     * @Date: 2019-04-09 19:11:42
     * @Copyright Copyright@2019
     */
    public enum JWT_CODE {
        ERR_TOKEN(4011, "The token has expired"),
        ERR_PARAMS(4012, "Pk or uuid is empty"),
        ERR_DIFF_PK(4013, "Pk is not the same"),
        ERR_DIFF_UUID(4014, "Uuid is not the same"),
        ERR_DIFF_TGID(4015, "TelegramId is empty"),
        ERR_EMPTY_TGID(4016, "TelegramId is not the same"),
        ERR_TYPE(4017, "Token's type is error"),
        ERR_DATA(4018, "Data is error"),
        ERR_TOKEN_DE(4019, "Token is error"),
        ERR_PASS(4020, "User or password is error");

        public final int value;
        public final String msg;

        JWT_CODE(int value, String msg) {
            this.value = value;
            this.msg = msg;
        }
    }

}
