package fairy.easy.httpcanary.preview;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.security.KeyChain;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import fairy.easy.httpcanary.HttpCanary;
import fairy.easy.httpcanary.HttpCanaryFactory;
import fairy.easy.httpcanary.R;
import fairy.easy.httpcanary.util.LifecycleCallbacksUtils;
import fairy.easy.httpcanary.util.PackageUtils;
import fairy.easy.httpcanary.util.PermissionsUtils;
import fairy.easy.httpcanary.util.ProxyUtils;
import fairy.easy.httpcanary.util.SharedPreferencesUtils;
import fairy.easy.httpcanary.util.SystemCertsUtils;


public class PreviewActivity extends AppCompatActivity {
    private static final int RESULT_PERMISSIONS = 1;
    private PreviewAdapter previewAdapter;
    private ListView listView;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.http_canary_activity_preview);
        checkPermission();
        PackageUtils.setContext(getApplicationContext());
        listView = findViewById(R.id.http_canary_list);
        editText = findViewById(R.id.http_canary_et);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence)) {
                    previewAdapter.getFilter().filter(charSequence);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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
    }


    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new ArrayList<>();
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (permissions.size() != 0) {
                String[] permissionArray = new String[permissions.size()];
                permissions.toArray(permissionArray);
                requestPermissions(permissionArray, RESULT_PERMISSIONS);
            }
        }
    }

    private void insertPem() {
        if (PermissionsUtils.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            if (!(Boolean) SharedPreferencesUtils.get(this, "wri_net", false)) {
                HttpCanary.getHttpCanaryFactory().initProxy(new HttpCanaryFactory.CallBack() {
                    @Override
                    public void onResult() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SharedPreferencesUtils.put(getApplicationContext(), "wri_net", true);
                                installCert();
                            }
                        });
                    }
                });
            } else {
                installCert();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        previewAdapter.setList();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                previewAdapter.notifyDataSetChanged();
            }
        });
        insertPem();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK) {
                SharedPreferencesUtils.put(this, "isInstallNewCert", true);
                Toast.makeText(this, "Successful installation", Toast.LENGTH_LONG).show();
                ProxyUtils.setProxyLollipop(this, "127.0.0.1", HttpCanary.getHttpCanaryFactory().getProxyPort());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    Application app = (Application) getApplicationContext();
                    app.registerActivityLifecycleCallbacks(new LifecycleCallbacksUtils());
                }
//                SystemCertsUtils.buildSystemCerts(getApplicationContext());
            } else {
                Toast.makeText(this, "installation failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void installCert() {
        final String CERTIFICATE_RESOURCE = Environment.getExternalStorageDirectory() + "/har/littleproxy-mitm.pem";
        Boolean isInstallCert = (Boolean) SharedPreferencesUtils.get(this.getApplicationContext(), "isInstallNewCert", false);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] keychainBytes;

                    FileInputStream is = null;
                    try {
                        is = new FileInputStream(CERTIFICATE_RESOURCE);
                        keychainBytes = new byte[is.available()];
                        is.read(keychainBytes);
                    } finally {
                        if (is != null) {
                            is.close();
                        }
                    }

                    Intent intent = KeyChain.createInstallIntent();
                    intent.putExtra(KeyChain.EXTRA_CERTIFICATE, keychainBytes);
                    intent.putExtra(KeyChain.EXTRA_NAME, "Network CA Certificate For Https");
                    startActivityForResult(intent, 3);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        if (!isInstallCert) {
            Toast.makeText(this, "Certificate must be installed to achieve HTTPS packet capture", Toast.LENGTH_LONG).show();
            runnable.run();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RESULT_PERMISSIONS) {
            for (int g : grantResults) {
                if (g != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Please give permission to enter the APP", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
//            insertPem();

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    public void notifyHarChange() {
        if (previewAdapter != null) {
            previewAdapter.notifyHarChange();
        }
    }

}
