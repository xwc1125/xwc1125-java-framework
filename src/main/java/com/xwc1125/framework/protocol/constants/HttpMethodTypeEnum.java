package com.xwc1125.framework.protocol.constants;

/**
 * @Description: 网络请求方式
 * @Author: xwc1125
 * @Date: 2019-03-24 22:24
 * @Copyright Copyright@2019
 */
public enum HttpMethodTypeEnum {
    GET {
        @Override
        public String value() {
            return "get:";
        }
    },
    POST {
        @Override
        public String value() {
            return "post:";
        }
    },
    DELETE {
        @Override
        public String value() {
            return "delete:";
        }
    },
    PUT {
        @Override
        public String value() {
            return "put:";
        }
    };

    public abstract String value();

}
