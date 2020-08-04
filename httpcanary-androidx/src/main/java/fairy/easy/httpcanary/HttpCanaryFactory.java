package fairy.easy.httpcanary;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.proxy.CaptureType;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import fairy.easy.httpcanary.util.LifecycleCallbacksUtils;
import fairy.easy.httpcanary.util.ProxyUtils;
import fairy.easy.httpcanary.util.SharedPreferencesUtils;


public class HttpCanaryFactory {
    private final Context mContext;
    private Boolean isInitProxy = false;
    private int proxyPort = 8888;
    public BrowserMobProxy proxy;

    public HttpCanaryFactory(Context mContext) {
        this.mContext = mContext;
        if ((boolean) SharedPreferencesUtils.get(mContext, "isInstallNewCert", false)) {
            initProxy(null);
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH){
                Application app= (Application) mContext.getApplicationContext();
                app.registerActivityLifecycleCallbacks(new LifecycleCallbacksUtils());
            }
        }
    }


    public BrowserMobProxy getProxy() {
        return proxy;
    }

    public Boolean getInitProxy() {
        return isInitProxy;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public static void forceMkdir(File directory) throws IOException {
        if (directory.exists()) {
            if (!directory.isDirectory()) {
                String message =
                        "File "
                                + directory
                                + " exists and is "
                                + "not a directory. Unable to create directory.";
                throw new IOException(message);
            }
        } else {
            if (!directory.mkdirs()) {
                // Double-check that some other thread or process hasn't made
                // the directory in the background
                if (!directory.isDirectory()) {
                    String message =
                            "Unable to create directory " + directory;
                    throw new IOException(message);
                }
            }
        }
    }


    public void initProxy(final CallBack callBack) {
        try {
            forceMkdir(new File(Environment.getExternalStorageDirectory() + "/har"));
        } catch (IOException e) {
            // har文件不存在
        }
        ThreadPoolUtils.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                startProxy();
                Intent intent = new Intent();
                intent.setAction("proxyfinished");
                mContext.sendBroadcast(intent);
                if (callBack != null) {
                    callBack.onResult();
                }
            }
        });
    }


    public interface CallBack {
        void onResult();
    }

    private void startProxy() {
        try {
            proxy = new BrowserMobProxyServer();
            proxy.setTrustAllServers(true);
            proxy.start(proxyPort);
        } catch (Exception e) {
            Random rand = new Random();
            int randNum = rand.nextInt(1000) + 8000;
            proxyPort = randNum;
            proxy = new BrowserMobProxyServer();
            proxy.setTrustAllServers(true);
            proxy.start(randNum);
        }
        proxy.enableHarCaptureTypes(CaptureType.REQUEST_HEADERS, CaptureType.REQUEST_COOKIES,
                CaptureType.REQUEST_CONTENT,CaptureType.REQUEST_BINARY_CONTENT, CaptureType.RESPONSE_HEADERS,CaptureType.RESPONSE_COOKIES,
                CaptureType.RESPONSE_CONTENT,CaptureType.RESPONSE_BINARY_CONTENT);
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
                .format(new Date(System.currentTimeMillis()));
        proxy.newHar(time);
        isInitProxy = true;
    }
}
