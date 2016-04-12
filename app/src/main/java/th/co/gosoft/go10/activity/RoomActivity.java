package th.co.gosoft.go10.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import gosoft.th.co.go10.R;
import th.co.gosoft.go10.adapter.HostTopicListAdapter;
import th.co.gosoft.go10.model.TopicModel;

public class RoomActivity extends UtilityActivity {

    private final String LOG_TAG = "RoomActivity";
    private final String URL = "http://liberty-java-2.mybluemix.net/GO10WebService/api/topic/gettopiclistbyroom";
    private ProgressDialog progress;
    private Map<String, Integer> imageIdMap = new HashMap<>();
    private List<TopicModel> topicModelList = new ArrayList<>();

    private String room_id;
    private String roomName;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_room);

        Intent intent = getIntent();
        room_id = intent.getStringExtra("room_id");
        roomName = intent.getStringExtra("roomName");

        Log.i(LOG_TAG, "room_id : " + room_id);
        Log.i(LOG_TAG, "roomName : " + roomName);

//        try{
//            callGetWebService();
            generateImageToMap(imageIdMap);
            ImageView imageView = (ImageView)findViewById(R.id.roomIcon);
            imageView.setImageResource(imageIdMap.get(room_id));
            TextView txtRoomName = (TextView)findViewById(R.id.txtRoomName);
            txtRoomName.setText(roomName);

//        } catch (Throwable e) {
//            Log.e(LOG_TAG, e.getMessage(), e);
//            throw new RuntimeException(e.getMessage(), e);
//        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
        ListView topicListView = (ListView)findViewById(R.id.listViewTopic);
        topicListView.setAdapter(null);
        callGetWebService();
    }

    private void callGetWebService(){
        String concatString = URL+"?roomId="+room_id;
        try {
            AsyncHttpClient client = new AsyncHttpClient();
            Log.i(LOG_TAG, "client");
            client.get(concatString, new BaseJsonHttpResponseHandler<List<TopicModel>>() {

                @Override
                public void onStart() {
                    showLoadingDialog();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, List<TopicModel> response) {
                    try {
                        Log.i(LOG_TAG, "raw json : " + rawJsonResponse);
                        topicModelList = response;
                        generateListView();
                        closeLoadingDialog();
                        Log.i(LOG_TAG, "Topic Model List Size : " + topicModelList.size());

                    } catch (Throwable e) {
                        closeLoadingDialog();
                        Log.e(LOG_TAG, e.getMessage(), e);
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }


                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, List<TopicModel> errorResponse) {
                    Log.e(LOG_TAG, "Error code : " + statusCode + ", " + throwable.getMessage());
                }

                @Override
                protected List<TopicModel> parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    Log.i(LOG_TAG, ">>>>>>>>>>>>>>>>.. Json String : " + rawJsonData);
                    return new ObjectMapper().readValue(rawJsonData, new TypeReference<List<TopicModel>>() {
                    });
                }

            });
        } catch (Exception e) {
            Log.e(LOG_TAG, "RuntimeException : "+e.getMessage(), e);
            showErrorDialog().show();
        }
    }

    private void generateListView() {
        ListView topicListView = (ListView)findViewById(R.id.listViewTopic);
        HostTopicListAdapter hostTopicListAdapter = new HostTopicListAdapter(this, R.layout.hot_topic_row, topicModelList);
        topicListView.setAdapter(hostTopicListAdapter);

        topicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                goBoardContentActivity(position);
            }
        });
    }

    private void goBoardContentActivity(int position) {
        Intent intent = new Intent(RoomActivity.this, BoardContentActivity.class);
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("_id", topicModelList.get(position).get_id());
        startActivity(intent);
//        finish();
    }


    public void createNewTopic(View view) {
        Intent intent = new Intent(RoomActivity.this, WritingTopicActivity.class);
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra("room_id", room_id);
        startActivity(intent);

    }



    private void generateImageToMap(Map<String, Integer> imageIdMap) {
        imageIdMap.put("rm01", R.drawable.general);
        imageIdMap.put("rm02", R.drawable.it_knowledge);
        imageIdMap.put("rm03", R.drawable.sport);
    }

    private void showLoadingDialog() {
        progress = ProgressDialog.show(this, null,
                "Processing", true);
    }

    private void closeLoadingDialog(){
        progress.dismiss();
    }

    private AlertDialog.Builder showErrorDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Error Occurred!!!");
        alert.setCancelable(true);
        return alert;
    }

}