package fairy.easy.httpcanary.preview;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


import net.lightbody.bmp.core.har.HarEntry;

import fairy.easy.httpcanary.AbstractParam;
import fairy.easy.httpcanary.HttpCanary;
import fairy.easy.httpcanary.R;
import fairy.easy.httpcanary.util.LifecycleCallbacksUtils;


public class PreviewActivity extends Activity {
    private PreviewAdapter previewAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.http_canary_activity_preview);
        listView = findViewById(R.id.http_canary_list);
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
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        previewAdapter.notifyDataSetChanged();
//                    }
//                });
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            Application app = (Application) getApplicationContext();
            app.registerActivityLifecycleCallbacks(lifecycleCallbacks);
        }
    }

    private final Application.ActivityLifecycleCallbacks lifecycleCallbacks = new LifecycleCallbacksUtils();

    public void notifyHarChange() {
        if (previewAdapter != null) {
            previewAdapter.notifyHarChange();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            Application app = (Application) getApplicationContext();
            app.unregisterActivityLifecycleCallbacks(lifecycleCallbacks);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpCanary.getHttpCanaryFactory().stop();
            }
        }).start();
    }

}
