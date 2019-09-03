package fairy.easy.httpcanary.preview;

import android.os.Bundle;
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

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import fairy.easy.httpcanary.HttpCanary;
import fairy.easy.httpcanary.R;


public class HarDetailActivity extends AppCompatActivity {


    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.http_canary_activity_scrolling);
        linearLayout = findViewById(R.id.http_canary_ll_detailLayout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        try {
            initHarLog(getIntent().getIntExtra("pos", -1));
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    public void initHarLog(int pos) {
        HarLog harLog = HttpCanary.getHttpCanaryFactory().getProxy().getHar().getLog();
        HarEntry harEntry = harLog.getEntries().get(pos);

        HarRequest harRequest = harEntry.getRequest();
        HarResponse harResponse = harEntry.getResponse();

        addItem("Overview");
        addItem("URL", harRequest.getUrl());
        setTitle(harRequest.getUrl());
        addItem("Method", harRequest.getMethod());
        addItem("Code", harResponse.getStatus() + "");
        addItem("TotalTime", harEntry.getTime() + "ms");
        addItem("Size", harResponse.getBodySize() + "Bytes");

        if (harRequest.getQueryString().size() > 0) {
            addItem("Request Query");
            for (HarNameValuePair pair : harRequest.getQueryString()) {
                addItem(pair.getName(), pair.getDecodeValue());
            }
        }

        addItem("Request Header");
        for (HarNameValuePair pair : harRequest.getHeaders()) {
            // 不显示cookie
            if (!pair.getName().equals("Cookie")) {
                addItem(pair.getName(), pair.getDecodeValue());
            }
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

//            if (harRequest.getPostData().getParams() != null
//                    && harRequest.getPostData().getParams().size() > 0) {
//                addItem("Request PostData");
//
//                for (HarPostDataParam pair : harRequest.getPostData().getParams()) {
//                    addItem(pair.getName(), pair.getValue());
//                }
//            }
        }

        addItem("Response Header");
        for (HarNameValuePair pair : harResponse.getHeaders()) {
            if (!pair.getName().equals("Cookie")) {
                addItem(pair.getName(), pair.getDecodeValue());
            }
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

    public void addItem(String title, final String value) {
        View view = LayoutInflater.from(this).inflate(R.layout.http_canary_item_detail, null);

        TextView textView = view.findViewById(R.id.http_canary_tv_title);
        textView.setText(title);

        TextView valueTextView = view.findViewById(R.id.http_canary_tv_value);
        valueTextView.setText(value);

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

    public void addItem(String cateName) {
        View view = LayoutInflater.from(this).inflate(R.layout.http_canary_item_cate, null);
        TextView textView = view.findViewById(R.id.http_canary_tv_catetitle);
        textView.setText(cateName);
        linearLayout.addView(view);
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
