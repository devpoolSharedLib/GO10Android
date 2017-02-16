package th.co.gosoft.go10.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.onesignal.OneSignal;
import com.onesignal.shortcutbadger.ShortcutBadger;

import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import th.co.gosoft.go10.R;
import th.co.gosoft.go10.util.PropertyUtility;

public class LoginActivity extends AppCompatActivity {

    private final String LOG_TAG = "LoginActivity";

    private String GET_BADGE_NUMBER_URL;
    private String URL;
    private String CHECK_ROOM_NOTIFICATION_URL;
    private EditText txtEmail;
    private EditText txtPassword;
    private Button btnLogin;
    private TextView txtForgotYourPassword;
    private ProgressDialog progress;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private List<Map<String, Object>> userModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ShortcutBadger.removeCount(LoginActivity.this);
        URL = PropertyUtility.getProperty("httpsUrlSite", this)+"GO10WebService/api/"+PropertyUtility.getProperty("versionServer", this)
                +"user/getUserByUserPassword";
        CHECK_ROOM_NOTIFICATION_URL = PropertyUtility.getProperty("httpsUrlSite", this)+"GO10WebService/api/"+PropertyUtility.getProperty("versionServer", this)
                +"user/checkRoomNotificationModel";
        GET_BADGE_NUMBER_URL = PropertyUtility.getProperty("httpsUrlSite", this)+"GO10WebService/api/"+PropertyUtility.getProperty("versionServer", this)
                +"topic/getbadgenumbernotification";
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        txtForgotYourPassword = (TextView) findViewById(R.id.txtForgotYourPassword);

        sharedPref = this.getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    public void login(View view){
        if(isInputsEmpty()){
            Toast.makeText(this, "Please enter your E-mail and Password.", Toast.LENGTH_SHORT).show();
        } else {
            callWebService(txtEmail.getText().toString(), txtPassword.getText().toString());
        }
    }

    private void callWebService(String email, String password){
        String concatString = URL+"?email="+email+"&password="+password;

        try {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(concatString, new BaseJsonHttpResponseHandler<List<Map<String, Object>>>() {

                @Override
                public void onStart() {
                    super.onStart();
                    showLoadingDialog();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, List<Map<String, Object>> response) {
                    try {
                        closeLoadingDialog();
                        userModelList = response;
                        Log.i(LOG_TAG, "user modelList size : "+userModelList.size());
                        if(userModelList.isEmpty()){
                            Log.i(LOG_TAG, "Not have user model");
                            Toast.makeText(getApplication(), "The e-mail or password is incorrect.\nPlease try again.", Toast.LENGTH_SHORT).show();
                        } else {
                            if(isActivate(userModelList)){
                                Toast.makeText(getApplication(), "The e-mail or password is incorrect.\nPlease try again.", Toast.LENGTH_SHORT).show();
                            } else {
                                insertUserModelToSharedPreferences(userModelList.get(0));
                                registerRoomNotificationModel();
                            }
                            Log.i(LOG_TAG, "have user model");
                        }

                    } catch (Throwable e) {
                        Log.e(LOG_TAG, e.getMessage(), e);
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, List<Map<String, Object>> errorResponse) {
                    Log.e(LOG_TAG, "Error code : " + statusCode + ", " + throwable.getMessage());
                    closeLoadingDialog();
                }

                @Override
                protected List<Map<String, Object>> parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    Log.i(LOG_TAG, ">>>>>>>>>>>>>>>>.. Json String : "+rawJsonData);
                    return new ObjectMapper().readValue(rawJsonData, new TypeReference<List<Map<String, Object>>>() {});
                }

            });
        } catch (Exception e) {
            Log.e(LOG_TAG, "RuntimeException : "+e.getMessage(), e);
            closeLoadingDialog();
        }
    }

    private void registerRoomNotificationModel() {
        String email = txtEmail.getText().toString();
        String concatString = CHECK_ROOM_NOTIFICATION_URL+"?empEmail="+email;
        Log.i(LOG_TAG, "Concat URL : "+concatString);
        try {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(concatString, new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    super.onStart();
                    showLoadingDialog();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    try {
                        closeLoadingDialog();
                        String responseString = new String(response);
                        Log.i(LOG_TAG, "Room Notification Model Date : "+responseString);
                        editor.putString("notificationDate", responseString);
                        editor.commit();
                        if(hasNotSettingAvatar(userModelList)){
                            gotoSettingAvatarActivity();
                        } else {
                            gotoHomeActivity();
                        }
                    } catch (Throwable e) {
                        Log.e(LOG_TAG, e.getMessage(), e);
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    Log.e(LOG_TAG, "Error code : " + statusCode + ", " + e.getMessage(), e);
                    OneSignal.setSubscription(false);
                    closeLoadingDialog();
                }

            });
        } catch (Exception e) {
            Log.e(LOG_TAG, "RuntimeException : "+e.getMessage(), e);
            closeLoadingDialog();
        }
    }

    private boolean hasNotSettingAvatar(List<Map<String, Object>> userModelList) {
        return "Avatar Name".equals(userModelList.get(0).get("avatarName")) || "default_avatar".equals(userModelList.get(0).get("avatarPic"));
    }

    private boolean isActivate(List<Map<String, Object>> userModelList) {
        return ! (Boolean) userModelList.get(0).get("activate");
    }

    private void insertUserModelToSharedPreferences(Map<String, Object> userModel) {
        editor.putString("_id", (String) userModel.get("_id"));
        editor.putString("_rev",  (String) userModel.get("_rev"));
        editor.putString("empName",  (String) userModel.get("empName"));
        editor.putString("empEmail",  (String) userModel.get("empEmail"));
        editor.putString("avatarName",  (String) userModel.get("avatarName"));
        editor.putString("avatarPic",  (String) userModel.get("avatarPic"));
        editor.putString("birthday",  (String) userModel.get("birthday"));
        editor.putBoolean("activate",  (Boolean) userModel.get("activate"));
        editor.putString("type", (String) userModel.get("type"));
        editor.putString("accountId", (String) userModel.get("accountId"));
        editor.putBoolean("hasLoggedIn", true);
        editor.commit();
    }

    private void gotoSettingAvatarActivity(){
        Intent intent = new Intent(this, SettingAvatar.class);
        intent.putExtra("state", "register");
        startActivity(intent);
        finish();
    }

    private void gotoHomeActivity() {
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        finish();
    }

    public void gotoForgetPasswordActivity(View view){
        Intent intent = new Intent(this, ForgetPasswordActivity.class);
        startActivity(intent);
    }

    private boolean isInputsEmpty() {
        return isEmpty(txtEmail) || isEmpty(txtPassword);
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    private void showLoadingDialog() {
        progress = ProgressDialog.show(this, null, "Processing", true);
    }

    private void closeLoadingDialog(){
        progress.dismiss();
    }
}
