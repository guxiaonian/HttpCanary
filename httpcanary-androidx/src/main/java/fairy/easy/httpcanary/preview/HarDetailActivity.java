package fairy.easy.httpcanary.preview;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.lightbody.bmp.core.har.HarCookie;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarLog;
import net.lightbody.bmp.core.har.HarNameValuePair;
import net.lightbody.bmp.core.har.HarPostDataParam;
import net.lightbody.bmp.core.har.HarRequest;
import net.lightbody.bmp.core.har.HarResponse;

import java.text.SimpleDateFormat;
import java.util.Locale;

import fairy.easy.httpcanary.R;


public class HarDetailActivity extends Activity {


    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.http_canary_activity_scrolling);
        linearLayout = findViewById(R.id.http_canary_ll_detailLayout);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    initHarLog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void initHarLog() {
        HarEntry harEntry = PreviewAdapter.getHarEntry();

        HarRequest harRequest = harEntry.getRequest();
        HarResponse harResponse = harEntry.getResponse();

        addItem("Overview");
        addItem("URL", harRequest.getUrl());
        addItem("ServerIP", harEntry.getServerIPAddress() + " ");
        addItem("Method", harRequest.getMethod());
        addItem("Code", harResponse.getStatus() + "");
        addItem("TotalTime", harEntry.getTime() + "ms");
        addItem("StartTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA)
                .format(harEntry.getStartedDateTime().getTime()) + "ms");
        addItem("Size", harResponse.getBodySize() + "Bytes");

        if (harRequest.getQueryString().size() > 0) {
            addItem("Request Query");
            for (HarNameValuePair pair : harRequest.getQueryString()) {
                addItem(pair.getName(), pair.getDecodeValue());
            }
        }

        addItem("Request Header");
        for (HarNameValuePair pair : harRequest.getHeaders()) {
            addItem(pair.getName(), pair.getDecodeValue());
        }

        if (harRequest.getCookies().size() > 0) {
            addItem("Request Cookies");
            for (HarCookie cookie : harRequest.getCookies()) {
                addItem(cookie.getName(), cookie.getDecodeValue());
            }
        }

        if (harRequest.getPostData() != null) {
            if (harRequest.getPostData().getText() != null
                    && harRequest.getPostData().getText().length() > 0) {
                addItem("Request Content");
                addItem("PostData", harRequest.getPostData().getText());
            }

            if (harRequest.getPostData().getParams() != null
                    && harRequest.getPostData().getParams().size() > 0) {
                addItem("Request PostData");

                for (HarPostDataParam pair : harRequest.getPostData().getParams()) {
                    addItem(pair.getName(), pair.getValue());
                }
            }
        }

        addItem("Response Header");
        for (HarNameValuePair pair : harResponse.getHeaders()) {
            addItem(pair.getName(), pair.getDecodeValue());
        }

        if (harResponse.getCookies().size() > 0) {
            addItem("Response Cookies");
            for (HarCookie cookie : harResponse.getCookies()) {
                addItem(cookie.getName(), cookie.getDecodeValue());
            }
        }

        if ((harResponse.getRedirectURL() != null && harResponse.getRedirectURL().length() > 0) ||
                (harResponse.getContent().getText() != null && harResponse.getContent().getText().length() > 0)) {
            addItem("Response Content");
        }
        if (harResponse.getRedirectURL() != null && harResponse.getRedirectURL().length() > 0) {
            addItem("RedirectURL", harResponse.getRedirectURL());
        }
        if (harResponse.getContent().getText() != null && harResponse.getContent().getText().length() > 0) {
            addItem("Content", harResponse.getContent().getText());
        }

    }

    public void addItem(final String title, final String value) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View view = LayoutInflater.from(HarDetailActivity.this).inflate(R.layout.http_canary_item_detail, null);

                TextView textView = view.findViewById(R.id.http_canary_tv_title);
                textView.setText(TextUtils.isEmpty(title) ? "placeholder" : title);

                TextView valueTextView = view.findViewById(R.id.http_canary_tv_value);
                valueTextView.setText(TextUtils.isEmpty(value) ? "placeholder" : value);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (value != null) {
                            View textEntryView = LayoutInflater.from(HarDetailActivity.this).inflate(R.layout.http_canary_alert_textview, null);
                            TextView edtInput = textEntryView.findViewById(R.id.http_canary_tv_content);
                            edtInput.setText(value);
                            new AlertDialog.Builder(HarDetailActivity.this)
                                    .setView(textEntryView)
                                    .setPositiveButton(getString(R.string.http_canary_yes), null)
                                    .show();
                        }
                    }
                });
                linearLayout.addView(view);
            }
        });
    }

    public void addItem(final String cateName) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View view = LayoutInflater.from(HarDetailActivity.this).inflate(R.layout.http_canary_item_cate, null);
                TextView textView = view.findViewById(R.id.http_canary_tv_catetitle);
                textView.setText(cateName);
                linearLayout.addView(view);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
