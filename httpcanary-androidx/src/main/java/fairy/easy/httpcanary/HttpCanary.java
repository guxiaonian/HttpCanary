package fairy.easy.httpcanary;

import android.content.Context;

import fairy.easy.httpcanary.preview.PreviewActivity;

public class HttpCanary {

    private HttpCanary() {
        throw new AssertionError();
    }

    private static HttpCanaryFactory httpCanaryFactory;

    static void install(Context context) {
        HttpModel.setEnabled(context, PreviewActivity.class, true);
        httpCanaryFactory = new HttpCanaryFactory(context);

    }

    public static HttpCanaryFactory getHttpCanaryFactory() {
        return httpCanaryFactory;
    }
}
