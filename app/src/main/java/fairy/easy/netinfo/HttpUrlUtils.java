package fairy.easy.netinfo;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by 谷闹年 on 2017/8/30.
 * 请求工具类
 */


public class HttpUrlUtils {
    /**
     * get请求
     *
     * @param getURL
     * @return
     */
    public static String readContentFromGet(String getURL) {

        URL url = getValidateURL(getURL);
        try {
            HttpsURLConnection httpsURLConnection = null;
            try {
                final StringBuffer sBuffer = new StringBuffer();
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new TrustManager[]{new TrustAllManager()}, null);

                HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

                    @Override
                    public boolean verify(String arg0, SSLSession arg1) {
                        return true;
                    }
                });

                httpsURLConnection = (HttpsURLConnection) url.openConnection();

                httpsURLConnection.setDoInput(true);
                httpsURLConnection.setDoOutput(false);
                httpsURLConnection.setRequestMethod("GET");
                httpsURLConnection.setConnectTimeout(5000);
                httpsURLConnection.setReadTimeout(5000);
                httpsURLConnection.connect();
                byte[] buf = new byte[1024];

                InputStream inStream = httpsURLConnection.getInputStream();

                for (int n; (n = inStream.read(buf)) != -1; ) {

                    sBuffer.append(new String(buf, 0, n, "UTF-8"));

                }

                inStream.close();

                int responseCode = httpsURLConnection.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    return sBuffer.toString();
                } else {
                    return responseCode + "";
                }

            } catch (Exception e) {
                return e.toString();
            } finally {
                if (httpsURLConnection != null) {
                    httpsURLConnection.disconnect();
                }
            }

        } catch (Exception e) {
            return "error:" + e.toString();
        }

    }

    /**
     * 数据流
     *
     * @param inputStream
     * @return
     */
    private static String dealResponseResult(InputStream inputStream) {
        String resultData;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultData = new String(byteArrayOutputStream.toByteArray());
        return resultData;
    }

    /**
     * X509证书
     */
    private static class TrustAllManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1)
                throws CertificateException {
            // TODO Auto-generated method stub
        }

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1)
                throws CertificateException {
            // TODO Auto-generated method stub
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            // TODO Auto-generated method stub
            return null;
        }
    }

    /**
     * url获取
     *
     * @param urlString
     * @return
     */
    private static URL getValidateURL(String urlString) {
        try {
            return new URL(urlString);
        } catch (Exception e) {
        }
        return null;
    }


}
