package th.co.gosoft.go10.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import th.co.gosoft.go10.R;
import th.co.gosoft.go10.fragment.BoardContentFragment;
import th.co.gosoft.go10.fragment.SelectRoomFragment;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String LOG_TAG = "HomeActivityTag";
    private GoogleApiClient mGoogleApiClient;
    private ImageView profileImageView;
    private TextView profileName;
    private OptionalPendingResult<GoogleSignInResult> opr;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        try{
            prepareGmailLoginSession();
            FacebookSdk.sdkInitialize(this.getApplicationContext());

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            View headerLayout = navigationView.getHeaderView(0);

            profileImageView = (ImageView) headerLayout.findViewById(R.id.imgProfileImage);
            profileName = (TextView) headerLayout.findViewById(R.id.txtProfileName);

            sharedPref = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);

            Intent intent = getIntent();
            String _id = intent.getStringExtra("_id");
            if(_id == null || _id.equals("")){
                Log.i(LOG_TAG, "IF");
                inflateSelectRoomFragment();
            } else {
                Log.i(LOG_TAG, "ELSE");
                inflateBoardContentFragment(_id);
            }

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        initialUserProfile();
    }

    private void inflateSelectRoomFragment() {
        Fragment fragment = new SelectRoomFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    private void inflateBoardContentFragment(String _id) {
        Bundle data = new Bundle();
        data.putString("_id", _id);
        Fragment fragment = new BoardContentFragment();
        fragment.setArguments(data);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
    }

    private void initialUserProfile() {
        String avatarPicName = sharedPref.getString("avatarPic", "default_avatar");
        profileImageView.setImageResource(getResources().getIdentifier(avatarPicName , "drawable", getPackageName()));
        String avatarName = sharedPref.getString("avatarName", "Avatar Name");
        profileName.setText(avatarName);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(getFragmentManager().getBackStackEntryCount() == 0) {
                super.onBackPressed();
                Log.i(LOG_TAG,"if");
            }
            else {
                Log.i(LOG_TAG,"popbackstack");
                String str="";
                Log.i(LOG_TAG,"backStackName "+this.getFragmentManager().getBackStackEntryCount());
                FragmentManager.BackStackEntry backEntry = getFragmentManager().getBackStackEntryAt(this.getFragmentManager().getBackStackEntryCount()-1);
                str=backEntry.getName();

                Log.i(LOG_TAG,"backStackName "+str);
                if(str == "tag"){
                    for(int i = 0; i < getFragmentManager().getBackStackEntryCount(); ++i) {
                        getFragmentManager().popBackStack("tag",FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                }else{
                        getFragmentManager().popBackStack();
                }
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.setting) {
            Intent intent = new Intent(HomeActivity.this, SettingAvatar.class);
            startActivity(intent);
        } else if (id == R.id.logout) {

//            if(checkCurrentTokenFacebook()){
//                try{
//                    Log.i(LOG_TAG, "Logging out Facebook");
//                    LoginManager.getInstance().logOut();
//                    goToLoginActivity();
//
//                } catch (Exception e) {
//                    Log.e(LOG_TAG, e.getMessage(), e);
//                    throw new RuntimeException(e.getMessage(), e);
//                }
//            } else if(checkCurrentTokenGmail()){
//                Log.i(LOG_TAG, "Logging out Gmail");
//                try{
//                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
//                        new ResultCallback<Status>() {
//                            @Override
//                            public void onResult(Status status) {
//                                goToLoginActivity();
//                            }
//                        });
//                    goToLoginActivity();
//
//                } catch (Exception e) {
//                    Log.e(LOG_TAG, e.getMessage(), e);
//                    throw new RuntimeException(e.getMessage(), e);
//                }
//            }
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("hasLoggedIn", false);
            editor.commit();
            goToLoginActivity();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void prepareGmailLoginSession() {
        try{
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
            mGoogleApiClient.connect();
            Log.i(LOG_TAG, "prepareGmailLoginSession()");

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private boolean checkCurrentTokenFacebook() {
        if (AccessToken.getCurrentAccessToken() != null) {
            Log.i(LOG_TAG, "Facebook cached sign-in");

            return true;
        } else {
            Log.i(LOG_TAG, "Facebook cached not sign-in");
            return false;
        }
    }

    private boolean checkCurrentTokenGmail() {

        opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            Log.i(LOG_TAG, "Gmail cached sign-in");
            return true;

        } else {
            Log.i(LOG_TAG, "Gmail cached not sign-in");
            return false;
        }
    }
}
