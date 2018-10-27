package top.grapedge.grapeschat;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtil {
    // 获取好友接口
    public final static String GET_FRIENDS_URL = "http://202.194.15.144:12234/user/friendList";
    // 添加好友列表
    public final static String ADD_FRIEND_URL = "http://202.194.15.144:12234/user/addFriend";
    // 查询用户接口
    public final static String QUERY_URL = "http://202.194.15.144:12234/user/query";
    // 注册用户接口
    public final static String REGISTER_URL = "http://202.194.15.144:12234/user/register";
    // 登录用户接口
    public final static String LOGIN_URL = "http://202.194.15.144:12234/user/login";

    public static String Get(String url) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            return  response.body().string();
        } catch (Exception e) {
            Log.e("GrapesError", e.toString());
            return null;
        }
    }
}
