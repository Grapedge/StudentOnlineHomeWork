package top.grapedge.grapeschat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

public class BaseFragment extends Fragment {
    public static final String EXTRA_USER_DATA = BaseActivity.EXTRA_USER_DATA;

    // 当前登录用户
    protected UserJsonData loginUser;
    // 登录用户的json信息
    protected String loginUserJson;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        loginUserJson = getActivity().getIntent().getStringExtra(EXTRA_USER_DATA);
        loginUser = new Gson().fromJson(loginUserJson, UserJsonData.class);
        return initView(inflater, container, savedInstanceState);
    }

    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
