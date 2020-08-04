package fairy.easy.httpcanary;

import android.content.Context;

import fairy.easy.httpcanary.preview.PreviewActivity;
import fairy.easy.httpcanary.preview.SettingActivity;

public class HttpCanary {

    private HttpCanary() {
        throw new AssertionError();
    }

    private static HttpCanaryFactory httpCanaryFactory;

    static void install(Context context) {
        HttpModel.setEnabled(context, SettingActivity.class, true);
        httpCanaryFactory = new HttpCanaryFactory(context);

    }

    public static HttpCanaryFactory getHttpCanaryFactory() {
        return httpCanaryFactory;
    }
}
