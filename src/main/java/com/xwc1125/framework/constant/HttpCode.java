package com.xwc1125.framework.constant;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2019-04-01 11:46
 * @Copyright Copyright@2019
 */
public class HttpCode {

    public static final String PRE_KEY_HTTP_CODE = "http:code:";
    /**
     * @author xwc1125
     * @ClassName MESSAGE
     * @Description 消息（1字头）
     * @date 2016年2月24日 下午6:23:12
     */
    public class MESSAGE {
        /**
         * 100 Continue
         */
        public static final int CODE_100 = 100;
        /**
         * 101 Switching Protocols
         */
        public static final int CODE_101 = 101;
        /**
         * 102 Processing
         */
        public static final int CODE_102 = 102;
    }

    /**
     * @author xwc1125
     * @ClassName SUCC
     * @Description 成功（2字头）
     * @date 2016年2月24日 下午6:23:23
     */
    public class SUCC {
        /**
         * 200 OK
         */
        public static final int CODE_200 = 200;
        /**
         * 201 Created
         */
        public static final int CODE_201 = 201;
        /**
         * 202 Accepted
         */
        public static final int CODE_202 = 202;
        /**
         * 203 Non-Authoritative Information
         */
        public static final int CODE_203 = 203;
        /**
         * 204 No Content
         */
        public static final int CODE_204 = 204;
        /**
         * 205 Reset Content
         */
        public static final int CODE_205 = 205;
        /**
         * 206 Partial Content
         */
        public static final int CODE_206 = 206;
        /**
         * 207 Multi-Status
         */
        public static final int CODE_207 = 207;
    }

    /**
     * @author xwc1125
     * @ClassName REDIRECT
     * @Description 重定向（3字头）
     * @date 2016年2月24日 下午6:27:01
     */
    public class REDIRECT {
        /**
         * 300 Multiple Choices
         */
        public static final int CODE_300 = 300;
        /**
         * 301 Moved Permanently
         */
        public static final int CODE_301 = 301;
        /**
         * 302 Move temporarily
         */
        public static final int CODE_302 = 302;
        /**
         * 303 See Other
         */
        public static final int CODE_303 = 303;
        /**
         * 304 Not Modified
         */
        public static final int CODE_304 = 304;
        /**
         * 305 Use Proxy
         */
        public static final int CODE_305 = 305;
        /**
         * 306 Switch Proxy
         */
        public static final int CODE_306 = 306;
        /**
         * 307 Temporary Redirect
         */
        public static final int CODE_307 = 307;
    }

    /**
     * @author xwc1125
     * @ClassName FAIL_REQUEST
     * @Description 请求错误（4字头）
     * @date 2016年2月24日 下午6:29:40
     */
    public class FAIL_REQUEST {
        /**
         * 400 Bad Request
         */
        public static final int CODE_400 = 400;
        /**
         * 401 Unauthorized
         */
        public static final int CODE_401 = 401;
        /**
         * 402 Payment Required
         */
        public static final int CODE_402 = 402;
        /**
         * 403 Forbidden
         */
        public static final int CODE_403 = 403;
        /**
         * 404 Not Found
         */
        public static final int CODE_404 = 404;
        /**
         * 405 Method Not Allowed
         */
        public static final int CODE_405 = 405;
        /**
         * 406 Not Acceptable
         */
        public static final int CODE_406 = 406;
        /**
         * 407 Proxy Authentication Required
         */
        public static final int CODE_407 = 407;
        /**
         * 408 Request Timeout
         */
        public static final int CODE_408 = 408;
        /**
         * 409 Conflict
         */
        public static final int CODE_409 = 409;
        /**
         * 410 Gone
         */
        public static final int CODE_410 = 410;
        /**
         * 411 Length Required
         */
        public static final int CODE_411 = 411;
        /**
         * 412 Precondition Failed
         */
        public static final int CODE_412 = 412;
        /**
         * 413 Request Entity Too Large
         */
        public static final int CODE_413 = 413;
        /**
         * 414 Request-URI Too Long
         */
        public static final int CODE_414 = 414;
        /**
         * 415 Unsupported Media Type
         */
        public static final int CODE_415 = 415;
        /**
         * 416 Requested Range Not Satisfiable
         */
        public static final int CODE_416 = 416;
        /**
         * 417 Expectation Failed
         */
        public static final int CODE_417 = 417;
        /**
         * 418 Paras Error
         */
        public static final int CODE_418 = 418;
        /**
         * 419 Apikey is null
         */
        public static final int CODE_419 = 419;
        /**
         * 421There are too many connections from your internet address
         */
        public static final int CODE_421 = 421;
        /**
         * 422 Unprocessable Entity
         */
        public static final int CODE_422 = 422;
        /**
         * 423 Locked
         */
        public static final int CODE_423 = 423;
        /**
         * 424 Failed Dependency
         */
        public static final int CODE_424 = 424;
        /**
         * 425 Unordered Collection
         */
        public static final int CODE_425 = 425;
        /**
         * 426 Upgrade Required
         */
        public static final int CODE_426 = 426;
        /**
         * 449 Retry With
         */
        public static final int CODE_449 = 449;
    }

    /**
     * @author xwc1125
     * @ClassName FAIL_SERVIER
     * @Description 服务器错误（5、6字头）
     * @date 2016年2月24日 下午6:36:14
     */
    public class FAIL_SERVIER {
        /**
         * 500 Internal Server Error
         */
        public static final int CODE_500 = 500;
        /**
         * 501 Not Implemented
         */
        public static final int CODE_501 = 501;
        /**
         * 502 Bad Gateway
         */
        public static final int CODE_502 = 502;
        /**
         * 503 Service Unavailable
         */
        public static final int CODE_503 = 503;
        /**
         * 504 Gateway Timeout
         */
        public static final int CODE_504 = 504;
        /**
         * 505 HTTP Version Not Supported
         */
        public static final int CODE_505 = 505;
        /**
         * 506 Variant Also Negotiates
         */
        public static final int CODE_506 = 506;
        /**
         * 507 Insufficient Storage
         */
        public static final int CODE_507 = 507;
        /**
         * 509 Bandwidth Limit Exceeded
         */
        public static final int CODE_509 = 509;
        /**
         * 510 Not Extended
         */
        public static final int CODE_510 = 510;
        /**
         * 600 Unparseable Response Headers
         */
        public static final int CODE_600 = 600;
    }
}
