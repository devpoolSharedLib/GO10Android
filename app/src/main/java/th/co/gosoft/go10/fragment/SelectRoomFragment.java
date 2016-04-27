package th.co.gosoft.go10.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import th.co.gosoft.go10.R;
import th.co.gosoft.go10.adapter.HostTopicListAdapter;
import th.co.gosoft.go10.adapter.RoomAdapter;
import th.co.gosoft.go10.model.RoomModel;
import th.co.gosoft.go10.model.TopicModel;

public class SelectRoomFragment extends Fragment {

    private final String LOG_TAG = "SelectRoomFragmentTag";
    private final String URL_HOT = "http://go10webservice.au-syd.mybluemix.net/GO10WebService/api/topic/gethottopiclist";
    private final String URL_ROOM = "http://go10webservice.au-syd.mybluemix.net/GO10WebService/api/room/get";
    private ProgressDialog progress;
    private List<TopicModel> topicModelList = new ArrayList<>();
    private List<RoomModel> roomModelList = new ArrayList<>();

    private boolean isLoadTopicDone = false;
    private boolean isLoadRoomDone = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "onCreate()");
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_select_room, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
        ListView listViewSelectAvatar = (ListView) getView().findViewById(R.id.listViewSelectAvatar);
        listViewSelectAvatar.setAdapter(null);
        GridView gridViewRoom = (GridView) getView().findViewById(R.id.gridViewRoom);
        gridViewRoom.setAdapter(null);
        callGetWebService();
    }

    private void callGetWebService(){
        try {
            AsyncHttpClient client = new AsyncHttpClient();
            showLoadingDialog();
            client.get(URL_HOT, new BaseJsonHttpResponseHandler<List<TopicModel>>() {

                @Override
                public void onStart() {
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, List<TopicModel> response) {
                    try {
                        topicModelList = response;
                        generateListView();
                        isLoadTopicDone = true;
                        if(isLoadTopicDone && isLoadRoomDone){
                            closeLoadingDialog();
                        }
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
                    Log.i(LOG_TAG, ">>>>>>>>>>>>>>>>.. Json String : "+rawJsonData);
                    return new ObjectMapper().readValue(rawJsonData, new TypeReference<List<TopicModel>>() {});
                }

            });
            client.get(URL_ROOM, new BaseJsonHttpResponseHandler<List<RoomModel>>() {

                @Override
                public void onStart() {
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, List<RoomModel> response) {
                    try {
                        roomModelList = response;
                        generateGridView();
                        isLoadRoomDone = true;
                        if(isLoadTopicDone && isLoadRoomDone){
                            closeLoadingDialog();
                        }
                        Log.i(LOG_TAG, "Room Model List Size : " + roomModelList.size());

                    } catch (Throwable e) {
                        closeLoadingDialog();
                        Log.e(LOG_TAG, e.getMessage(), e);
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, List<RoomModel> errorResponse) {
                    Log.e(LOG_TAG, "Error code : " + statusCode + ", " + throwable.getMessage());
                }

                @Override
                protected List<RoomModel> parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
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
        ListView hotTopicListView = (ListView) getView().findViewById(R.id.listViewSelectAvatar);
        HostTopicListAdapter hostTopicListAdapter = new HostTopicListAdapter(getActivity(), R.layout.hot_topic_row, topicModelList);
        hotTopicListView.setAdapter(hostTopicListAdapter);

        hotTopicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                goBoardContentActivity(position);
            }
        });
    }

    private void generateGridView() {
        GridView gridView = (GridView)  getView().findViewById(R.id.gridViewRoom);
        RoomAdapter roomAdapter = new RoomAdapter(getActivity(), R.layout.room_grid, roomModelList);
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
            Bundle data = new Bundle();
            data.putString("_id", topicModelList.get(position).get_id());
            Fragment fragment = new BoardContentFragment();
            fragment.setArguments(data);
            FragmentManager fragmentManager = getFragmentManager();
//            FragmentTransaction tx = fragmentManager.beginTransaction();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    private void goRoomActivity(int position) {
        Log.i(LOG_TAG, ">>>>>>>>>>>>>>.. goRoomActivity");
        try{
            Bundle data = new Bundle();
            data.putString("room_id", roomModelList.get(position).get_id());
            Log.i(LOG_TAG, ">>>>>>>>> room_id " +data.getString("room_id"));
            data.putString("roomName", roomModelList.get(position).getName());
            Log.i(LOG_TAG, ">>>>>>>>> roomName " +data.getString("roomName"));
            Fragment fragment = new RoomFragment();
            fragment.setArguments(data);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();


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
        progress = ProgressDialog.show(getActivity(), null,
                "Processing", true);
    }

    private void closeLoadingDialog(){
        progress.dismiss();
    }

    private AlertDialog.Builder showErrorDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setMessage("Error Occurred!!!");
        alert.setCancelable(true);
        return alert;
    }
}
