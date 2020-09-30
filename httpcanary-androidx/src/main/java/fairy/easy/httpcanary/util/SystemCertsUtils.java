package fairy.easy.httpcanary.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SystemCertsUtils {

    public static boolean buildSystemCerts(Context context) {
//        boolean copy = copyAssets2Files(Environment.getExternalStorageDirectory() + "/har/littleproxy-mitm.pem", context.getFilesDir().getAbsolutePath() + File.separator + "littleproxy-mitm.pem");
//        if (!copy) {
//            return false;
//        }
//        Log.e("SSSS","copy success");
        return prepareRoot(context);
    }

    private static boolean copyAssets2Files(String fileName, String path) {
        FileInputStream inputStream = null;
        FileOutputStream fos = null;
        try {
            inputStream = new FileInputStream(new File(fileName));
            File file = new File(path);
            if (!file.exists() || file.length() == 0) {
                fos = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                while ((len = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                fos.flush();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean hasCert() {
        return new File("/system/etc/security/cacerts/4bb9877f.0").exists();
    }

    public static boolean hasCertApp(Context context) {
        return new File(context.getFilesDir() + "/cacerts/4bb9877f.0").exists();
    }

    private static boolean prepareRoot(Context context) {
        String fakeCertDir = context.getFilesDir() + "/cacerts/";
        String cmd = "umount /system/etc/security/cacerts;cp -pR /system/etc/security/cacerts " + context.getFilesDir() +
                ";cp /data/misc/user/0/cacerts-added/4bb9877f.0 " + fakeCertDir +
                ";chmod -R 755 " + fakeCertDir +
                ";chcon -R `ls -Z /system/etc/security/cacerts | head -n1 | cut -d \" \" -f 1 ` " + fakeCertDir +
                ";mount " + fakeCertDir + " /system/etc/security/cacerts/";
        Log.e("SSSSS", cmd);
        CommandUtils.getSingleInstance().exec(cmd,true);
        return true;
    }
}
