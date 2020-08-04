package fairy.easy.httpcanary.preview;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import fairy.easy.httpcanary.R;

/**
 * 设置相关页面
 * 第一步 给与相关权限
 * 第二步 下载证书并安装
 * 第三步 给与su权限
 * 第四步 迁移证书
 */
public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }
}