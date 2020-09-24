package fairy.easy.httpcanary.util;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class PackageBean implements Serializable {

    private Drawable icon;
    private String appName;

    public PackageBean(Drawable icon, String appName) {
        this.icon = icon;
        this.appName = appName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
