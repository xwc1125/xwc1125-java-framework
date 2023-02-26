package com.xwc1125.framework.protocol.wrapper;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;

/**
 * Description: 响应封包
 * </p>
 *
 * @Author: xwc1125
 * @Date: 2019-03-25 09:40:33
 * @Copyright Copyright@2019
 */
public class EncryptionResponseWrapper extends HttpServletResponseWrapper {

    private final static String charsetName = "UTF-8";
    private ByteArrayOutputStream buffer = null;

    private ServletOutputStream out = null;

    private PrintWriter writer = null;


    public EncryptionResponseWrapper(HttpServletResponse response) throws IOException {
        super(response);

        buffer = new ByteArrayOutputStream();
        out = new WapperedOutputStream(buffer);
        writer = new PrintWriter(new OutputStreamWriter(buffer, charsetName));
    }

    //重载父类获取outputstream的方法
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return out;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return writer;
    }

    @Override
    public void flushBuffer() throws IOException {
        if (out != null) {
            out.flush();
            out.close();
        }
        if (writer != null) {
            writer.flush();
            writer.close();
        }
    }

    @Override
    public void reset() {
        buffer.reset();
    }

    public String getResponseData() throws IOException {
        flushBuffer();//将out、writer中的数据强制输出到WapperedResponse的buffer里面，否则取不到数据
        byte[] bytes = buffer.toByteArray();
        try {
            return new String(bytes, charsetName);
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    //内部类，对ServletOutputStream进行包装，指定输出流的输出端
    private class WapperedOutputStream extends ServletOutputStream {
        private ByteArrayOutputStream bos = null;

        public WapperedOutputStream(ByteArrayOutputStream stream) throws IOException {
            bos = stream;
        }

        //将指定字节写入输出流bos
        @Override
        public void write(int b) throws IOException {
            bos.write(b);
        }

        @Override
        public boolean isReady() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void setWriteListener(WriteListener listener) {
            // TODO Auto-generated method stub

        }
    }


}
