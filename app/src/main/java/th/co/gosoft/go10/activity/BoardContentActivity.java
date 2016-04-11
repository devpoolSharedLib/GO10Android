package th.co.gosoft.go10.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import gosoft.th.co.go10.R;
import th.co.gosoft.go10.adapter.TopicAdapter;
import th.co.gosoft.go10.model.TopicModel;

public class BoardContentActivity extends UtilityActivity {

    private final String LOG_TAG = "BoardContentActivity";
    private final String URL = "http://liberty-java-2.mybluemix.net/GO10WebService/api/topic/gettopicbyid";
    private ProgressDialog progress;
    private String _id ;
    private String room_id ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "Oncreate : BoardContentActivity");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_content);

        Intent intent = getIntent();
        _id = intent.getStringExtra("_id");
        Log.i(LOG_TAG, "_id : " + _id);

//        callGetWebService();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
        ListView commentListView = (ListView)findViewById(R.id.commentListView);
        commentListView.setAdapter(null);
        callGetWebService();
    }

    private void callGetWebService(){
        String concatString = URL+"?topicId="+_id;

        try {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(concatString, new BaseJsonHttpResponseHandler<List<TopicModel>>() {

                @Override
                public void onStart() {
                    showLoadingDialog();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, List<TopicModel> response) {
                    try {
                        List<TopicModel> topicModelList = (List<TopicModel>) parseResponse(rawJsonResponse, false);
                        room_id = topicModelList.get(0).getRoomId();
                        generateListView(topicModelList);
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
                    Log.i(LOG_TAG, ">>>>>>>>>>>>>>>>.. Json String : "+rawJsonData);
                    return new ObjectMapper().readValue(rawJsonData, new TypeReference<List<TopicModel>>() {});
                }

            });
        } catch (Exception e) {
            Log.e(LOG_TAG, "RuntimeException : "+e.getMessage(), e);
            showErrorDialog().show();
        }
    }

    private void generateListView(List<TopicModel> topicModelList) {
        ListView commentListView = (ListView) findViewById(R.id.commentListView);
        TopicAdapter commentAdapter = new TopicAdapter(this, topicModelList);
        commentListView.setAdapter(commentAdapter);
    }

    public void gotoCommentPage(View view) {
        Intent intent = new Intent(this, WritingCommentActivity.class);
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra("_id", _id);
        intent.putExtra("room_id", room_id);
        startActivity(intent);
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
