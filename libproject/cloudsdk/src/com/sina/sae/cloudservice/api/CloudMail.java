package com.sina.sae.cloudservice.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.google.gson.JsonObject;
import com.sina.sae.cloudservice.callback.SendCallback;
import com.sina.sae.cloudservice.exception.CloudServiceException;

/**
 * 邮件操作类，对应SAE中的Mail服务
 * 
 * @author zhiyun
 */
public class CloudMail {

    private String from;
    private String smtpusername;
    private String smtpPassword;
    private String[] cc;
    private String[] to;
    private String subject;
    private String content;
    private int smtpPort = 25;
    private String contentType;
    private String smtpHost;
    private String chartset;
    private boolean tls = false;

    public CloudMail() {
    }

    /**
     * 同步方式发送邮件
     */
    public boolean send() throws CloudServiceException {
        Map<String, String> params = getParams();
        if (null == params)
            return false;
        JsonObject json = CloudClient.post(CloudClient.REST_MAIL, params, null);
        int code = json.get("code").getAsInt();
        String message = json.get("message").getAsString();
        if (0 == code && "success".equalsIgnoreCase(message)) {
            return true;
        } else {
            String errorMessage = "CloudMail.send() Error!Params:" + toString() + "  Code: " + code
                    + " message:" + message;
            Log.e("CloudService", errorMessage);
            throw new CloudServiceException(errorMessage, CloudServiceException.SERVER_ERROR);
        }
    }

    /**
     * 异步方式发送邮件
     */
    public void send(SendCallback callback) throws CloudServiceException {
        Map<String, String> params = getParams();
        if (null != params)
            CloudClient.post(CloudClient.REST_MAIL, params, callback);
    }

    private Map<String, String> getParams() {
        // 验证必填参数
        if (null == from || null == smtpusername || null == smtpPassword || null == to
                || null == subject || null == content || null == smtpHost) {
            return null;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("from", from);
        params.put("smtpUsername", smtpusername);
        params.put("smtpPassword", smtpPassword);
        String t = Arrays.toString(to);
        t = t.substring(1, t.length() - 1).replace(", ", ";");
        params.put("to", t);
        params.put("subject", subject);
        params.put("content", content);
        params.put("smtpHost", smtpHost);
        params.put("smtpPort", smtpPort + "");
        if (cc != null) {
            String c = Arrays.toString(cc);
            c = c.substring(1, c.length() - 1).replace(", ", ";");
            params.put("cc", c);
        }
        if (contentType != null)
            params.put("contentType", contentType);

        if (chartset != null)
            params.put("chartset", chartset);
        params.put("tls", tls + "");
        return params;
    }

    /**
     * 设置邮件发送方地址
     * 
     * @param from
     *            发送方邮件地址
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * 设置邮件接收方地址(可设置多个)
     * 
     * @param to
     *            邮件接收方地址
     */
    public void setTo(String[] to) {
        this.to = to;
    }

    /**
     * 设置抄送人地址(可设置多个)
     * 
     * @param cc
     *            抄送地址
     */
    public void setCc(String[] cc) {
        this.cc = cc;
    }

    /**
     * 设置smtpHost
     * 
     * @param smtpHost
     */
    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    /**
     * 设置smtpPort
     * 
     * @param smtpPort
     *            默认为25
     */
    public void setSmtpPort(Integer smtpPort) {
        this.smtpPort = smtpPort;
    }

    /**
     * 设置smtpUsername
     * 
     * @param smtpUsername
     */
    public void setSmtpUsername(String smtpUsername) {
        this.smtpusername = smtpUsername;
    }

    /**
     * 设置smtpUsername
     * 
     * @param smtpPassword
     */
    public void setSmtpPassword(String smtpPassword) {
        this.smtpPassword = smtpPassword;
    }

    /**
     * 设置邮件主题 主题内容必须小于256B
     * 
     * @param subject
     *            邮件主题
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * 设置邮件正文 邮件整体大小必须小于1M(包括标题、内容、附件)
     * 
     * @param content
     *            邮件正文
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 设置邮件正文展现方式
     * 
     * @param contentType
     *            邮件正文展现方式 "TEXT"|"HTML" 默认为TEXT
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * 邮件字符集
     * 
     * @param chartset
     */
    public void setChartset(String chartset) {
        this.chartset = chartset;
    }

    public void setTls(boolean tls) {
        this.tls = tls;
    }

    @Override
    public String toString() {
        return "CloudMail [from=" + from + ", smtpusername=" + smtpusername + ", smtpPassword="
                + smtpPassword + ", cc=" + Arrays.toString(cc) + ", to=" + Arrays.toString(to)
                + ", subject=" + subject + ", content=" + content + ",smtpPort=" + smtpPort
                + ", contentType=" + contentType + ", smtpHost=" + smtpHost + ", chartset="
                + chartset + ", tls=" + tls + "]";
    }

}
