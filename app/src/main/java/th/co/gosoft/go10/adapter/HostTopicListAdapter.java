package th.co.gosoft.go10.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import gosoft.co.th.go10.R;
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
        imageView.setImageResource(R.drawable.hot_topic);

        return view;
    }

}

