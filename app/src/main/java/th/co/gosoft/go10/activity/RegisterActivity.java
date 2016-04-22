package th.co.gosoft.go10.activity;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.TextView;

import gosoft.co.th.go10.R;


public class RegisterActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                setContentView(R.layout.register_activity);
        TextView loginScreen = (TextView) findViewById(R.id.link_to_login);

        loginScreen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                RegisterActivity.this.startActivity(intent);
                finish();
            }
        });
    }



}

