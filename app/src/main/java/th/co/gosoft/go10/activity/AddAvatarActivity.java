package th.co.gosoft.go10.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import gosoft.th.co.go10.R;

public class AddAvatarActivity extends UtilityActivity {

    private Button btnGotoSelectAvatarActivity;
    private Button btnGotoSelectMultiAvatarActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addavatar);

        btnGotoSelectAvatarActivity = (Button) findViewById(R.id.btnSaveAvatar);
        btnGotoSelectAvatarActivity.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
             //   Intent newActivity = new Intent(AddAvatarActivity.this, SelectAvatarActivity.class);
             //   startActivity(newActivity);
                Toast.makeText(AddAvatarActivity.this, "Save Completed", Toast.LENGTH_SHORT).show();
            }
        });

        btnGotoSelectMultiAvatarActivity = (Button) findViewById(R.id.btnChooseAvatar);
        btnGotoSelectMultiAvatarActivity.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent newActivity = new Intent(AddAvatarActivity.this, AddSelectMultiAvatarActivity.class);
                startActivity(newActivity);
            }
        });
    }
}
