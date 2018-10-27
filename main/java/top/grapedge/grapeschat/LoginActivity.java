package top.grapedge.grapeschat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Random;

import top.grapedge.qqlogin.R;

public class LoginActivity extends BaseActivity {

    public static final String ACTION = "top.grapedge.qqlogin.intent.action.LOGIN";
    private EditText accountEdit;
    private EditText passwordEdit;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        // 设置键盘防挡
        scrollToView(findViewById(R.id.login_view), loginButton);
    }

    private void initView() {
        setupEditText();
        setupButton();
        setupBackground();
        changeImageWithTimeInterval();
    }

    private void setupEditText() {
        accountEdit = findViewById(R.id.activity_login_account_edit);
        passwordEdit = findViewById(R.id.activity_login_password_edit);
    }

    private void setupButton() {
        loginButton = findViewById(R.id.activity_login_login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = accountEdit.getText().toString();
                final String password = passwordEdit.getText().toString();
                login(name, password);
            }
        });

        TextView register = findViewById(R.id.activity_login_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }


    private int[] backgroundIds = new int[3];

    // 初始化图片资源
    private void setupBackground() {
        for (int i = 0; i < backgroundIds.length; i++) {
            // 获取图片的id
            int resId = getResources().getIdentifier("login_background" + i, "mipmap", this.getPackageName());
            backgroundIds[i] = resId;
        }
    }

    // 动态改变图片
    private void changeImageWithTimeInterval() {
        final ImageView imageView = findViewById(R.id.activity_login_random_image);
        final int delayMillis = 10000;
        final Random random = new Random();
        new Runnable() {

            @Override
            public void run() {
                int index = random.nextInt() % backgroundIds.length;
                index = index < 0 ? index + backgroundIds.length : index;
                imageView.setImageResource(backgroundIds[index]);
                imageView.postDelayed(this, delayMillis);
            }
        }.run();
    }

    private void login(final String name, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                loginButton.setClickable(false);
                String result = HttpUtil.Get(HttpUtil.LOGIN_URL + "?nickname=" + name + "&password=" + password);
                if (result == null) {
                    Log.e("GrapesError", "登录失败，检查一下网络连接");
                    return;
                }
                JsonData data = new Gson().fromJson(result, JsonData.class);
                loginButton.setClickable(true);
                updateUI(data);
                if (data.getCode() == 0) {
                    dataManager.enableAutoLogin(name, password);
                    dataManager.apply();
                }
            }
        }).start();
    }


    private void updateUI(final JsonData data) {
        // 子线程中不能更新 UI 界面，所以我们将数据通过 runOnUiThread 传至主线程进行更新
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (data.getCode() == 0) {
                    // 登录成功
                    Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra(EXTRA_USER_DATA, data.getObj());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, data.getObj(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
