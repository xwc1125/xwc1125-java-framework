package com.xwc1125.framework.protocol.wrapper;

import com.xwc1125.common.entity.RequestDataObj;
import com.xwc1125.common.crypto.sign.SignalUtils;
import com.xwc1125.framework.protocol.utils.StreamUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.TreeMap;

/**
 * Description: 请求封包
 * </p>
 *
 * @Author: xwc1125
 * @Date: 2019-03-25 09:36:42
 * @Copyright Copyright@2019
 */
public class DecryptionRequestWrapper extends HttpServletRequestWrapper {

    private byte[] requestBody = new byte[0];
    private TreeMap<String, String> requestMap;
    private RequestDataObj requestDataObj;

    public DecryptionRequestWrapper(HttpServletRequest request, boolean isToMap) {
        super(request);
        if (isToMap) {
            requestMap = SignalUtils.CalculateSign(request);
        } else {
            try {
                requestBody = StreamUtils.copyToByteArray(request.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(requestBody);
        return new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return bais.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener listener) {

            }
        };
    }

    public TreeMap<String, String> getRequestMap() {
        return requestMap;
    }

    public void setRequestMap(TreeMap<String, String> requestMap) {
        this.requestMap = requestMap;
    }

    public RequestDataObj getRequestDataObj() {
        return requestDataObj;
    }

    public void setRequestDataObj(RequestDataObj requestDataObj) {
        this.requestDataObj = requestDataObj;
    }

    public String getRequestData() {
        return new String(requestBody);
    }

    public void setRequestData(String requestData) {
        this.requestBody = requestData.getBytes();
    }
}
