package fairy.easy.httpcanary.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;



public class PermissionsUtils {

    public static boolean checkPermission(Context context, String... permissions) {
        if (permissions == null || permissions.length < 1) {
            return true;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        for (String permission : permissions) {
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
