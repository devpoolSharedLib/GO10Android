package th.co.gosoft.go10.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import th.co.gosoft.go10.R;
import th.co.gosoft.go10.model.RoomModel;
import th.co.gosoft.go10.model.TopicModel;

/**
 * Created by manitkan on 27/06/16.
 */
public class RoomAdapter  extends ArrayAdapter<Map<String, Object>> {

    private final String LOG_TAG = "RoomAdapter";
    private Context context;

    public RoomAdapter(Context context, int resource, List<Map<String, Object>> items) {
        super(context, resource, items);
        this.context = context;
    }


    public View getView(int position, View convertView, ViewGroup parent) {

        try{
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater;
                inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.hot_topic_row, null);

                holder.txtRowSubject = (TextView) convertView.findViewById(R.id.rowSubject);
                holder.txtLikeCount = (TextView) convertView.findViewById(R.id.txtLikeCount);
                holder.txtRowDate = (TextView) convertView.findViewById(R.id.rowDate);
                holder.imageView = (ImageView) convertView.findViewById(R.id.iconImage);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Map<String, Object> topicMap = getItem(position);

            if (topicMap != null) {
                holder.txtRowSubject.setText(topicMap.get("subject").toString());
                holder.txtLikeCount.setText(topicMap.get("countLike") == null ? "0" : topicMap.get("countLike").toString());
                holder.txtRowDate.setText(topicMap.get("date").toString());
                holder.imageView.setImageResource(context.getResources().getIdentifier(topicMap.get("avatarPic").toString(), "drawable",
                        context.getPackageName()));
                if((Boolean) topicMap.get("statusRead") == false) {
                    convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorUnreadTopic));
                } else {
                    convertView.setBackgroundColor(0);
                }
            }

            return convertView;
        } catch (Exception e){
            Log.e(LOG_TAG, e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static class ViewHolder {
        TextView txtRowSubject;
        TextView txtLikeCount;
        TextView txtRowDate;
        ImageView imageView;
    }
}
