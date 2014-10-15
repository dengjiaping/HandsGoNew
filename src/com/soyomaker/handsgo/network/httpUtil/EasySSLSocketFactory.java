package com.soyomaker.handsgo.network.httpUtil;

import java.io.IOException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLSocketFactory;

public class EasySSLSocketFactory extends SSLSocketFactory {

    protected SSLContext Cur_SSL_Context;

    public EasySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException,
            KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        super(truststore);
        try {
            Cur_SSL_Context = SSLContext.getInstance("TLS");
        } catch (Exception e) {
            Cur_SSL_Context = SSLContext.getInstance("LLS");
        }
        Cur_SSL_Context.init(null, new TrustManager[] { new EasyX509TrustManager() }, null);
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
            throws IOException {
        return Cur_SSL_Context.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    @Override
    public Socket createSocket() throws IOException {
        return Cur_SSL_Context.getSocketFactory().createSocket();
    }

    private static class EasyX509TrustManager implements X509TrustManager {

        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {

        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {

        }

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

    }
}