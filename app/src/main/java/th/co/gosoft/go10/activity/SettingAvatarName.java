package th.co.gosoft.go10.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import th.co.gosoft.go10.R;
import th.co.gosoft.go10.model.UserModel;

public class SettingAvatarName extends AppCompatActivity {

    private final String LOG_TAG = "SettingAvatarName";
//    private final String URL = "http://go10webservice.au-syd.mybluemix.net/GO10WebService/api/user/updateUser";
    private final String URL = "http://go10.au-syd.mybluemix.net/GO10WebService/api/user/updateUser";

    private static final int MAX_LENGTH = 20;
    private EditText edtAvatarName;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private ProgressDialog progress;
    private boolean isSeparateUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_avatar_name);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.change_avatar_name);

        sharedPref = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        Bundle extras = getIntent().getExtras();
        isSeparateUpdate = extras.getBoolean("isSeparateUpdate");

        edtAvatarName = (EditText) findViewById(R.id.edtAvatarName);
        edtAvatarName.setFilters( new InputFilter[] {new InputFilter.LengthFilter(MAX_LENGTH)});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setting_avatar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnSaveSetting:
                saveAvatarToSharedPreferences();
                if(isSeparateUpdate){
                    saveSetting();
                } else {
                    backPressed();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveSetting() {
        UserModel userModel = getUserModelFromSharedPreferences();
        callWebService(userModel);
    }

    private void saveAvatarToSharedPreferences(){
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("avatarName",  edtAvatarName.getText().toString());
        editor.commit();
    }

    private UserModel getUserModelFromSharedPreferences() {
        UserModel userModel = new UserModel();
        userModel.set_id(sharedPref.getString("_id", null));
        userModel.set_rev(sharedPref.getString("_rev", null));
        userModel.setAccountId(sharedPref.getString("accountId", null));
        userModel.setEmpName(sharedPref.getString("empName", null));
        userModel.setEmpEmail(sharedPref.getString("empEmail", null));
        userModel.setToken(sharedPref.getString("token", null));
        userModel.setActivate(sharedPref.getBoolean("activate", false));
        userModel.setType(sharedPref.getString("type", null));
        userModel.setAvatarPic(sharedPref.getString("avatarPic", null));
        userModel.setAvatarName(sharedPref.getString("avatarName", null));
        userModel.setBirthday(sharedPref.getString("birthday", null));
        return userModel;
    }

    private void callWebService(UserModel userModel){
        try {
            String jsonString = new ObjectMapper().writeValueAsString(userModel);
            Log.i(LOG_TAG, URL);
            Log.i(LOG_TAG, jsonString);

            AsyncHttpClient client = new AsyncHttpClient();
            client.put(this, URL, new StringEntity(jsonString,"utf-8"),
                    RequestParams.APPLICATION_JSON, new AsyncHttpResponseHandler() {

                        @Override
                        public void onStart() {
                            showLoadingDialog();
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                            Log.i(LOG_TAG, String.format(Locale.US, "Return Status Code: %d", statusCode));
                            Log.i(LOG_TAG, "New rev : "+new String(response));
                            editor.putString("_rev",  new String(response));
                            editor.commit();
                            closeLoadingDialog();
                            backPressed();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                            Log.e(LOG_TAG, String.format(Locale.US, "Return Status Code: %d", statusCode));
                            Log.e(LOG_TAG, "AsyncHttpClient returned error", e);
                        }
                    });
        } catch (JsonProcessingException e) {
            Log.e(LOG_TAG, "JsonProcessingException : "+e.getMessage(), e);
            showErrorDialog().show();
        }
    }

    private void backPressed(){
        super.onBackPressed();
    }

    private void showLoadingDialog() {
        progress = ProgressDialog.show(this, null, "Processing", true);
    }

    private void closeLoadingDialog(){
        progress.dismiss();
    }

    private AlertDialog.Builder showErrorDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Error Occurred!!!");
        alert.setCancelable(true);
        return alert;
    }
}
