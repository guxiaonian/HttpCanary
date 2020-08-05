package fairy.easy.httpcanary.util;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class PackageBean implements Serializable {
    private String appName;
    private Drawable icon;

    public PackageBean() {
    }

    public PackageBean(String appName, Drawable icon) {
        this.appName = appName;
        this.icon = icon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}
