package th.co.gosoft.go10.activity;

import android.app.Activity;
import android.content.Intent;

import android.view.Menu;
import android.view.MenuItem;

import gosoft.co.th.go10.R;

public class UtilityActivity extends Activity {

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_avatar:
                Intent addAvatar = new Intent(UtilityActivity.this, AddAvatarActivity.class);
                UtilityActivity.this.startActivity(addAvatar);
                return true;

            case R.id.action_all_avatar:
                Intent allAvatar = new Intent(UtilityActivity.this, AllAvatarActivity.class);
                UtilityActivity.this.startActivity(allAvatar);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
