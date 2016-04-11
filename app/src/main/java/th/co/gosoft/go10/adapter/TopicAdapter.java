package th.co.gosoft.go10.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gosoft.th.co.go10.R;
import th.co.gosoft.go10.model.TopicModel;

public class TopicAdapter extends BaseAdapter {

    private final String LOG_TAG = "TopicAdapter";
    private final String TYPE_HOST = "host";
    private final String TYPE_COMMENT = "comment";
    private Context context;

    private Map<Integer,Integer> rowLayoutMap;
    private List<TopicModel> topicModelList;

    public TopicAdapter(Context context, List<TopicModel> topicModelList) {
        this.topicModelList = topicModelList;
        this.context = context;
        rowLayoutMap = new HashMap<>();
        rowLayoutMap.put(0, R.layout.host_row);
        rowLayoutMap.put(1, R.layout.comment_row);
    }

    @Override
    public int getCount() {
        return topicModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return topicModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return (topicModelList.get(position).getType().equals(TYPE_HOST)) ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() {
        return rowLayoutMap.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i(LOG_TAG, "Position : "+position);

        View view = convertView;
        LayoutInflater layoutInflater =  LayoutInflater.from(context);

//        if (view == null) {
//            layoutInflater = LayoutInflater.from(context);
//        }

        TopicModel topicModel = (TopicModel) getItem(position);
        Log.i(LOG_TAG, "Item Type : "+topicModel.getType());

        if(getItemViewType(position) == 0){
            Log.i(LOG_TAG, "HOST : 0");
            Log.i(LOG_TAG,rowLayoutMap.get(0)+"");
            view = layoutInflater.inflate(rowLayoutMap.get(0), null);

            TextView hostSubject = (TextView) view.findViewById(R.id.hostSubject);
            hostSubject.setText(topicModel.getSubject());

            TextView hostContent = (TextView) view.findViewById(R.id.hostContent);
            hostContent.setText(topicModel.getContent());

            TextView hostUser = (TextView) view.findViewById(R.id.hostUsername);
            hostUser.setText(topicModel.getUser());

            TextView hostDate = (TextView) view.findViewById(R.id.hostTime);
            hostDate.setText(topicModel.getDate().toString());

        } else {
            Log.i(LOG_TAG, "COMMENT : 1");
            view = layoutInflater.inflate(rowLayoutMap.get(1), null);

            TextView tt1 = (TextView) view.findViewById(R.id.commentContent);
            tt1.setText(topicModel.getContent());

            TextView tt2 = (TextView) view.findViewById(R.id.commentUsername);
            tt2.setText(topicModel.getUser());

            TextView tt3 = (TextView) view.findViewById(R.id.commentTime);
            tt3.setText(topicModel.getDate().toString());
        }

        return view;
    }

}
