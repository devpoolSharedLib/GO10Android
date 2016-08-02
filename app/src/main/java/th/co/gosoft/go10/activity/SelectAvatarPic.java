package th.co.gosoft.go10.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import th.co.gosoft.go10.R;
import th.co.gosoft.go10.fragment.ManAvatarFragment;
import th.co.gosoft.go10.fragment.WomanAvatarFragment;
import th.co.gosoft.go10.model.UserModel;
import th.co.gosoft.go10.util.OnDataPass;

public class SelectAvatarPic extends AppCompatActivity implements OnDataPass {

    private final String LOG_TAG = "SelectAvatarPic";
    private final String URL = "http://go10webservice.au-syd.mybluemix.net/GO10WebService/api/user/updateUser";

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String avatarName;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private ProgressDialog progress;
    private boolean isSeparateUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_avatar_pic);

        sharedPref = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        Bundle extras = getIntent().getExtras();
        isSeparateUpdate = extras.getBoolean("isSeparateUpdate");

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ManAvatarFragment(), "Man");
        adapter.addFragment(new WomanAvatarFragment(), "Woman");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onDataPass(String data) {
        String[] splietString = data.split("th.co.gosoft.go10:drawable/");
        Log.i(LOG_TAG, "Select avatar ID : " + splietString[1]);
        this.avatarName = splietString[1];
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

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
                Log.i(LOG_TAG, "Save");
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
        editor.putString("avatarPic", avatarName);
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
        progress = ProgressDialog.show(this, null,
                "Processing", true);
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
