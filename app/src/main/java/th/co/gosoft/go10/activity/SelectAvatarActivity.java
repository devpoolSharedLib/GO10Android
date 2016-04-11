package th.co.gosoft.go10.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import gosoft.th.co.go10.R;

public class SelectAvatarActivity extends UtilityActivity {

    Integer[] arrImg = {
            R.drawable.em1,
            R.drawable.avatar12,
            R.drawable.avatar3,
            R.drawable.avatar5,
            R.drawable.avatar6,
            R.drawable.avatar7,
            R.drawable.avatar4,
            R.drawable.avatar11
    };

    private String _id ;
    private String action;
    private String room_Id;

    private Button btnGotoAddAvatar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectavatarlist);

        Intent intent = getIntent();
        _id = intent.getStringExtra("_id");
        action = intent.getStringExtra("action");

        final ListView lstView1 = (ListView)findViewById(R.id.listViewSelectAvatar);
        lstView1.setAdapter(new ImageAdapter(this));
        lstView1.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if(action.equals("new topic")){

                } else {
                    Intent newIntent = new Intent(SelectAvatarActivity.this, WritingCommentActivity.class);
                    newIntent.putExtra("_id", _id);
                    startActivity(newIntent);
                }
            }
        });

    }

    public class ImageAdapter extends BaseAdapter
    {
        private Context context;

        public ImageAdapter(Context c) {
            context = c;
        }

        public int getCount() {
            return arrImg.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.activity_selectavatarcolumn, null);
            }

            // #ColImage
            ImageView imageView = (ImageView) convertView.findViewById(R.id.ColImage);
            //imageView.getLayoutParams().height = 100;
            //imageView.getLayoutParams().width = 100;
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(arrImg[position]);

            // #ColName
            TextView txtPosition = (TextView) convertView.findViewById(R.id.txttopic);
            //txtPosition.setPadding(10, 0, 0, 0);
            txtPosition.setText("Avatar : " + position);

            // #ColDescription
            TextView txtPicName = (TextView) convertView.findViewById(R.id.ColDescription);
            //txtPicName.setPadding(50, 0, 0, 0);
            //int picID = getResources().getIdentifier(, null, null);
            //String imageRes = getResources().getResourceName(arrImg[position]);
            //String fileName = imageRes.substring(imageRes.lastIndexOf('/') + 1, imageRes.length());
            txtPicName.setText("Description : " + ".......");

            return convertView;
        }

    }


}