package th.co.gosoft.go10.adapter;

import android.content.Context;
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

public class HotTopicListAdapter extends ArrayAdapter<TopicModel> {

    private final String LOG_TAG = "HotTopicListAdapter";
    private Map<String, Integer> imageIdMap = new HashMap<>();

    public HotTopicListAdapter(Context context, int resource, List<TopicModel> items) {
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

            TopicModel topicModel = getItem(position);

            if (topicModel != null) {
                holder.txtRowSubject.setText(topicModel.getSubject());
                holder.txtLikeCount.setText(topicModel.getCountLike() == null ? "0" : String.valueOf(topicModel.getCountLike()));
                holder.txtRowDate.setText(topicModel.getDate());
            }

            holder.imageView.setImageResource(imageIdMap.get(topicModel.getRoomId()));
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

