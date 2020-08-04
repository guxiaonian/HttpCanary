package fairy.easy.httpcanary.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

public class PackageUtils {

    private static Context context;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        PackageUtils.context = context;
    }

    private static String getTcp(String portHex) {
        String tcpResult = CommandUtils.getSingleInstance().exec("cat /proc/net/tcp |grep " + portHex);
        if (tcpResult == null) {
            Log.e("SSSSSSS", "tcp4 is null");
            return null;
        } else {
            Log.e("SSSSSSSStcp4", tcpResult);
            String[] strings = tcpResult.split("\n");
            for (String s : strings) {
                int i = s.indexOf(portHex);
                if (i == 14) {
                    String uid = s.substring(76, 82).replace(" ", "");
                    Log.e("SSSSSSS", "uid is: " + uid);
                    String appName = getAppName(Integer.parseInt(uid));
                    Log.e("SSSSSSS", "app is: " + appName);
                    return appName;
                }
                Log.e("SSSSSSS", "tcp4 strings null");
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
    public synchronized static String getUid(int port) {
        Log.e("SSSSSSS", "port is: " + port);
        String portHex = ":" + Integer.toHexString(port).toUpperCase();
        String tcpResult = CommandUtils.getSingleInstance().exec("cat /proc/net/tcp6 |grep " + portHex);
        if (tcpResult != null) {
            Log.e("SSSSSSSStcp6", tcpResult);
            String[] strings = tcpResult.split("\n");
            for (String s : strings) {
                int i = s.indexOf(portHex);
                if (i == 38) {
                    String uid = s.substring(124, 130).replace(" ", "");
                    Log.e("SSSSSSS", "uid is: " + uid);
                    String appName = getAppName(Integer.parseInt(uid));
                    Log.e("SSSSSSS", "app is: " + appName);
                    if (!uid.equals("0") && !TextUtils.isEmpty(appName)) {
                        return appName;
                    }
                }
            }
            Log.e("SSSSSSS", "tcp6 strings null");
        }
        return getTcp(portHex);
    }

    private static String getAppName(int uid) {
        PackageManager pm = context.getPackageManager();
        String[] pkgs = pm.getPackagesForUid(uid);
        if (pkgs != null) {
            try {
                return pm.getApplicationLabel(pm.getApplicationInfo(pkgs[0], PackageManager.GET_META_DATA)).toString();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

}
