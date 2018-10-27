package top.grapedge.grapeschat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import top.grapedge.qqlogin.R;

public class CommentFragment extends BaseFragment {
    @Nullable
    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View commentLayout = inflater.inflate(R.layout.fragment_comment, container, false);
        TextView welcome = commentLayout.findViewById(R.id.welcome);
        welcome.setText("欢迎回来，" + loginUser.getNickname() + "！");
        return commentLayout;
    }
}
