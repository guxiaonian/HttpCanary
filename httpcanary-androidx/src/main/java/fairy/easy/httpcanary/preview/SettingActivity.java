package fairy.easy.httpcanary.preview;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.security.KeyChain;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import fairy.easy.httpcanary.HttpCanary;
import fairy.easy.httpcanary.HttpCanaryFactory;
import fairy.easy.httpcanary.R;
import fairy.easy.httpcanary.util.CommandUtils;
import fairy.easy.httpcanary.util.LifecycleCallbacksUtils;
import fairy.easy.httpcanary.util.PermissionsUtils;
import fairy.easy.httpcanary.util.ProxyUtils;
import fairy.easy.httpcanary.util.SharedPreferencesUtils;
import fairy.easy.httpcanary.util.SystemCertsUtils;

/**
 * 设置相关页面
 * 第一步 给与相关权限
 * 第二步 下载证书并安装
 * 第三步 给与su权限
 * 第四步 迁移证书
 */
public class SettingActivity extends AppCompatActivity {
    private static final int RESULT_PERMISSIONS = 1;
    private Button btnPermission;
    private Button btnDownload;
    private Button btnGenerate;
    private Button btnSu;
    private Button btnMigration;
    private Button btnGo;
    private Button btnGlobal;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.http_canary_activity_setting);
        btnPermission = findViewById(R.id.http_canary_permission_btn);
        btnDownload = findViewById(R.id.http_canary_download_btn);
        btnSu = findViewById(R.id.http_canary_su_btn);
        btnMigration = findViewById(R.id.http_canary_etc_btn);
        btnGo = findViewById(R.id.http_canary_go_btn);
        btnGlobal = findViewById(R.id.http_canary_global_btn);
        btnGenerate = findViewById(R.id.http_canary_generate_btn);
        if (PermissionsUtils.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            btnPermission.setEnabled(false);
            btnGenerate.setEnabled(true);
        }

        btnPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });

        if ((Boolean) SharedPreferencesUtils.get(this, "wri_net", false)) {
            btnGenerate.setEnabled(false);
            btnDownload.setEnabled(true);
        }

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertPem();
            }
        });

        if ((Boolean) SharedPreferencesUtils.get(this, "isInstallNewCert", false)) {
            btnDownload.setEnabled(false);
            btnGlobal.setEnabled(true);
            btnGo.setEnabled(true);

        }

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                installCert();
            }
        });

        btnGlobal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent();
                        intent.setClassName("com.android.settings", "com.android.settings.Settings$WifiSettingsActivity");
                        startActivity(intent);
                    }
                };
                new AlertDialog.Builder(SettingActivity.this)
                        .setTitle("Global proxy setting")
                        .setMessage("IP: 127.0.0.1 \nPORT: " + HttpCanary.getHttpCanaryFactory().getProxyPort())
                        .setPositiveButton(getString(R.string.http_canary_yes), okListener)
                        .show();
            }

        });

        if ((Boolean) SharedPreferencesUtils.get(this, "wri_ps", false)) {
            btnSu.setEnabled(false);
            btnMigration.setEnabled(true);
        }

        btnSu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = CommandUtils.getSingleInstance().exec("ps",true);
                if (!TextUtils.isEmpty(result)) {
                    SharedPreferencesUtils.put(getApplicationContext(), "wri_ps", true);
                    btnSu.setEnabled(false);
                    btnMigration.setEnabled(true);
                }
            }
        });

        if (SystemCertsUtils.hasCert()) {
            btnMigration.setEnabled(false);
        }

        btnMigration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommandUtils.getSingleInstance().exec("ps",true);
                boolean result = SystemCertsUtils.buildSystemCerts(getApplicationContext());
                if (result) {
                    btnMigration.setEnabled(false);
                }
            }
        });

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProxyUtils.setProxyLollipop(getApplicationContext(), "127.0.0.1", HttpCanary.getHttpCanaryFactory().getProxyPort());
                startActivity(new Intent(getApplicationContext(), PreviewActivity.class));
            }
        });
    }


    private void insertPem() {
        progressDialog = ProgressDialog.show(this, null, "save...");
        HttpCanary.getHttpCanaryFactory().initProxy(new HttpCanaryFactory.CallBack() {
            @Override
            public void onResult() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        SharedPreferencesUtils.put(getApplicationContext(), "wri_net", true);
                        btnGenerate.setEnabled(false);
                        btnDownload.setEnabled(true);
                    }
                });
            }
        });


    }

    private void installCert() {
        final String CERTIFICATE_RESOURCE = Environment.getExternalStorageDirectory() + "/har/littleproxy-mitm.pem";
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
        runnable.run();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK) {
                SharedPreferencesUtils.put(this, "isInstallNewCert", true);
                Toast.makeText(this, "Installation Success", Toast.LENGTH_LONG).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    Application app = (Application) getApplicationContext();
                    app.registerActivityLifecycleCallbacks(new LifecycleCallbacksUtils());
                }
                btnDownload.setEnabled(false);
                btnGlobal.setEnabled(true);
                btnGo.setEnabled(true);
            } else {
                Toast.makeText(this, "Installation Fail", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void checkPermission() {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RESULT_PERMISSIONS) {
            int i = 0;
            for (int g : grantResults) {
                if (g != PackageManager.PERMISSION_GRANTED) {
                    i++;
                    Toast.makeText(this, "Please give permission to enter the APP", Toast.LENGTH_LONG).show();
                }
            }
            if (i == 0) {
                btnGenerate.setEnabled(true);
                btnPermission.setEnabled(false);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}