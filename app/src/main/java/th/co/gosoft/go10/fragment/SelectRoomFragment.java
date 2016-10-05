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
import android.widget.LinearLayout;
import android.widget.ListView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import th.co.gosoft.go10.R;
import th.co.gosoft.go10.adapter.HotTopicListAdapter;
import th.co.gosoft.go10.adapter.RoomGridAdapter;
import th.co.gosoft.go10.model.RoomModel;
import th.co.gosoft.go10.model.TopicModel;
import th.co.gosoft.go10.util.PropertyUtility;

public class SelectRoomFragment extends Fragment {

    private final String LOG_TAG = "SelectRoomFragmentTag";

    private String URL_HOT;
    private String URL_ROOM;
    private ProgressDialog progress;
    private List<TopicModel> topicModelList;
    private List<RoomModel> roomModelList;
    private ListView hotTopicListView;
    private LinearLayout linearRoom;
    private boolean isLoadTopicDone = false;
    private boolean isLoadRoomDone = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "onCreate()");

        URL_HOT = PropertyUtility.getProperty("httpUrlSite", getActivity())+"GO10WebService/api/topic/gethottopiclist";
        URL_ROOM = PropertyUtility.getProperty("httpUrlSite", getActivity())+"GO10WebService/api/room/get";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreateView()");
        return inflater.inflate(R.layout.activity_select_room, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try{
            hotTopicListView = (ListView) getView().findViewById(R.id.listViewSelectAvatar);
            linearRoom = (LinearLayout) getView().findViewById(R.id.linearRoom);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart()");
        hotTopicListView.setAdapter(null);
        callGetWebService();
    }

    private void callGetWebService(){
        try {
            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Cache-Control", "no-cache");
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
        HotTopicListAdapter hostTopicListAdapter = new HotTopicListAdapter(getActivity(), R.layout.hot_topic_row, topicModelList);
        hotTopicListView.setAdapter(hostTopicListAdapter);
        hotTopicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                goBoardContentActivity(position);
            }
        });
    }

    private void generateGridView() {
        try{
            Log.i(LOG_TAG, "Generate Grid View");
            linearRoom.removeAllViews();
            RoomGridAdapter roomAdapter = new RoomGridAdapter(getActivity(), R.layout.room_grid, roomModelList);
            for (int i = 0; i < roomModelList.size(); i++) {
                View view = roomAdapter.getView(i, null, null);
                final int index = i;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goRoomActivity(index);
                    }
                });
                linearRoom.addView(view);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void goBoardContentActivity(int position) {
        try{
            Bundle data = new Bundle();
            data.putString("_id", topicModelList.get(position).get_id());
            Fragment fragment = new BoardContentFragment();
            fragment.setArguments(data);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("tag").commit();

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void goRoomActivity(int position) {
        Log.i(LOG_TAG, ">>>>>>>>>>>>>>.. goRoomActivity");
        try{
            Bundle data = new Bundle();
            Log.i(LOG_TAG, ">>>>>>>>> room_id " +data.getString("room_id"));
            Log.i(LOG_TAG, ">>>>>>>>> roomName " +data.getString("roomName"));

            data.putString("room_id", roomModelList.get(position).get_id());
            data.putString("roomName", roomModelList.get(position).getName());
            Fragment fragment = new RoomFragment();
            fragment.setArguments(data);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();


        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
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
        alert.setMessage("Error while loading content.");
        alert.setCancelable(true);
        return alert;
    }
}
