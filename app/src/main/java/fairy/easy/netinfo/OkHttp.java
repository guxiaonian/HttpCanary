package fairy.easy.netinfo;

import android.util.Log;

import java.io.IOException;
import java.util.Arrays;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttp {

    public static void useOkHttp() {

        final TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };

        SSLContext sslContext;

        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (Exception e) {
            return;
        }

        String url = App.HTTPS;
        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        try {
                            session.getPeerCertificates();
                        } catch (SSLPeerUnverifiedException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                })
                .build();
        FormBody formBody = new FormBody.Builder()
                .add("username", "admin")
                .add("password", "admin")
                .add("a", "admin")
                .add("d", "admin")
                .add("b", "admin")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        final Call call = client.newCall(request);


        try {
            Response response = null;
            try {
                response = call.execute();
            } catch (IOException x) {
                x.printStackTrace();
            }

            Log.e(App.TAG, response.body().string());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
