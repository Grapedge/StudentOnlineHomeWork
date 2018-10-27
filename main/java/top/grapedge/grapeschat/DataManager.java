package top.grapedge.grapeschat;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

public class DataManager {
    private SharedPreferences preferences;
    private SharedPreferences.Editor preEditor;
    private static final String LOGIN_INFO = "login_info";
    private static final String AUTO_LOGIN = "auto_login";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    private Activity activity;

    public DataManager(Activity activity) {
        this.activity = activity;
        Log.e("Grapes", activity.getSharedPreferences(LOGIN_INFO, MODE_PRIVATE).toString());
        preferences = activity.getSharedPreferences(LOGIN_INFO, MODE_PRIVATE);
        preEditor = preferences.edit();
    }

    public void disableAutoLogin() {
        preEditor.putBoolean(AUTO_LOGIN, false);
    }

    public void enableAutoLogin(String account, String password) {
        preEditor.putBoolean(AUTO_LOGIN, true);
        preEditor.putString(ACCOUNT, account);
        preEditor.putString(PASSWORD, password);
    }

    public boolean getAutoLogin() {
        return preferences.getBoolean(AUTO_LOGIN, false);
    }

    public String getAccount() {
        return preferences.getString(ACCOUNT, null);
    }

    public String getPassword() {
        return preferences.getString(PASSWORD, null);
    }

    public void apply() {
        preEditor.apply();
    }

}
