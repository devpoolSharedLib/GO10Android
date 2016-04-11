package th.co.gosoft.go10.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import gosoft.th.co.go10.R;
import th.co.gosoft.go10.model.TopicModel;
import th.co.gosoft.go10.util.Session;

public class WritingCommentActivity extends UtilityActivity {

    private final String LOG_TAG = "WritingCommentActivity";
    private final String URL = "http://liberty-java-2.mybluemix.net/GO10WebService/api/topic/post";
    private ProgressDialog progress;
    private Session session;
    private String _id ;
    private String room_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing_comment);

        Intent intent = getIntent();
        _id = intent.getStringExtra("_id");
        room_id = intent.getStringExtra("room_id");
    }

    public void sendComment(View view) {
        EditText edtCommentContent = (EditText) findViewById(R.id.txtCommentContent);
        Log.i(LOG_TAG, "Content : " + edtCommentContent.getText().toString());
        session = new Session(this);

        TopicModel topicModel = new TopicModel();
        topicModel.setTopicId(_id);
        topicModel.setContent(edtCommentContent.getText().toString());
        topicModel.setUser(session.getusename());
        topicModel.setType("comment");
        topicModel.setRoomId(room_id);

        callPostWebService(topicModel);
    }

    public void gotoPreviewPage(View view) {
        Intent intent = new Intent(this, PreviewActivity.class);
        startActivity(intent);
    }

    private void callPostWebService(TopicModel topicModel){

        try {
            String jsonString = new ObjectMapper().writeValueAsString(topicModel);
            Log.i(LOG_TAG, URL);
            Log.i(LOG_TAG, jsonString);

            AsyncHttpClient client = new AsyncHttpClient();
            client.post(this, URL, new StringEntity(jsonString, "utf-8"),
                    RequestParams.APPLICATION_JSON, new AsyncHttpResponseHandler() {

                        @Override
                        public void onStart() {
                            showLoadingDialog();
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                            Log.i(LOG_TAG, String.format(Locale.US, "Return Status Code: %d", statusCode));
                            Log.i(LOG_TAG, "New id : "+new String(response));
                            closeLoadingDialog();
                            callNextActivity(_id);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                            Log.e(LOG_TAG, String.format(Locale.US, "Return Status Code: %d", statusCode));
                            Log.e(LOG_TAG, "AsyncHttpClient returned error", e);
                        }
                    });

        } catch (JsonProcessingException e) {
            Log.e(LOG_TAG, "JsonProcessingException : "+e.getMessage(), e);
            showErrorDialog().show();
        }
    }

    private void showLoadingDialog() {
        progress = ProgressDialog.show(this, null,
                "Processing", true);
    }

    private void closeLoadingDialog(){
        progress.dismiss();
    }

    private void callNextActivity(String _id) {
        Intent intent = new Intent(this, BoardContentActivity.class);
        intent.putExtra("_id", _id);
        startActivity(intent);
    }

    private AlertDialog.Builder showErrorDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Error Occurred!!!");
        alert.setCancelable(true);
        return alert;
    }

}
