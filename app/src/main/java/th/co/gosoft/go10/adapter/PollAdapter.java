package th.co.gosoft.go10.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import th.co.gosoft.go10.R;
import th.co.gosoft.go10.model.LikeModel;
import th.co.gosoft.go10.util.OnDataPass;
import th.co.gosoft.go10.util.PropertyUtility;

public class PollAdapter extends BaseAdapter {

    private final String LOG_TAG = "PollAdapter";
    private final String TYPE_HOST = "poll";

    private LayoutInflater layoutInflater;
    private List<Map> pollModelMap;
    private List<Map> choiceMaster;
    private List<Map> questionMaster;
    private Context context;;
    private Map<Integer,Integer> rowLayoutMap;
    private boolean questionOr;
    private ViewHolder holder = null;
    private String empEmail;
    private List<Map> questionModel;
    private OnDataPass onDataPass;


    public PollAdapter(Context context, List<Map> pollModel) {
//        this.layoutInflater = LayoutInflater.from(context);
        this.layoutInflater = LayoutInflater.from(context);
        this.pollModelMap =(List<Map>) pollModel;
        this.questionMaster = (List<Map>) pollModelMap.get(0).get("questionMaster");
        this.choiceMaster = (List<Map>) questionMaster.get(0).get("choiceMaster");
        this.context = context;
//        rowLayoutMap = new HashMap<>();
        Log.i(LOG_TAG," PollModel : "+pollModel);
        Log.i(LOG_TAG,"Size of Question "+questionMaster.size());
        Log.i(LOG_TAG,"Size of Choice "+choiceMaster.size());

//        rowLayoutMap.put(0, R.layout.activity_question);
//        rowLayoutMap.put(1, R.layout.activity_choice);

        Log.i(LOG_TAG,"MAP : "+pollModelMap);
//        Log.i(LOG_TAG,"Position :" +pollModel.get(1));
    }

    @Override
    public int getCount() {
        Log.i(LOG_TAG,"Size ; "+ pollModelMap.size());
//        return pollModelMap.size();
        return pollModelMap.size();
    }

    @Override
    public Object getItem(int position) {
        Log.i(LOG_TAG,"getPosition ; "+pollModelMap.get(position));
        return pollModelMap.get(position);
    }

    @Override
    public long getItemId(int position) {
        Log.i(LOG_TAG,"Position ; "+position);
        return position;
    }

//    @Override
//    public int getItemViewType(int position) {
//        return pollModelMap.get(position).get("type").equals(TYPE_HOST) ? 0 : 1 ;
//    }

//    @Override
//    public int getViewTypeCount() {
//        return rowLayoutMap.size();
//    }
    //    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        return null;
//    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i(LOG_TAG,"position : "+position);
        try {
            Log.i(LOG_TAG, "position : " + position);
            final Map<String, Object> pollModelMap = (Map<String, Object>) getItem(position);


            Log.i(LOG_TAG, "GetView PollModelMap" + pollModelMap);
            Log.i(LOG_TAG,"Item Type :" + pollModelMap.get("type"));
            if (convertView == null) {
                Log.i(LOG_TAG, "converView ");
                holder = new ViewHolder();
                for (int i = 1; i <= questionMaster.size(); i++) {
                    convertView = layoutInflater.inflate(R.layout.activity_question, null);
                    holder.questionTopic = (TextView) convertView.findViewById(R.id.questionTopic);
                    Log.i(LOG_TAG, "Question Layout");
                    if (i == questionMaster.size()){
                        holder.questionTopic.setText((String) questionMaster.get(0).get("questionTitle")) ;
                        Log.i(LOG_TAG,"Question Title: "+questionMaster.get(0).get("questionTitle"));
                    }
                    for (int j = 1 ; j <= choiceMaster.size(); j++) {
//                             View child = layoutInflater.inflate(R.layout.activity_choice, null);
                        Log.i(LOG_TAG, "Choice Layout");
                        holder.choice1 = (RadioButton) convertView.findViewById(R.id.choice1);
                        holder.choice2 = (RadioButton) convertView.findViewById(R.id.choice2);
                        holder.choice1.setText((String) choiceMaster.get(0).get("choiceTitle"));
                        holder.choice2.setText((String) choiceMaster.get(0).get("choiceTitle"));
                        Log.i(LOG_TAG, "Choice TiTle: " + choiceMaster.get(0).get("choiceTitle"));

                    }
                }
                convertView.setTag(holder);
            } else{
                holder = (PollAdapter.ViewHolder) convertView.getTag();
            }

//                if(){
//                holder.questionTopic.setText((String) questionMaster.get(0).get("questionTitle")) ;
//                holder.choiceTopic.setText((String) choiceMaster.get(0).get("choiceTitle"));
//                Log.i(LOG_TAG,"Question Title: "+questionMaster.get(0).get("questionTitle"));
//                } else{
//
////                    convertView = layoutInflater.inflate(R.layout.activity_choice,null);
//                    holder.choice1.setText((String) choiceMaster.get(0).get("choiceTitle"));
//                    Log.i(LOG_TAG, "Choice TiTle: " + choiceMaster.get(0).get("choiceTitle"));
//            }
            return convertView;
        }catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage() + e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }




    public class ViewHolder {
        TextView questionTopic;
        TextView choiceTopic;
        TextView pollDetail;
        TextView Date;
        TextView test;
        RadioButton choice1;
        RadioButton choice2;
        RadioButton choice3;
        RadioButton choice4;
        RadioButton choice5;
    }

}