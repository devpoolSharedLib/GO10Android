package th.co.gosoft.go10.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import gosoft.th.co.go10.R;

public class AddSelectMultiAvatarActivity extends Activity {
    GridView grid;
    String[] web = {
            "Google",
            "Github",
            "Instagram",
            "Facebook",
            "Flickr",
            "Pinterest",
            "Quora",
    } ;
    int[] imageId = {
            R.drawable.avatar12,
            R.drawable.avatar3,
            R.drawable.avatar5,
            R.drawable.avatar6,
            R.drawable.avatar7,
            R.drawable.avatar4,
            R.drawable.avatar11,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addselectmultiavartar);

        CustomGrid adapter = new CustomGrid(AddSelectMultiAvatarActivity.this, web, imageId);
        grid=(GridView)findViewById(R.id.grid);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
//              Toast.makeText(AddSelectMultiAvatarActivity.this, "You Clicked at " + web[+position], Toast.LENGTH_SHORT).show();

                Intent newActivity = new Intent(AddSelectMultiAvatarActivity.this, AddAvatarActivity.class);
                startActivity(newActivity);


            }
        });

    }

}