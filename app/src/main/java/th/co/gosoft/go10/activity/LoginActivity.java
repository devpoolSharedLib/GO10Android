package th.co.gosoft.go10.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;
import com.ibm.mobilefirstplatform.clientsdk.android.security.facebookauthentication.FacebookAuthenticationManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import gosoft.th.co.go10.R;
import th.co.gosoft.go10.util.GO10Application;
import th.co.gosoft.go10.util.Session;

public class LoginActivity extends Activity {

    private final String LOG_TAG = "LoginActivityTag";

    private Button btnGotoLogin, btnGotoRegister;
    private EditText edtUserName, edtPassword;
    private Session session;

    CallbackManager callbackManager;
    Profile profile;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        try{
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            profile = Profile.getCurrentProfile();
            if (accessToken != null) {
                Log.i(LOG_TAG, "accessToken");
//                gotoSelectRoomActivity();
            } else {
                Log.i(LOG_TAG, "not accessToken");
//                prepareLoginSession();
            }
            if (profile != null) {
                Log.i(LOG_TAG, "Logged in");
                gotoSelectRoomActivity();
            } else {
                Log.i(LOG_TAG, "not Logged in");
                prepareLoginSession();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    private void prepareLoginSession(){
        try{
            setContentView(R.layout.activity_login);
            callbackManager = CallbackManager.Factory.create();
            LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
            List < String > permissionNeeds = Arrays.asList("user_photos", "email", "user_birthday", "public_profile");
            loginButton.setReadPermissions(permissionNeeds);

            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.i(LOG_TAG, "success : "+loginResult);

                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    Log.i("LoginActivity", response.toString());
                                    Bundle facebookBundle = getFacebookData(object);
                                    addFacebookBundleToApplication(facebookBundle);
                                    gotoSelectRoomActivity();
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields","id, first_name, last_name, email, gender, birthday, location");
                    request.setParameters(parameters);
                    request.executeAsync();
                }

                @Override
                public void onCancel() {
                    Log.i(LOG_TAG, "cancel");
                }

                @Override
                public void onError(FacebookException exception) {
                    Log.e(LOG_TAG, exception.getMessage(), exception);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    private void addFacebookBundleToApplication(Bundle facebookBundle) {
        ((GO10Application) this.getApplication()).setFacebookBundle(facebookBundle);
    }

    private void gotoSelectRoomActivity() {
        Intent newActivity = new Intent(LoginActivity.this, SelectRoomActivity.class);
        startActivity(newActivity);
    }

    private Bundle getFacebookData(JSONObject object) {

        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));

            return bundle;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        Log.i(LOG_TAG, "onActivityResult");
            callbackManager.onActivityResult(requestCode, responseCode, data);
    }

}