package th.co.gosoft.go10.adapter;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import th.co.gosoft.go10.R;
import th.co.gosoft.go10.model.LikeModel;
import th.co.gosoft.go10.util.LikeButtonOnClick;
import th.co.gosoft.go10.util.OnDataPass;
import th.co.gosoft.go10.util.URLImageParser;

public class TopicAdapter extends BaseAdapter {

    private final String LOG_TAG = "TopicAdapter";
    private final String TYPE_HOST = "host";

    private Context context;
    private Map<Integer,Integer> rowLayoutMap;
    private List<Map> topicModelMapList;
    private LikeModel likeModel;
    private boolean isClick = false;
    private ViewHolder holder = null;
    private OnDataPass onDataPass;

    public TopicAdapter(Context context,  OnDataPass onDataPass, List<Map> topicModelMapList, LikeModel likeModel) {
        this.topicModelMapList = topicModelMapList;
        this.context = context;
        this.likeModel = likeModel;
        this.onDataPass = onDataPass;
        rowLayoutMap = new HashMap<>();
        rowLayoutMap.put(0, R.layout.host_row);
        rowLayoutMap.put(1, R.layout.comment_row);
    }

    @Override
    public int getCount() {
        return topicModelMapList.size();
    }

    @Override
    public Object getItem(int position) {
        return topicModelMapList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return (topicModelMapList.get(position).get("type").equals(TYPE_HOST)) ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() {
        return rowLayoutMap.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            Log.i(LOG_TAG, "Position : "+position);
            LayoutInflater layoutInflater =  LayoutInflater.from(context);

            Map<String, Object> topicModelMap = (Map<String, Object>) getItem(position);
            Log.i(LOG_TAG, "Item Type : "+topicModelMap.get("type"));

            int rowType = getItemViewType(position);
            if (convertView == null) {
                holder = new ViewHolder();
                if(rowType == 0) {
                    convertView = layoutInflater.inflate(rowLayoutMap.get(0), null);
                    holder.subject = (TextView) convertView.findViewById(R.id.hostSubject);
                    holder.content = (TextView) convertView.findViewById(R.id.hostContent);
                    holder.user = (TextView) convertView.findViewById(R.id.hostUsername);
                    holder.date = (TextView) convertView.findViewById(R.id.hostTime);
                    holder.likeCount = (TextView) convertView.findViewById(R.id.txtLikeCount);
                    holder.imageView =(ImageView) convertView.findViewById(R.id.hostImage);
                    holder.btnLike = (Button) convertView.findViewById(R.id.btnLike);
                    holder.btnComment = (Button) convertView.findViewById(R.id.btnComment);
                } else if(rowType == 1) {
                    convertView = layoutInflater.inflate(rowLayoutMap.get(1), null);
                    holder.content = (TextView) convertView.findViewById(R.id.commentContent);
                    holder.user = (TextView) convertView.findViewById(R.id.commentUsername);
                    holder.date = (TextView) convertView.findViewById(R.id.commentTime);
                    holder.imageView =(ImageView) convertView.findViewById(R.id.commentImage);
                }
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (rowType == 0) {
                Log.i(LOG_TAG, "row type : 0");
                holder.subject.setText((String) topicModelMap.get("subject"));
                URLImageParser urlImageParser = new URLImageParser(holder.content, this.context);
                Spanned htmlSpan = Html.fromHtml((String) topicModelMap.get("content"), urlImageParser, null);
                Log.i(LOG_TAG, "row type : 00");
                holder.content.setText(htmlSpan);
                holder.user.setText((String) topicModelMap.get("avatarName"));
                Log.i(LOG_TAG, "row type : 01");
                holder.date.setText((String) topicModelMap.get("date"));
                Log.i(LOG_TAG, "row type : 02");
                Log.i(LOG_TAG, "countLike : "+topicModelMap.get("countLike"));
                holder.likeCount.setText(String.valueOf((Integer) topicModelMap.get("countLike")));
                Log.i(LOG_TAG, "row type : 03");
                holder.imageView.setImageResource(context.getResources().getIdentifier((String) topicModelMap.get("avatarPic") , "drawable", context.getPackageName()));
                Log.i(LOG_TAG, "row type : 04");
                if(likeModel != null && likeModel.isStatusLike()){
                    holder.btnLike.setTextColor(this.context.getResources().getColor(R.color.colorLikeButton));
                    isClick = true;
                    Log.i(LOG_TAG, "row type : 044");
                }
                holder.btnLike.setOnClickListener(new LikeButtonOnClick(this.context, holder.btnLike, holder.likeCount, isClick));
                holder.btnComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onDataPass.onDataPass(null);
                        Log.i(LOG_TAG, "row type : 055");
                    }
                });
                Log.i(LOG_TAG, "row type : 05");
            } else if(rowType == 1) {
                Log.i(LOG_TAG, "row type : 1");
                URLImageParser urlImageParser = new URLImageParser(holder.content, this.context);
                Spanned htmlSpan = Html.fromHtml((String) topicModelMap.get("content"), urlImageParser, null);
                Log.i(LOG_TAG, "row type : 10");
                holder.content.setText(htmlSpan);
                holder.user.setText((String) topicModelMap.get("avatarName"));
                Log.i(LOG_TAG, "row type : 11");
                holder.date.setText((String) topicModelMap.get("date"));
                Log.i(LOG_TAG, "row type : 12");
                holder.imageView.setImageResource(context.getResources().getIdentifier((String) topicModelMap.get("avatarPic") , "drawable", context.getPackageName()));
                Log.i(LOG_TAG, "row type : 13");
            }
            return convertView;
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage() + e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static class ViewHolder {
        TextView subject;
        TextView content;
        TextView user;
        TextView date;
        TextView likeCount;
        ImageView imageView;
        Button btnLike;
        Button btnComment;
    }

}
