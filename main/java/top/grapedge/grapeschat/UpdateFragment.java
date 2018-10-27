package top.grapedge.grapeschat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import top.grapedge.qqlogin.R;

public class UpdateFragment extends BaseFragment {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View updateLayout = inflater.inflate(R.layout.fragment_update, container, false);
        WebView webView = updateLayout.findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http://blog.grapedge.top");
        return  updateLayout;
    }
}
