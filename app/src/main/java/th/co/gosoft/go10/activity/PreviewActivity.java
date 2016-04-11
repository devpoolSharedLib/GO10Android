package th.co.gosoft.go10.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import gosoft.th.co.go10.R;

public class PreviewActivity extends UtilityActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
    }

    public void sendComment(View view) {
        Intent intent = new Intent(this, BoardUpdateCommentActivity.class);
        startActivity(intent);
    }
}
