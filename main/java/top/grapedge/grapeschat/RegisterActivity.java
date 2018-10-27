package top.grapedge.grapeschat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.Gson;
import top.grapedge.qqlogin.R;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }


    private EditText nickNameEdit;
    private EditText passwordEdit;
    private EditText passwordAgainEdit;
    private Button submit;

    private void initView() {
        nickNameEdit = findViewById(R.id.register_nickname);
        passwordEdit = findViewById(R.id.register_password);
        passwordAgainEdit = findViewById(R.id.register_password_again);
        submit = findViewById(R.id.register_submit);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String value = inputCheck();
        if (value.equals("success")) {
            // 输入合法
            registerAccount();
        } else {
            // 输入不合法
            Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
        }
    }

    private String inputCheck() {
        String name = nickNameEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        if (name.length() == 0) {
            return getString(R.string.input_user_name);
        }
        if (password.length() < 3) {
            return getString(R.string.password_gt_3);
        }
        // 确认两遍密码输入是否一致
        if (!password.equals(passwordAgainEdit.getText().toString())) {
            return getString(R.string.password_not_equal);
        }
        return "success";
    }


    private void registerAccount() {
        final String name = nickNameEdit.getText().toString();
        final String passwd = passwordEdit.getText().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = HttpUtil.Get(HttpUtil.REGISTER_URL + "?nickname=" + name + "&password=" + passwd);
                if (result == null) {
                    Log.e("GrapesError", "注册失败，请检测网络");
                    return;
                }
                JsonData data = new Gson().fromJson(result, JsonData.class);
                updateUI(data);
            }
        }).start();
    }

    private void updateUI(final JsonData data) {
        // 子线程中不能更新 UI 界面，所以我们将数据通过 runOnUiThread 传至主线程进行更新
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (data.getCode() == 0) {
                    // 注册成功
                    Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, data.getObj(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
