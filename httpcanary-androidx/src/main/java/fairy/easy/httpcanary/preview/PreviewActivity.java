package fairy.easy.httpcanary.preview;

import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;


import androidx.appcompat.app.AppCompatActivity;

import net.lightbody.bmp.core.har.HarEntry;

import fairy.easy.httpcanary.AbstractParam;
import fairy.easy.httpcanary.HttpCanary;
import fairy.easy.httpcanary.R;
import fairy.easy.httpcanary.util.LifecycleCallbacksUtils;


public class PreviewActivity extends AppCompatActivity {
    private PreviewAdapter previewAdapter;
    private ListView listView;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.http_canary_activity_preview);
        listView = findViewById(R.id.http_canary_list);
//        editText = findViewById(R.id.http_canary_et);
//        editText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (!TextUtils.isEmpty(charSequence)) {
//                    previewAdapter.getFilter().filter(charSequence);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
        setTitle(String.format(getResources().getString(R.string.http_canary_title), getApplicationContext().getPackageName()));
        findViewById(R.id.http_canary_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (HttpCanary.getHttpCanaryFactory().getProxy() == null) {
                            return;
                        }
                        HttpCanary.getHttpCanaryFactory().getProxy().getHar().getLog().clearAllEntries();
                        notifyHarChange();
                    }
                };
                new AlertDialog.Builder(PreviewActivity.this)
                        .setTitle(getString(R.string.http_canary_delete_all))
                        .setMessage(getString(R.string.http_canary_message))
                        .setPositiveButton(getString(R.string.http_canary_yes), okListener)
                        .setNegativeButton(getString(R.string.http_canary_no), null)
                        .show();
            }
        });
        previewAdapter = new PreviewAdapter(this);
        listView.setAdapter(previewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                previewAdapter.onItemClick(parent, view, position, id);
            }
        });

        HttpCanary.getHttpCanaryFactory().initProxy(null, new AbstractParam() {
            @Override
            public void getParam(HarEntry harEntry) {
                previewAdapter.addList(harEntry);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        previewAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            Application app = (Application) getApplicationContext();
            app.registerActivityLifecycleCallbacks(new LifecycleCallbacksUtils());
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
//        previewAdapter.setList();
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                previewAdapter.notifyDataSetChanged();
//            }
//        });

    }

    public void notifyHarChange() {
        if (previewAdapter != null) {
            previewAdapter.notifyHarChange();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HttpCanary.getHttpCanaryFactory().stop();
    }

}
