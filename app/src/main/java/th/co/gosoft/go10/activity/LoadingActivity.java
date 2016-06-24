package th.co.gosoft.go10.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import io.fabric.sdk.android.Fabric;
import th.co.gosoft.go10.R;
import th.co.gosoft.go10.model.UserModel;

public class LoadingActivity extends Activity {

    private final String LOG_TAG = "LoadingActivityTag";
    private final String URL = "http://go10webservice.au-syd.mybluemix.net/GO10WebService/api/user/getUserByAccountId";

    private final long SPLASH_TIME_OUT = 1000L;
    private boolean IS_LOGIN_FACEBOOK = false;
    private boolean IS_SIGNIN_GOOGLE = false;
    private GoogleApiClient mGoogleApiClient;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        sharedPref = this.getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        try{
            logKeyHash();
            FacebookSdk.sdkInitialize(this.getApplicationContext());
            setContentView(R.layout.activity_loading);
            checkCurrentTokenFacebook();
            prepareGmailLoginSession();
            checkCurrentTokenGmail();

            if(!IS_LOGIN_FACEBOOK && !IS_SIGNIN_GOOGLE){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(LoadingActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();

                    }
                }, SPLASH_TIME_OUT);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage(), e);
        }

    }

    private void logKeyHash() {
        try {
            PackageInfo info = null;
            info = getPackageManager().getPackageInfo(
                    "th.co.gosoft.go10",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = null;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("key_hash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }catch(Exception e){
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void checkCurrentTokenFacebook() {
        if (AccessToken.getCurrentAccessToken() != null) {
            Log.i(LOG_TAG, "Facebook logged in");
            initialNewFacebookBundle();
            IS_LOGIN_FACEBOOK = true;
        } else {
            Log.i(LOG_TAG, "Facebook not logged in");
            IS_LOGIN_FACEBOOK = false;
        }
    }

    private void initialNewFacebookBundle() {
        Bundle params = new Bundle();
        params.putString("fields","id, name");

        new GraphRequest(AccessToken.getCurrentAccessToken(), "me", params, HttpMethod.GET,
            new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse response) {
                    if (response != null) {
                        try {
                            JSONObject data = response.getJSONObject();
                            getUserDataFromServer(data.getString("id"));
                        } catch (Exception e) {
                            Log.e(LOG_TAG, e.getMessage(), e);
                        }
                    }
                }
            }).executeAsync();
    }

    private void checkCurrentTokenGmail() {

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            Log.i(LOG_TAG, "Google logged in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
            IS_SIGNIN_GOOGLE = true;
        } else {
            Log.i(LOG_TAG, "Google not logged in");
            IS_SIGNIN_GOOGLE = false;

        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(LOG_TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            getUserDataFromServer(acct.getId());
        } else {
            Log.i(LOG_TAG, "Cannot Login GMAIL Accout !!!");
        }
    }

    private void prepareGmailLoginSession() {
        try{
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void getUserDataFromServer(final String accountId){
        String concatString = URL+"?accountId="+accountId;

        try {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(concatString, new BaseJsonHttpResponseHandler<List<UserModel>>() {

                @Override
                public void onStart() {
                    super.onStart();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, List<UserModel> response) {
                    try {
                        List<UserModel> userModelList = response;
                        Log.i(LOG_TAG, "user modelList size : "+userModelList.size());
                        if(userModelList.isEmpty()){
                            Log.i(LOG_TAG, "Not have user model");
                            insertUserModelToSharedPreferences(accountId);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent i = new Intent(LoadingActivity.this, RegisterActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            }, SPLASH_TIME_OUT);
                        } else {
                            Log.i(LOG_TAG, "have user model");
                            insertUserModelToSharedPreferences(userModelList.get(0));
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent i = new Intent(LoadingActivity.this, HomeActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            }, SPLASH_TIME_OUT);
                        }

                    } catch (Throwable e) {
                        Log.e(LOG_TAG, e.getMessage(), e);
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, List<UserModel> errorResponse) {
                    Log.e(LOG_TAG, "Error code : " + statusCode + ", " + throwable.getMessage());
                }

                @Override
                protected List<UserModel> parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    Log.i(LOG_TAG, ">>>>>>>>>>>>>>>>.. Json String : "+rawJsonData);
                    return new ObjectMapper().readValue(rawJsonData, new TypeReference<List<UserModel>>() {});
                }

            });
        } catch (Exception e) {
            Log.e(LOG_TAG, "RuntimeException : "+e.getMessage(), e);
        }
    }

    private void insertUserModelToSharedPreferences(String accountId){

        Log.i(LOG_TAG, "account id : "+accountId);
        clearSharedPreference();
        editor.putString("accountId",  accountId);
        if(!sharedPref.contains("empName")){
            editor.putString("empName",  "Employee Name");
        }
        if(!sharedPref.contains("empEmail")){
            editor.putString("empEmail",  "email@gosoft.com");
        }
        if(!sharedPref.contains("avatarName")){
            editor.putString("avatarName",  "Avatar Name");
        }
        if(!sharedPref.contains("avatarPic")) {
            editor.putString("avatarPic", "default_avatar");
        }
        editor.commit();
    }

    private void insertUserModelToSharedPreferences(UserModel userModel){
        Log.i(LOG_TAG, "AVATAR DATA : "+userModel.getAvatarPic()+" : "+userModel.getAvatarName());
        clearSharedPreference();
        editor.putString("_id",  userModel.get_id());
        editor.putString("_rev",  userModel.get_rev());
        editor.putString("accountId",  userModel.getAccountId());
        editor.putString("empName",  userModel.getEmpName());
        editor.putString("empEmail",  userModel.getEmpEmail());
        editor.putString("avatarName",  userModel.getAvatarName());
        editor.putString("avatarPic", userModel.getAvatarPic());
        editor.putString("token",  userModel.getToken());
        editor.putBoolean("activate",  userModel.isActivate());
        editor.putString("type", userModel.getType());
        editor.commit();
    }

    private void clearSharedPreference() {
        editor.clear();
        editor.commit();
    }

}