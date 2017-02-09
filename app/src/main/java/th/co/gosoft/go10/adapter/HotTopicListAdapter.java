package th.co.gosoft.go10.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
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
import th.co.gosoft.go10.model.TopicModel;

public class HotTopicListAdapter extends ArrayAdapter<Map<String, Object>> {

    private final String LOG_TAG = "HotTopicListAdapter";
    private Map<String, Integer> imageIdMap = new HashMap<>();

    public HotTopicListAdapter(Context context, int resource, List<Map<String, Object>> items) {
        super(context, resource, items);
        generateImageToMap(imageIdMap);
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
                holder.imageView.setImageResource(imageIdMap.get(topicMap.get("roomId").toString()));
                Log.i(LOG_TAG, "statusRead : "+topicMap.get("statusRead"));
                if((Boolean) topicMap.get("statusRead") == false) {
                    convertView.setBackgroundColor(Color.parseColor("#FBB2BC"));
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

    private void generateImageToMap(Map<String, Integer> imageIdMap) {
        imageIdMap.put("rm01", R.drawable.general);
        imageIdMap.put("rm02", R.drawable.tell);
        imageIdMap.put("rm03", R.drawable.game);
        imageIdMap.put("rm04", R.drawable.food);
        imageIdMap.put("rm05", R.drawable.stock);
        imageIdMap.put("rm06", R.drawable.travel);
        imageIdMap.put("rm07", R.drawable.it);
        imageIdMap.put("rm08", R.drawable.sport);
        imageIdMap.put("rm09", R.drawable.newbie);
        imageIdMap.put("rm10", R.drawable.talktoadmin);
    }

}

