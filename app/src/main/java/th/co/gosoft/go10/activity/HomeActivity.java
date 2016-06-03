package th.co.gosoft.go10.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
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
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import th.co.gosoft.go10.R;
import th.co.gosoft.go10.fragment.BoardContentFragment;
import th.co.gosoft.go10.fragment.SelectRoomFragment;
import th.co.gosoft.go10.util.DownloadImageTask;
import th.co.gosoft.go10.util.GO10Application;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String LOG_TAG = "HomeActivityTag";
    private GoogleApiClient mGoogleApiClient;
    ImageView profileView;
    TextView profileName;
    AccessToken accessToken;
    OptionalPendingResult<GoogleSignInResult> opr;

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

            profileView = (ImageView) headerLayout.findViewById(R.id.imgProfileImage);
            profileName = (TextView) headerLayout.findViewById(R.id.txtProfileName);

            Bundle profileBundle = ((GO10Application) this.getApplication()).getBundle();
            if(profileBundle != null){
                Log.i(LOG_TAG, "Bundle not Null");
//                initialUserProfile(profileBundle);
                createBundleFromAvatar(profileBundle);
            } else {
                Log.i(LOG_TAG, "Bundle Null");
                if(checkCurrentTokenFacebook()) {
                    initialNewFacebookBundle();
                } else if(checkCurrentTokenGmail()) {
                    GoogleSignInResult result = opr.get();
                    handleSignInResult(result);
                }
            }

            Intent intent = getIntent();
            String _id = intent.getStringExtra("_id");
            if(_id == null || _id.equals("")){
                Log.i(LOG_TAG, "IF");
                inflateSelectRoomFragment();
            } else {
                Log.i(LOG_TAG, "ELSE");
                Bundle data = new Bundle();
                data.putString("_id", _id);
                Fragment fragment = new BoardContentFragment();
                fragment.setArguments(data);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
            }




        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void initialNewFacebookBundle() {
        Bundle params = new Bundle();
        params.putString("fields","id, name, email, gender, birthday, location");

        new GraphRequest(AccessToken.getCurrentAccessToken(), "me", params, HttpMethod.GET,
            new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse response) {
                    Log.i(LOG_TAG, "GraphResponse onCompleted");
                    if (response != null) {
                        try {
                            Log.i(LOG_TAG, "response not null");
                            JSONObject data = response.getJSONObject();
                            Bundle facebookBundle = createBundleFromFacebookObject(data);
                            addBundleToApplication(facebookBundle);
//                            initialUserProfile(facebookBundle);
                            createBundleFromAvatar(facebookBundle);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).executeAsync();
    }



    private void initialUserProfile(Bundle profileBundle) {

        if(profileBundle.getString("profile_pic") != null){
            Log.i(LOG_TAG, "Set Profile Image : "+profileBundle.getString("profile_pic"));
            new DownloadImageTask(profileView)
                    .execute(profileBundle.getString("profile_pic"));
        }

        if(profileBundle.getString("name") != null){
            Log.i(LOG_TAG, "Set Profile Name : "+profileBundle.getString("name"));
            profileName.setText(profileBundle.getString("name"));
        }

    }

    private void createBundleFromAvatar(Bundle bundleFacebook) {
        Bundle profilAvatar = new Bundle();
        if(bundleFacebook.getString("name").equals("Thounsand Touching")||bundleFacebook.getString("name").equals("Chiradechwiroj Jirapas")){
            profilAvatar.putString("name","Cristiano Ronaldo");
            profilAvatar.putString("profile_pic","http://go10webservice.au-syd.mybluemix.net/GO10WebService/images/Avatar/avatar_ronaldo.png");
        }else if(bundleFacebook.getString("name").equals("Mon Nit Kannika")){
            profilAvatar.putString("name", "Mary Jane");
            profilAvatar.putString("profile_pic", "http://go10webservice.au-syd.mybluemix.net/GO10WebService/images/Avatar/avatar_mary.png");
        }else{
            profilAvatar =  bundleFacebook;
        }

        addBundleToApplication(profilAvatar);
        initialUserProfile(profilAvatar);

    }

    private void inflateSelectRoomFragment() {
//        FragmentFactory fragmentFactory = new FragmentFactory();
//        Fragment fragment = fragmentFactory.getFactory(key);
//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.content_frame, fragment).addToBackStack("tag")
//                .commit();
        Fragment fragment = new SelectRoomFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
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
//                if(this.getFragmentManager().getBackStackEntryCount()>0) {
                    FragmentManager.BackStackEntry backEntry = getFragmentManager().getBackStackEntryAt(this.getFragmentManager().getBackStackEntryCount()-1);
                    str=backEntry.getName();
//                }

                Log.i(LOG_TAG,"backStackName "+str);
                if(str == "tag"){
                    Log.i(LOG_TAG,"xxxx");
                    for(int i = 0; i < getFragmentManager().getBackStackEntryCount(); ++i) {
                        getFragmentManager().popBackStack("tag",FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                }else{
                    Log.i(LOG_TAG,"yyy");
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
        } else if (id == R.id.logout) {

            if(checkCurrentTokenFacebook()){
                try{
                    Log.i(LOG_TAG, "Logging out Facebook");
                    LoginManager.getInstance().logOut();
                    goToLoginActivity();

                } catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    throw new RuntimeException(e.getMessage(), e);
                }
            } else if(checkCurrentTokenGmail()){
                Log.i(LOG_TAG, "Logging out Gmail");
                try{
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                goToLoginActivity();
                            }
                        });
                    goToLoginActivity();

                } catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
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

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(LOG_TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            Bundle googleBundle = createBundleFromGmailObject(acct);
            addBundleToApplication(googleBundle);
            initialUserProfile(googleBundle);
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

    private void addBundleToApplication(Bundle bundle) {
        Log.i(LOG_TAG, "addBundleToApplication()");
        ((GO10Application) this.getApplication()).setBundle(bundle);
    }
}
