package top.grapedge.grapeschat;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.gson.Gson;

public class BaseActivity extends AppCompatActivity {

    // 使用这个键值表示创建
    public final static String EXTRA_USER_DATA = "user_data";

    protected DataManager dataManager;

    // protected UserJsonData loginUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataManager = new DataManager(this);
    }

    // 实现点击空白处隐藏软键盘
    protected void touchBlank(View v, MotionEvent ev) {
        if (v != null && v instanceof EditText) {
            Rect rect = new Rect();
            v.getHitRect(rect);
            // 判断点击点是否在编辑框区域外
            if (!rect.contains((int)ev.getX(), (int)ev.getY())) {
                // 隐藏软键盘
                ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    public UserJsonData parseUserFromString(String obj) {
        if (obj == null) return  new UserJsonData();
        return new Gson().fromJson(obj, UserJsonData.class);
    }

    // 分发点击事件
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            touchBlank (getCurrentFocus(), ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    // 防止遮挡控件
    private int scrollTo = 0;
    protected void scrollToView(final View scrollView, final View targetView) {
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                // 获得可见区域的高度
                scrollView.getWindowVisibleDisplayFrame(rect);
                // 父布局的高度减去可见区域的底部高度就是不可见区域的高度
                int invisibleHeight = scrollView.getRootView().getHeight() - rect.bottom;
                // 如果不可见高度大于150说明键盘已经打开
                if (invisibleHeight > 150) {
                    // 下面的部分和 ScrollView 版是相似原理
                    int[] location = new int[2];
                    targetView.getLocationInWindow(location);

                    scrollTo += location[1] + targetView.getHeight() - rect.bottom;

                } else scrollTo = 0;

                scrollView.scrollTo(0, scrollTo);
            }
        });
    }
}
