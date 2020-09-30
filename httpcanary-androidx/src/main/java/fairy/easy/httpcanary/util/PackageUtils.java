package fairy.easy.httpcanary.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PackageUtils {

    public static final String TAG = "Http---";
    private static Context context;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        PackageUtils.context = context;
    }

    private static PackageBean getTcp(String portHex) {
        String tcpResult = CommandUtils.getSingleInstance().exec("cat /proc/net/tcp |grep " + portHex,false);
        Log.e(TAG, "tcp4 start");
        if (TextUtils.isEmpty(tcpResult)) {
            Log.e(TAG, "tcp4 is null");
            return null;
        } else {
            Log.e(TAG, "tcp4 " + tcpResult);
            String[] strings = tcpResult.split("\n");
            for (String s : strings) {
                int i = s.indexOf(portHex);
                if (i == 14) {
                    String uid = s.substring(76, 82).replace(" ", "");
                    Log.e(TAG, "tcp4 uid is: " + uid);
                    PackageBean appName = getAppName(Integer.parseInt(uid));
                    Log.e(TAG, "tcp4 app is: " + appName.getAppName());
                    return appName;
                }
            }
        }
        return null;
    }

    private static PackageBean getTcp6(String portHex) {
        String tcpResult = CommandUtils.getSingleInstance().exec("cat /proc/net/tcp6 |grep " + portHex,false);
        if (TextUtils.isEmpty(tcpResult)) {
            Log.e(TAG, "tcp6 is null");
            return null;
        } else {
            Log.e(TAG, "tcp6  " + tcpResult);
            String[] strings = tcpResult.split("\n");
            for (String s : strings) {
                int i = s.indexOf(portHex);
                if (i == 38) {
                    String uid = s.substring(124, 130).replace(" ", "");
                    Log.e(TAG, "tcp6 uid is: " + uid);
                    PackageBean appName = getAppName(Integer.parseInt(uid));
                    Log.e(TAG, "tcp6 app is: " + appName.getAppName());
                    return appName;
                }
            }
        }
        return null;
    }

    /**
     * 1 传入的端口进行16位进制转换
     * 2 执行cat /proc/net/tcp6 | findstr 端口 未找到则执行cat /proc/net/tcp | findstr 端口
     * 3 找到对应的UID
     * 4 根据对应的UID找到对应的APP名
     *
     * @param port
     */
    public synchronized static PackageBean getUid(int port) {
        Log.e(TAG, "port is: " + port);
        final String portHex = ":" + Integer.toHexString(port).toUpperCase();
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        Set<Callable<PackageBean>> callables = new HashSet<>();
        callables.add(new Callable<PackageBean>() {
            @Override
            public PackageBean call() throws Exception {
                return getTcp(portHex);
            }
        });
        callables.add(new Callable<PackageBean>() {
            @Override
            public PackageBean call() throws Exception {
                return getTcp6(portHex);
            }
        });
        try {
            List<Future<PackageBean>> futures = executorService.invokeAll(callables);
            for (Future<PackageBean> future : futures) {
                try {
                    PackageBean result = future.get();
                    if (result != null) {
                        return result;
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static PackageBean getAppName(int uid) {
        PackageManager pm = context.getPackageManager();
        String[] pkgs = pm.getPackagesForUid(uid);
        if (pkgs != null) {
            try {
                ApplicationInfo appInfo = pm.getApplicationInfo(pkgs[0], PackageManager.GET_META_DATA);
                return new PackageBean(pm.getApplicationIcon(appInfo.packageName), pm.getApplicationLabel(appInfo).toString());
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
