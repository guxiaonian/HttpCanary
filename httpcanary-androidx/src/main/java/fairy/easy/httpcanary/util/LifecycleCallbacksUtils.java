package fairy.easy.httpcanary.util;

import android.app.Activity;
import android.app.Application;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import fairy.easy.httpcanary.HttpCanary;


public class LifecycleCallbacksUtils implements Application.ActivityLifecycleCallbacks {


    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        iteratorView(decorView);
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    private void iteratorView(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            if (vg instanceof WebView) {
                hookWebView((WebView) vg);
            } else {
                int childCount = vg.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    iteratorView(vg.getChildAt(i));
                }
            }
        }

    }

    private void hookWebView(final WebView webView) {
        if(!HttpCanary.getHttpCanaryFactory().getInitProxy()){
            return;
        }
        if (webView == null) {
            return;
        }
        if (webView.getUrl() == null) {
            return;
        }
        WebSettings webSettings = webView.getSettings();
        if (!webSettings.getJavaScriptEnabled()) {
            webSettings.setJavaScriptEnabled(true);
        }
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
                super.onReceivedSslError(view, handler, error);
            }
        });

        webView.post(new Runnable() {
            @Override
            public void run() {
                if (ProxyUtils.setProxy(webView, "127.0.0.1", HttpCanary.getHttpCanaryFactory().getProxy().getPort())) {
                    webView.loadUrl(webView.getUrl());
                }
            }
        });

    }
}
