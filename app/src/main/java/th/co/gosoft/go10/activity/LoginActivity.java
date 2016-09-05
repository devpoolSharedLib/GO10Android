package th.co.gosoft.go10.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import th.co.gosoft.go10.R;
import th.co.gosoft.go10.model.UserModel;

public class LoginActivity extends AppCompatActivity {

    private final String LOG_TAG = "LoginActivity";
    private final String URL = "https://go10webservice.au-syd.mybluemix.net/GO10WebService/api/user/getUserByUserPassword";

    private EditText txtEmail;
    private EditText txtPassword;
    private Button btnLogin;
    private TextView txtForgotYourPassword;
    private ProgressDialog progress;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
            client.get(concatString, new BaseJsonHttpResponseHandler<List<UserModel>>() {

                @Override
                public void onStart() {
                    super.onStart();
                    showLoadingDialog();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, List<UserModel> response) {
                    try {
                        closeLoadingDialog();
                        List<UserModel> userModelList = response;
                        Log.i(LOG_TAG, "user modelList size : "+userModelList.size());
                        if(userModelList.isEmpty()){
                            Log.i(LOG_TAG, "Not have user model");
                            Toast.makeText(getApplication(), "The e-mail or password is incorrect.\nPlease try again.", Toast.LENGTH_SHORT).show();
                        } else {
                            if(isActivate(userModelList)){
                                Toast.makeText(getApplication(), "The e-mail or password is incorrect.\nPlease try again.", Toast.LENGTH_SHORT).show();
                            } else {
                                insertUserModelToSharedPreferences(userModelList.get(0));
                                if(hasNotSettingAvatar(userModelList)){
                                    gotoSettingAvatarActivity();
                                } else {
                                    gotoHomeActivity();
                                }
                            }
                            Log.i(LOG_TAG, "have user model");
                        }

                    } catch (Throwable e) {
                        Log.e(LOG_TAG, e.getMessage(), e);
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, List<UserModel> errorResponse) {
                    Log.e(LOG_TAG, "Error code : " + statusCode + ", " + throwable.getMessage());
                    closeLoadingDialog();
                }

                @Override
                protected List<UserModel> parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    Log.i(LOG_TAG, ">>>>>>>>>>>>>>>>.. Json String : "+rawJsonData);
                    return new ObjectMapper().readValue(rawJsonData, new TypeReference<List<UserModel>>() {});
                }

            });
        } catch (Exception e) {
            Log.e(LOG_TAG, "RuntimeException : "+e.getMessage(), e);
            closeLoadingDialog();
        }
    }

    private boolean hasNotSettingAvatar(List<UserModel> userModelList) {
        return "Avatar Name".equals(userModelList.get(0).getAvatarName()) || "default_avatar".equals(userModelList.get(0).getAvatarPic());
    }

    private boolean isActivate(List<UserModel> userModelList) {
        return !userModelList.get(0).isActivate();
    }

    private void insertUserModelToSharedPreferences(UserModel userModel) {
        editor.putString("_id",  userModel.get_id());
        editor.putString("_rev",  userModel.get_rev());
        editor.putString("empName",  userModel.getEmpName());
        editor.putString("empEmail",  userModel.getEmpEmail());
        editor.putString("avatarName",  userModel.getAvatarName());
        editor.putString("avatarPic",  userModel.getAvatarPic());
        editor.putString("birthday",  userModel.getBirthday());
        editor.putBoolean("activate",  userModel.isActivate());
        editor.putString("type", userModel.getType());
        editor.putString("accountId", userModel.getAccountId());
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
