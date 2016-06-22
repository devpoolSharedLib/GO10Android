package th.co.gosoft.go10.adapter;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import th.co.gosoft.go10.R;
import th.co.gosoft.go10.model.TopicModel;
import th.co.gosoft.go10.util.DownloadImageTask;

public class TopicAdapter extends BaseAdapter {

    private final String LOG_TAG = "TopicAdapter";
    private final String TYPE_HOST = "host";
    private final String TYPE_COMMENT = "comment";
    private Context context;

    private Map<Integer,Integer> rowLayoutMap;
    private List<TopicModel> topicModelList;

    public TopicAdapter(Context  context, List<TopicModel> topicModelList) {
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

        LayoutInflater layoutInflater =  LayoutInflater.from(context);

        TopicModel topicModel = (TopicModel) getItem(position);
        Log.i(LOG_TAG, "Item Type : "+topicModel.getType());

        ViewHolder holder = null;
        int rowType = getItemViewType(position);

        if (convertView == null) {
            holder = new ViewHolder();

            if(rowType == 0){
                convertView = layoutInflater.inflate(rowLayoutMap.get(0), null);
                holder.subject = (TextView) convertView.findViewById(R.id.hostSubject);
                holder.content = (WebView) convertView.findViewById(R.id.hostContent);
//                holder.content = (TextView) convertView.findViewById(R.id.hostContent);
                holder.user = (TextView) convertView.findViewById(R.id.hostUsername);
                holder.date = (TextView) convertView.findViewById(R.id.hostTime);
                holder.imageView =(ImageView) convertView.findViewById(R.id.hostImage);


            } else if(rowType == 1){
                convertView = layoutInflater.inflate(rowLayoutMap.get(1), null);
                holder.content = (WebView) convertView.findViewById(R.id.commentContent);
//                holder.content = (TextView) convertView.findViewById(R.id.commentContent);
                holder.user = (TextView) convertView.findViewById(R.id.commentUsername);
                holder.date = (TextView) convertView.findViewById(R.id.commentTime);
                holder.imageView =(ImageView) convertView.findViewById(R.id.commentImage);
            }

            convertView.setTag(holder);

        } else {
            Log.i(LOG_TAG, "else : "+position);
            holder = (ViewHolder) convertView.getTag();
        }

        if (rowType == 0){
            holder.subject.setText(topicModel.getSubject());
//            holder.content.setText(Html.fromHtml(topicModel.getContent()));
            holder.content.loadData(topicModel.getContent(), "text/html; charset=UTF-8", null);
            holder.user.setText(topicModel.getAvatarName());
            holder.date.setText(topicModel.getDate().toString());
            holder.imageView.setImageResource(context.getResources().getIdentifier(topicModel.getAvatarPic() , "drawable", context.getPackageName()));


        } else if(rowType == 1){
//            holder.content.setText(Html.fromHtml(topicModel.getContent()));
            holder.content.loadData(topicModel.getContent(), "text/html; charset=UTF-8", null);
            holder.user.setText(topicModel.getAvatarName());
            holder.date.setText(topicModel.getDate().toString());
            holder.imageView.setImageResource(context.getResources().getIdentifier(topicModel.getAvatarPic() , "drawable", context.getPackageName()));
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView subject;
        WebView content;
//        TextView content;
        TextView user;
        TextView date;
        ImageView imageView;
    }

}
