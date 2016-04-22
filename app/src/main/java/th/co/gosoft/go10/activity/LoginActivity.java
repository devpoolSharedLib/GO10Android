package th.co.gosoft.go10.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import gosoft.co.th.go10.R;
import th.co.gosoft.go10.util.GO10Application;

public class LoginActivity extends Activity implements
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    private final String LOG_TAG = "LoginActivityTag";
    private static final int RC_SIGN_IN = 9001;

    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "onCreate()");

        try{
            FacebookSdk.sdkInitialize(getApplicationContext());
            setContentView(R.layout.activity_login);
            prepareFacebookLoginSession();
            prepareGmailLoginSession();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    private void prepareFacebookLoginSession(){
        try{
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
                                    Bundle facebookBundle = createBundleFromFacebookObject(object);
                                    addBundleToApplication(facebookBundle);
                                    gotoSelectRoomActivity();
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields","id, name, email, gender, birthday, location");
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

    private void prepareGmailLoginSession() {
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Log.i(LOG_TAG, "prepareGmailLoginSession()");
    }

    public void signIn(){
        Log.i(LOG_TAG, "callGoogleSignInActivity()");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void addBundleToApplication(Bundle facebookBundle) {
        ((GO10Application) this.getApplication()).setFacebookBundle(facebookBundle);
    }

    private void gotoSelectRoomActivity() {
        Intent newActivity = new Intent(LoginActivity.this, SelectRoomActivity.class);
        startActivity(newActivity);
    }

    private Bundle createBundleFromFacebookObject(JSONObject object) {

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

            bundle.putString("id", id);
            if (object.has("name"))
                bundle.putString("name", object.getString("name"));
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
    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            callbackManager.onActivityResult(requestCode, responseCode, data);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(LOG_TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            createBundleFromGmailObject(acct);
            gotoSelectRoomActivity();
        } else {
            Log.i(LOG_TAG, "Cannot Login GMAIL Accout !!!");
        }
    }

    private Bundle createBundleFromGmailObject(GoogleSignInAccount acct) {
        Bundle bundle = new Bundle();
        bundle.putString("id", acct.getId());
        if (acct.getDisplayName() != null && !acct.getDisplayName().isEmpty())
            bundle.putString("name", acct.getDisplayName());
        if (acct.getEmail() != null && !acct.getEmail().isEmpty())
            bundle.putString("email", acct.getEmail());
        if (acct.getPhotoUrl() != null)
            bundle.putString("profile_pic", acct.getPhotoUrl().toString());
        return bundle;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(LOG_TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }
}