package th.co.gosoft.go10.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import gosoft.th.co.go10.R;
import th.co.gosoft.go10.util.Session;

public class LoginActivity extends Activity {

    private Button btnGotoLogin, btnGotoRegister;
    private EditText edtUserName, edtPassword;
    private Session session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUserName = (EditText) findViewById(R.id.edtUserName);
        edtPassword = (EditText) findViewById(R.id.edtPassword);


        btnGotoLogin = (Button) findViewById(R.id.btnLogin);
//        btnGotoRegister = (Button) findViewById(R.id.btnRegis);



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
            // });
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
}