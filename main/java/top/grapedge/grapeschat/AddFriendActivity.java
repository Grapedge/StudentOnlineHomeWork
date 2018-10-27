package top.grapedge.grapeschat;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import top.grapedge.qqlogin.R;

public class AddFriendActivity extends BaseActivity implements TextView.OnEditorActionListener {

    // 用户输入框
    private EditText userNameEdit;
    // 用户列表
    private List<UserJsonData> users = new ArrayList<>();
    // 显示列表
    private RecyclerView recyclerView;
    // 适配器
    private FriendRecyclerViewAdapter adapter;
    // 登录用户的 json 信息
    private UserJsonData loginUser;
    // 返回按钮
    private TextView backText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        // 初始化控件
        initView();
    }

    private void initView() {
        // 获取当前登录用户
        loginUser = parseUserFromString(getIntent().getStringExtra("user_data"));
        // 初始化控件
        userNameEdit = findViewById(R.id.add_friend_edit);
        userNameEdit.requestFocus();
        recyclerView = findViewById(R.id.add_friend_list);
        backText = findViewById(R.id.add_friend_back);
        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        // 适配器
        adapter = new FriendRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);
        userNameEdit.setOnEditorActionListener(this);
    }


    // 更新搜索列表
    private void updateUserList(final String key) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String json = new Gson().fromJson(HttpUtil.Get(HttpUtil.QUERY_URL + "?key=" + key), JsonData.class).getObj();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 通过解析 json 获得用户列表
                        users = new Gson().fromJson(json, new TypeToken<List<UserJsonData>>(){}.getType());
                        adapter.updateList();
                        Toast.makeText(AddFriendActivity.this, "一共找到" + users.size() + "位用户", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    // 设置输入框是否可以进行输入
    private void setEditTextActive(final boolean active) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                userNameEdit.setFocusable(active);
                userNameEdit.setFocusableInTouchMode(active);
            }
        });
    }

    // 进行搜索
    private void search(final EditText editText) {
        // 隐藏软键盘
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        if (editText.getText().toString().length() == 0) {
            Toast.makeText(this, "请输入用户名！", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 禁用输入框
                setEditTextActive(false);
                // 得到输入数据
                String key = editText.getText().toString();
                // 更新列表数据
                updateUserList(key);
                // 启用输入框
                setEditTextActive(true);
            }
        }).start();
    }

    // 添加好友
    private void addFriend(final UserJsonData addUser) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 获取返回数据，传入登录用户和添加用户
                String data = HttpUtil.Get(HttpUtil.ADD_FRIEND_URL + "?userId=" + loginUser.getId() + "&friendId=" + addUser.getId());
                final JsonData jsonData = new Gson().fromJson(data, JsonData.class);

                // 更新 UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddFriendActivity.this, addUser.getNickname() + " " + jsonData.getObj(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    // 输入动作发生时
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            // 如果按了软键盘上的搜索键
            search(userNameEdit);
        }
        return false;
    }

    class FriendRecyclerViewAdapter extends RecyclerView.Adapter {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            // 创建子项
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.add_friend_item, viewGroup, false);
            UserHolder holder = new UserHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            // 得到对应项的数据
            UserJsonData data = users.get(i);
            UserHolder userHolder = (UserHolder)viewHolder;
            // 设置昵称
            userHolder.userName.setText(data.getNickname());
            // 设置对应的按钮的 Tag
            userHolder.addButton.setTag(users.get(i));
        }

        // 更新列表
        public void updateList() {
            notifyDataSetChanged();
        }

        // 清空用户列表，不过似乎我没用到
        public void clear() {
            users.clear();
            updateList();
        }

        // 得到项目数量
        @Override
        public int getItemCount() {
            return users.size();
        }

        // Holder
        class UserHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView userName;
            Button addButton;

            public UserHolder(@NonNull View itemView) {
                super(itemView);
                userName = itemView.findViewById(R.id.friend_name);
                addButton = itemView.findViewById(R.id.add_friend);
                addButton.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                // 获得对应的用户数据
                UserJsonData userJsonData = (UserJsonData) addButton.getTag();
                // 添加好友
                addFriend(userJsonData);
            }
        }
    }

}
