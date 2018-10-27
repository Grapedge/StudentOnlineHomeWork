package top.grapedge.grapeschat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import top.grapedge.grapeschat.LoginActivity;
import top.grapedge.qqlogin.R;

public class SplashActivity extends BaseActivity {

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 延时两秒打开登录界面
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initSharedPreferences();
            }
        }, 2000);

        // 延时三秒关闭此Activity
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 3000);
    }

    private void initSharedPreferences() {
        boolean autoLogin = dataManager.getAutoLogin();
        if (autoLogin) {
            final String name = dataManager.getAccount();
            final String password = dataManager.getPassword();
            if (name == null || password == null) {
                Toast.makeText(this, R.string.login_info_fail, Toast.LENGTH_SHORT);
                startActivity(new Intent(LoginActivity.ACTION));
            }
            login(name, password);
        } else {
            startActivity(new Intent(LoginActivity.ACTION));
        }
    }

    // 登录
    private void login(final String name, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = HttpUtil.Get(HttpUtil.LOGIN_URL + "?nickname=" + name + "&password=" + password);
                if (result == null) {
                    Log.e("GrapesError", "登录失败，确认网络是否正常");
                    return;
                }
                JsonData data = new Gson().fromJson(result, JsonData.class);
                UpdateUI(data);
            }
        }).start();
    }

    // 更新 UI
    private void UpdateUI(final JsonData data) {
        // 子线程中不能更新 UI 界面，所以我们将数据通过 runOnUiThread 传至主线程进行更新
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (data.getCode() == 0) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    intent.putExtra(EXTRA_USER_DATA, data.getObj());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SplashActivity.this, data.getObj(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
