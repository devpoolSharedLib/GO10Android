package th.co.gosoft.go10.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;

import gosoft.th.co.go10.R;

public class LoadingActivity extends Activity {

    private final String LOG_TAG = "LoadingActivityTag";
    private final long SPLASH_TIME_OUT = 2000L;
    AccessTokenTracker accessTokenTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_loading);

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                updateWithToken(newAccessToken);
            }
        };

        updateWithToken(AccessToken.getCurrentAccessToken());
    }

    private void updateWithToken(AccessToken currentAccessToken) {

        if (currentAccessToken != null) {
            Log.i(LOG_TAG, "currentAccessToken");
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    Intent i = new Intent(LoadingActivity.this, SelectRoomActivity.class);
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
}
