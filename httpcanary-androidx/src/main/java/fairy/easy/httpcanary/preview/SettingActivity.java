package fairy.easy.httpcanary.preview;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import fairy.easy.httpcanary.R;
import fairy.easy.httpcanary.util.PermissionsUtils;

/**
 * 设置相关页面
 * 第一步 给与相关权限
 * 第二步 下载证书并安装
 * 第三步 给与su权限
 * 第四步 迁移证书
 */
public class SettingActivity extends AppCompatActivity {

    private Button btnPermission;
    private Button btnDownload;
    private Button btnSu;
    private Button btnMigration;
    private Button btnGo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.http_canary_activity_setting);
        btnPermission = findViewById(R.id.http_canary_permission_btn);
        btnDownload = findViewById(R.id.http_canary_download_btn);
        btnSu = findViewById(R.id.http_canary_su_btn);
        btnMigration = findViewById(R.id.http_canary_etc_btn);
        btnGo = findViewById(R.id.http_canary_go_btn);
        if (PermissionsUtils.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            btnPermission.setEnabled(false);
            btnDownload.setEnabled(true);
        }

        btnPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}