package th.co.gosoft.go10.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import gosoft.th.co.go10.R;
import th.co.gosoft.go10.util.Session;



//import com.ibm.mobilefirstplatform.clientsdk.android.security.facebookauthentication.FacebookAuthenticationManager;

public class LoginActivity extends Activity {

    private Button btnGotoLogin, btnGotoRegister, btnLoginFB;
    private EditText edtUserName, edtPassword;
    private Session session;
    private final String LOG_TAG = "LoginActivityLOG";
    CallbackManager callbackManager;



//    @Override
//    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
//
//        View view = inflater.inflate(R.layout.activity_login, container, false);
//
//        btnLoginFB = (LoginButton) view.findViewById(R.id.btnLoginFacebook);
//        btnLoginFB.setReadPermissions("email");
//        // If using in a fragment
//        btnLoginFB.setFragment(this);
//        btnLoginFB.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                // do something
//            }
//        });
//        return view;
//    }

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);


        edtUserName = (EditText) findViewById(R.id.edtUserName);
        edtPassword = (EditText) findViewById(R.id.edtPassword);


        btnGotoLogin = (Button) findViewById(R.id.btnLogin);
        btnLoginFB = (Button) findViewById(R.id.btnLoginFacebook);


//        btnGotoRegister = (Button) findViewById(R.id.btnRegis);

//        btnLoginFB.setFragment(this);
//        btnLoginFB.setReadPermissions(Arrays.asList("user_status"));

        callbackManager = CallbackManager.Factory.create();


        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.i(LOG_TAG, "onSuccess");

                }

                    @Override
                    public void onCancel() {
                        Log.i(LOG_TAG, "onCancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.i(LOG_TAG, "onError");
                    }
                });


//        btnLoginFacebook.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View v) {
//
//                    try {
//                        BMSClient.getInstance().initialize(getApplicationContext(),
//                                "https://mobileAccess.au-syd.mybluemix.net",
//                                "6921b19c-aec2-4a8f-8362-7b596bbd4c42");
//                    }catch (MalformedURLException e) {
//                        e.printStackTrace();
//                    }
//                FacebookAuthenticationManager.getInstance().logout(getApplicationContext(), null);
//                FacebookAuthenticationManager.getInstance().register(LoginActivity.this);
//                Log.i(LOG_TAG, "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
//
//                Log.i(LOG_TAG, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//                Log.i(LOG_TAG, AuthorizationManager.getInstance().getUserIdentity().toString());
//                Intent newActivity = new Intent(LoginActivity.this, SelectRoomActivity.class);
//                startActivity(newActivity);



//                }
//        });

//        Request request = new Request("/protected", Request.GET);
//        request.send(LoginActivity.this, new ResponseListener() {
//            @Override
//            public void onSuccess (Response response) {
//                Log.i(LOG_TAG, "onSuccess :: " + response.getResponseText());
//                Intent newActivity = new Intess :: " + response.getResponseText());
//                Log.i(LOG_TAG, AuthorizationManager.getInstance().getUserIdentity().toString());
//                Intent newActivity = new Intent(LoginActivity.this, SelectRoomActivity.class);
//                startActivity(newActivity);
//            }
//            @Override
//            public void onFailure (Response response, Throwable t, JSONObject extendedInfo) {
//                if (null != t) {
//                    Log.e(LOG_TAG, "onFailure :: " + t.getMessage());
//                } else if (null != extendedInfo) {
//                    Log.e(LOG_TAG, "onFailure :: " + extendedInfo.toString());
//                } else {
//                    Log.e(LOG_TAG, "onFailure :: " + response.getResponseText());
//                }
//            }
//        });

//        FacebookAuthenticationManager.getInstance().logout(getApplicationContext(), null);

        btnGotoLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (edtUserName.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, "Please Insert Username", Toast.LENGTH_SHORT).show();
                } else {
                    session = new Session(LoginActivity.this);
                    session.setusename(edtUserName.getText().toString());

                    Intent newActivity = new Intent(LoginActivity.this, SelectRoomActivity.class);
                    startActivity(newActivity);
                }
            }
        });

//        btnGotoRegister.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent RegisterActivity = new Intent(LoginActivity.this, RegisterActivity.class);
//                startActivity(RegisterActivity);
//            }
//        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.i(LOG_TAG, "onActivityResult");
    }

}