package fairy.easy.netinfo;


import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpsURLConnection {

    public static void useHttpsURLConnection() {

        URL url = getValidateURL(App.HTTPS);
        try {
            javax.net.ssl.HttpsURLConnection httpsURLConnection = null;
            try {
                final StringBuffer sBuffer = new StringBuffer();
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new TrustManager[]{new TrustAllManager()}, null);

                javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
                javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

                    @Override
                    public boolean verify(String arg0, SSLSession arg1) {
                        return true;
                    }
                });

                httpsURLConnection = (javax.net.ssl.HttpsURLConnection) url.openConnection();

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

                if (responseCode == javax.net.ssl.HttpsURLConnection.HTTP_OK) {
                    Log.e(App.TAG, sBuffer.toString());
                } else {
                    Log.e(App.TAG, responseCode + "");
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpsURLConnection != null) {
                    httpsURLConnection.disconnect();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

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

    private static URL getValidateURL(String urlString) {
        try {
            return new URL(urlString);
        } catch (Exception e) {
        }
        return null;
    }


}
