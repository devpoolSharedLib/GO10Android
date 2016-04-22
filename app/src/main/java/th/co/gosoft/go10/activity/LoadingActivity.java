package th.co.gosoft.go10.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;

import gosoft.co.th.go10.R;

public class LoadingActivity extends Activity {

    private final String LOG_TAG = "LoadingActivityTag";
    private final long SPLASH_TIME_OUT = 2000L;

    private AccessTokenTracker accessTokenTracker;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            FacebookSdk.sdkInitialize(this.getApplicationContext());
            setContentView(R.layout.activity_loading);

//            accessTokenTracker = new AccessTokenTracker() {
//                @Override
//                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
//                    updateWithToken(newAccessToken);
//                }
//            };
//
//            updateWithToken(AccessToken.getCurrentAccessToken());

            prepareGmailLoginSession();

            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
            if (opr.isDone()) {
                Log.i(LOG_TAG, "Got cached sign-in");
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
//                    Intent i = new Intent(LoadingActivity.this, SelectRoomActivity.class);
                        Intent i = new Intent(LoadingActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                }, SPLASH_TIME_OUT);
            } else {
                Log.i(LOG_TAG, "Not Get cached sign-in");
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
//                    Intent i = new Intent(LoadingActivity.this, SelectRoomActivity.class);
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

    private void updateWithToken(AccessToken currentAccessToken) {

        if (currentAccessToken != null) {
            Log.i(LOG_TAG, "currentAccessToken");
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
//                    Intent i = new Intent(LoadingActivity.this, SelectRoomActivity.class);
                    Intent i = new Intent(LoadingActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }, SPLASH_TIME_OUT);
        } else {
            Log.i(LOG_TAG, "null currentAccessToken");
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    Intent i = new Intent(LoadingActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }, SPLASH_TIME_OUT);
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

            Log.i(LOG_TAG, "prepareGmailLoginSession()");

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
