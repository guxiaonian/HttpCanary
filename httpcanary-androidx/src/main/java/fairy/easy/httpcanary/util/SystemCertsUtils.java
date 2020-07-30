package fairy.easy.httpcanary.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SystemCertsUtils {

    public static void buildSystemCerts(Context context) {
        copyAssets2Files(Environment.getExternalStorageDirectory() + "/har/littleproxy-mitm.pem", context.getFilesDir().getAbsolutePath() + File.separator + "littleproxy-mitm.pem");
        prepareRoot(context);
    }

    private static void copyAssets2Files(String fileName, String path) {
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
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("SSSSSS",e.toString());
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

    private static boolean hasCert() {
        return new File("/system/etc/security/cacerts/4bb9877f.0").exists();
    }

    private static void prepareRoot(Context context) {
        if (hasCert()) {
            return;
        }
        OutputStream os = null;
        try {
            String fakeCertDir = context.getFilesDir() + "/cacerts/";
            String cmd;
            Process p = Runtime.getRuntime().exec("su -c ls");
            p.waitFor();
            if (p.exitValue() == 0) {
                p = Runtime.getRuntime().exec("su");
                os = p.getOutputStream();
                cmd = "umount /system/etc/security/cacerts;cp -pR /system/etc/security/cacerts " + context.getFilesDir() +
                        ";cp " + context.getFilesDir() + "/littleproxy-mitm.pem " + fakeCertDir + "/4bb9877f.0" +
                        ";chmod -R 755 " + fakeCertDir +
                        ";chcon -R `ls -Z /system/etc/security/cacerts | head -n1 | cut -d \" \" -f 1 ` " + fakeCertDir +
                        ";mount " + fakeCertDir + " /system/etc/security/cacerts/;exit";
                Log.d("CMD", "cmd " + cmd);
                os.write(cmd.getBytes());
                os.flush();
                p.waitFor();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SSSSSS",e.toString());
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
