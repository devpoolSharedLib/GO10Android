package th.co.gosoft.go10.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import th.co.gosoft.go10.R;
import th.co.gosoft.go10.adapter.SettingAvatarAdapter;
import th.co.gosoft.go10.model.UserModel;
import th.co.gosoft.go10.util.PropertyUtility;

public class SettingAvatar extends AppCompatActivity {

    private final String LOG_TAG = "SettingAvatar";

    private String URL;
    private ImageView avatarPic;
    private ListView settingListView;
    private Button btnStart;
    private String avatarPicName;
    private String avatarName;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private ProgressDialog progress;
    private boolean isSeparateUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_avatar);

        try{

        URL = PropertyUtility.getProperty("httpUrlSite", this)+"GO10WebService/api/"+PropertyUtility.getProperty("versionServer", this)
                    +"user/updateUser";
        sharedPref = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        Bundle extras = getIntent().getExtras();

        if(extras == null) {
            isSeparateUpdate = true;
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.setting_avatar);
        } else if(isComeFromRegisterActivity(extras)){
            isSeparateUpdate = false;
            btnStart = (Button) findViewById(R.id.btnStart);
            btnStart.setVisibility(View.VISIBLE);
            btnStart.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(avatarPicName.equals("default_avatar") || avatarName.equals("Avatar Name")){
                        Toast.makeText(getApplication(), "Please select avatar image and setting avatar name.", Toast.LENGTH_LONG).show();
                    } else {
                        saveSetting();
                    }
                }
            });
        }

        avatarPic = (ImageView) findViewById(R.id.imgProfileImage);
        avatarPic.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectAvatarPic.class);
                intent.putExtra("isSeparateUpdate",isSeparateUpdate);
                startActivity(intent);
            }
        });

        settingListView = (ListView) findViewById(R.id.settingListview);
        settingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent;
                switch(position) {
                    case 0 :
                        intent = new Intent(getApplicationContext(), SettingAvatarName.class);
                        intent.putExtra("isSeparateUpdate",isSeparateUpdate);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    private boolean isComeFromRegisterActivity(Bundle extras) {
        return extras != null && extras.getString("state").equals("register");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return(true);
        }

        return(super.onOptionsItemSelected(item));
    }

    @Override
    protected void onStart() {
        super.onStart();

        avatarPicName = sharedPref.getString("avatarPic", "default_avatar");
        Log.i(LOG_TAG, "SettingAvatar AvatarPicName : "+avatarPicName);
        avatarPic.setImageResource(getResources().getIdentifier(avatarPicName , "drawable", getPackageName()));

        avatarName = sharedPref.getString("avatarName", "Avatar Name");
        SettingAvatarAdapter settingAvatarAdapter = new SettingAvatarAdapter(this, avatarName);
        settingListView.setAdapter(settingAvatarAdapter);

    }

    private void saveSetting(){
        UserModel userModel = getUserModelFromSharedPreferences();
        callWebService(userModel);
    }

    private UserModel getUserModelFromSharedPreferences() {
        UserModel userModel = new UserModel();
        userModel.set_id(sharedPref.getString("_id", null));
        userModel.set_rev(sharedPref.getString("_rev", null));
        userModel.setAccountId(sharedPref.getString("accountId", null));
        userModel.setEmpName(sharedPref.getString("empName", null));
        userModel.setEmpEmail(sharedPref.getString("empEmail", null));
        userModel.setActivate(sharedPref.getBoolean("activate", true));
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
                        callNextActivity();
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

    private void showLoadingDialog() {
        progress = ProgressDialog.show(this, null,
                "Processing", true);
    }

    private void closeLoadingDialog(){
        progress.dismiss();
    }

    private AlertDialog.Builder showErrorDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Error while loading content.");
        alert.setCancelable(true);
        return alert;
    }

    private void callNextActivity(){
        Intent intent = new Intent(SettingAvatar.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

}
