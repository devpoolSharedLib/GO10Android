package th.co.gosoft.go10.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import th.co.gosoft.go10.R;
import th.co.gosoft.go10.model.TopicModel;

public class HostTopicListAdapter extends ArrayAdapter<TopicModel> {

    private final String LOG_TAG = "HostTopicListAdapter";

    public HostTopicListAdapter(Context context, int resource, List<TopicModel> items) {
        super(context, resource, items);
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
                holder.txtRowUserName = (TextView) convertView.findViewById(R.id.rowUserName);
                holder.txtRowDate = (TextView) convertView.findViewById(R.id.rowDate);
                holder.imageView = (ImageView) convertView.findViewById(R.id.iconImage);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            TopicModel topicModel = getItem(position);

            if (topicModel != null) {
                holder.txtRowSubject.setText(topicModel.getSubject());
                holder.txtRowUserName.setText(topicModel.getAvatarName());
                holder.txtRowDate.setText(topicModel.getDate());
            }

            if(topicModel.getRoomId().equals("rm01")){
                holder.imageView.setImageResource(R.drawable.general);
            }else if(topicModel.getRoomId().equals("rm02")){
                holder.imageView.setImageResource(R.drawable.it_knowledge);
            }else if(topicModel.getRoomId().equals("rm03")){
                holder.imageView.setImageResource(R.drawable.sport);
            }
            return convertView;
        } catch (Exception e){
            Log.e(LOG_TAG, e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static class ViewHolder {
        TextView txtRowSubject;
        TextView txtRowUserName;
        TextView txtRowDate;
        ImageView imageView;
    }

}

