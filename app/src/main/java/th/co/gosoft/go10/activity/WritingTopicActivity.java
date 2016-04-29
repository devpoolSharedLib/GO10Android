package th.co.gosoft.go10.activity;

import android.app.Activity;
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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import th.co.gosoft.go10.R;
import th.co.gosoft.go10.fragment.BoardContentFragment;
import th.co.gosoft.go10.model.TopicModel;
import th.co.gosoft.go10.util.GO10Application;

public class WritingTopicActivity extends Activity {

    private final String LOG_TAG = "WritingTopicActivityTag";
    private final String URL = "http://go10webservice.au-syd.mybluemix.net/GO10WebService/api/topic/post";
    private ProgressDialog progress;
    private String room_id;
    private Bundle profileBundle;
    private Map<String, String> fragmentMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing_topic);

        Intent intent = getIntent();
        room_id = intent.getStringExtra("room_id");
        profileBundle = ((GO10Application) this.getApplication()).getBundle();
    }

    public void postTopic(View view) {

        EditText edtHostSubject = (EditText) findViewById(R.id.txtHostSubject);
        EditText edtHostContent = (EditText) findViewById(R.id.txtHostContent);
        Log.i(LOG_TAG, "Subject : " + edtHostSubject.getText().toString());
        Log.i(LOG_TAG, "Content : " + edtHostContent.getText().toString());

        TopicModel topicModel = new TopicModel();
        topicModel.setSubject(edtHostSubject.getText().toString());
        topicModel.setContent(edtHostContent.getText().toString());
        topicModel.setUser(getUsernameFromApplication());
        topicModel.setType("host");
        topicModel.setRoomId(room_id);
        Log.i(LOG_TAG, "Subject : "+edtHostSubject.getText().toString());
        Log.i(LOG_TAG, "content : "+edtHostContent.getText().toString());
        callPostWebService(topicModel);
    }

    private String getUsernameFromApplication() {
        return profileBundle.getString("name");
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
                            Log.i(LOG_TAG, "new id : "+new String(response));
                            closeLoadingDialog();

                            callNextActivity(new String(response));
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

    private void callNextActivity(String _id)
    {
        Intent intent = new Intent(this, BoardContentFragment.class);
        intent.putExtra("_id", _id);
        intent.putExtra("room_id", room_id);

        startActivity(intent);
    }

    private AlertDialog.Builder showErrorDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Error Occurred!!!");
        alert.setCancelable(true);
        return alert;
    }


}
