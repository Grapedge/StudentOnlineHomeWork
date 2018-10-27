package top.grapedge.grapeschat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import top.grapedge.qqlogin.R;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    // 碎片
    private CommentFragment commentFragment;
    private FriendsFragment friendsFragment;
    private UpdateFragment updateFragment;

    // 对应的布局的view
    private View commentLayout;
    private View friendsLayout;
    private View updateLayout;

    // 对应的图片，因为按下时我们要将图片换成按下状态的图
    private ImageView commentImageView;
    private ImageView friendsImageView;
    private ImageView updateImageView;

    // 标题栏
    private TextView titleTextView;

    // 碎片管理
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setTitle("");
        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));
        selectItem(R.id.main_comment_layout);
    }

    private void initView() {
        commentLayout = findViewById(R.id.main_comment_layout);
        friendsLayout = findViewById(R.id.main_friends_layout);
        updateLayout = findViewById(R.id.main_update_layout);

        commentImageView = findViewById(R.id.main_comment_image);
        friendsImageView = findViewById(R.id.main_friends_image);
        updateImageView = findViewById(R.id.main_update_image);

        titleTextView = findViewById(R.id.main_toolbar_title);

        commentLayout.setOnClickListener(this);
        friendsLayout.setOnClickListener(this);
        updateLayout.setOnClickListener(this);

        fragmentManager = getSupportFragmentManager();

    }

    /*
    private void setupUserData() {
        Intent intent = getIntent();
        String obj = intent.getStringExtra("user_data");
        UserJsonData data = new Gson().fromJson(obj, UserJsonData.class);
        Log.d("Grapes", data.getId() + " " + data.getNickname());
    }*/


    // 实现按下返回键不退出应用程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // 点击时切换碎片
    @Override
    public void onClick(View v) {
        selectItem(v.getId());
    }

    // 重置底部导航栏按钮按下状态
    private void resetNavBar() {
        commentImageView.setImageResource(R.mipmap.comment_unselected);
        friendsImageView.setImageResource(R.mipmap.friends_unselected);
        updateImageView.setImageResource(R.mipmap.update_unselected);
    }

    // 切换到指定id碎片
    private void selectItem(int resId) {
        // 重置导航栏为未选中状态的图片
        resetNavBar();
        fragmentTransaction = fragmentManager.beginTransaction();
        // 隐藏碎片
        hideFragment();
        // 判断应该显示哪个布局然后显示
        if (resId == R.id.main_comment_layout) {
            commentImageView.setImageResource(R.mipmap.comment_selected);
            setActivityTitle(R.string.comment);
            if (commentFragment == null) {
                commentFragment = new CommentFragment();
                fragmentTransaction.add(R.id.main_content, commentFragment);
            } else fragmentTransaction.show(commentFragment);
        } else if (resId == R.id.main_friends_layout) {
            friendsImageView.setImageResource(R.mipmap.friends_selected);
            setActivityTitle(R.string.friends);
            if (friendsFragment == null) {
                friendsFragment = new FriendsFragment();
                fragmentTransaction.add(R.id.main_content, friendsFragment);
            } else fragmentTransaction.show(friendsFragment);
        } else if (resId == R.id.main_update_layout) {
            updateImageView.setImageResource(R.mipmap.update_selected);
            setActivityTitle(R.string.update);
            if (updateFragment == null) {
                updateFragment = new UpdateFragment();
                fragmentTransaction.add(R.id.main_content, updateFragment);
            } else fragmentTransaction.show(updateFragment);
        }
        fragmentTransaction.commit();
    }

    // 创建菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // 菜单选择
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_friend:
                Intent intent = new Intent(MainActivity.this, AddFriendActivity.class);
                intent.putExtra(EXTRA_USER_DATA, getIntent().getStringExtra(EXTRA_USER_DATA));
                startActivity(intent);
                break;
            case R.id.logout:
                dataManager.disableAutoLogin();
                dataManager.apply();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // 隐藏碎片
    private void hideFragment() {
        // 隐藏界面
        if (fragmentTransaction == null) return;
        if (commentFragment != null) fragmentTransaction.hide(commentFragment);
        if (friendsFragment != null) fragmentTransaction.hide(friendsFragment);
        if (updateFragment != null) fragmentTransaction.hide(updateFragment);
    }

    // 设置标题
    private void setActivityTitle(int title) {
        titleTextView.setText(title);
    }

}
