package th.co.gosoft.go10.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import gosoft.th.co.go10.R;
import th.co.gosoft.go10.adapter.HostTopicListAdapter;
import th.co.gosoft.go10.adapter.RoomAdapter;
import th.co.gosoft.go10.model.RoomModel;
import th.co.gosoft.go10.model.TopicModel;

public class SelectRoomActivity extends UtilityActivity {

    private final String LOG_TAG = "SelectRoomActivity";
    private final String URL_HOT = "http://liberty-java-2.mybluemix.net/GO10WebService/api/topic/gethottopiclist";
    private final String URL_ROOM = "http://liberty-java-2.mybluemix.net/GO10WebService/api/room/get";
    private ProgressDialog progress;
    private List<TopicModel> topicModelList = new ArrayList<>();
    private List<RoomModel> roomModelList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_room);

//        callGetWebService();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
        ListView listViewSelectAvatar = (ListView)findViewById(R.id.listViewSelectAvatar);
        listViewSelectAvatar.setAdapter(null);
        GridView gridViewRoom = (GridView)findViewById(R.id.gridViewRoom);
        gridViewRoom.setAdapter(null);
        callGetWebService();
    }

    private void callGetWebService(){
        try {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(URL_HOT, new BaseJsonHttpResponseHandler() {

                @Override
                public void onStart() {
                    showLoadingDialog();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                    try {
                        topicModelList = (List<TopicModel>) parseResponse(rawJsonResponse, false);
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
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {
                    Log.e(LOG_TAG, "Error code : " + statusCode + ", " + throwable.getMessage());
                }

                @Override
                  protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    Log.i(LOG_TAG, ">>>>>>>>>>>>>>>>.. Json String : "+rawJsonData);
                    return new ObjectMapper().readValue(rawJsonData, new TypeReference<List<TopicModel>>() {});
                }

            });
            client.get(URL_ROOM, new BaseJsonHttpResponseHandler() {

                @Override
                public void onStart() {
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                    try {
                        roomModelList = (List<RoomModel>) parseResponse(rawJsonResponse, false);
                        generateGridView();
                        closeLoadingDialog();
                        Log.i(LOG_TAG, "Room Model List Size : " + roomModelList.size());

                    } catch (Throwable e) {
                        closeLoadingDialog();
                        Log.e(LOG_TAG, e.getMessage(), e);
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {
                    Log.e(LOG_TAG, "Error code : " + statusCode + ", " + throwable.getMessage());
                }

                @Override
                protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    Log.i(LOG_TAG, ">>>>>>>>>>>>>>>>.. Json String : "+rawJsonData);
                    return new ObjectMapper().readValue(rawJsonData, new TypeReference<List<RoomModel>>() {});
                }
            });

        } catch (Exception e) {
            Log.e(LOG_TAG, "RuntimeException : "+e.getMessage(), e);
            closeLoadingDialog();
            showErrorDialog().show();
        }
    }

    private void generateListView() {
        ListView hotTopicListView = (ListView)findViewById(R.id.listViewSelectAvatar);
        HostTopicListAdapter hostTopicListAdapter = new HostTopicListAdapter(this, R.layout.hot_topic_row, topicModelList);
        hotTopicListView.setAdapter(hostTopicListAdapter);

        hotTopicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                goBoardContentActivity(position);
            }
        });
    }

    private void generateGridView() {
        GridView gridView = (GridView) findViewById(R.id.gridViewRoom);
        RoomAdapter roomAdapter = new RoomAdapter(this, R.layout.room_grid, roomModelList);
        gridView.setAdapter(roomAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(LOG_TAG, ">>>>>>>>>>>>>>.. position : "+position);
                goRoomActivity(position);
            }
        });
    }

    private void goBoardContentActivity(int position) {
        try{
            Intent intent = new Intent(SelectRoomActivity.this, BoardContentActivity.class);
            intent.putExtra("_id", topicModelList.get(position).get_id());
            startActivity(intent);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void goRoomActivity(int position) {
        Log.i(LOG_TAG, ">>>>>>>>>>>>>>.. goRoomActivity");
        try{
            Intent intent = new Intent(SelectRoomActivity.this, RoomActivity.class);
            intent.putExtra("room_id", roomModelList.get(position).get_id());
            intent.putExtra("roomName", roomModelList.get(position).getName());
            Log.i(LOG_TAG, ">>>>>>>>>>>>>>.. build intent complete");
            startActivity(intent);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void addModelToTopicList(List<TopicModel> topicModelList, int size){
        int i=0;
        while (i<size){
            TopicModel topicModel = new TopicModel();
            topicModel.setSubject("subject"+(i+1));
            topicModel.setContent("content"+(i+1));
            topicModelList.add(topicModel);
            i++;
        }
    }

    private void addModelToRoomList(List<RoomModel> RoomModelList, int size){
        int i=0;
        while (i<size){
            RoomModel roomModel = new RoomModel();
            roomModel.set_id("rm0"+(i+1));
            roomModel.setName("Name" + (i + 1));
            roomModel.setDesc("Desc" + (i + 1));
            RoomModelList.add(roomModel);
            i++;
        }
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
