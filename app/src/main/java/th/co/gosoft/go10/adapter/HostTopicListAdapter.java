package th.co.gosoft.go10.adapter;

import android.content.Context;
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

    public HostTopicListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public HostTopicListAdapter(Context context, int resource, List<TopicModel> items) {
        super(context, resource, items);
    }


    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            LayoutInflater inflater;
            inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.hot_topic_row, null);
        }

        TopicModel topicModel = getItem(position);

        if (topicModel != null) {
            TextView txtRowSubject = (TextView) view.findViewById(R.id.rowSubject);
            TextView txtRowContent = (TextView) view.findViewById(R.id.rowContent);

            if (txtRowSubject != null) {
                txtRowSubject.setText(topicModel.getSubject());
            }

            if (txtRowContent != null) {
                txtRowContent.setText(topicModel.getContent());
            }
        }

        ImageView imageView = (ImageView) view.findViewById(R.id.iconImage);
//        imageView.setImageResource(R.drawable.hot_topic);
        if(topicModel.getRoomId().equals("rm01")){
            imageView.setImageResource(R.drawable.general);
        }else if(topicModel.getRoomId().equals("rm02")){
            imageView.setImageResource(R.drawable.it_knowledge);
        }else if(topicModel.getRoomId().equals("rm03")){
            imageView.setImageResource(R.drawable.sport);
        }


        return view;
    }

}

