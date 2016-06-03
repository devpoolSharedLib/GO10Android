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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import th.co.gosoft.go10.R;
import th.co.gosoft.go10.model.TopicModel;
import th.co.gosoft.go10.util.GO10Application;

public class WritingTopicFragment extends Fragment {

    private final String LOG_TAG = "WritingTopicFragmentTag";
    private final String URL = "http://go10webservice.au-syd.mybluemix.net/GO10WebService/api/topic/post";
    private ProgressDialog progress;
    private String room_id;
    private Bundle profileBundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "WritingTopicFragment");
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.activity_writing_topic, container, false);
        Bundle bundle = getArguments();
        room_id = bundle.getString("room_id");
        Log.i(LOG_TAG, "room_id : " + room_id);
        profileBundle = ((GO10Application) getActivity().getApplication()).getBundle();
        Button button = (Button) view.findViewById(R.id.btnPost);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditText edtHostSubject = (EditText) getView().findViewById(R.id.txtHostSubject);
                EditText edtHostContent = (EditText) getView().findViewById(R.id.txtHostContent);
                Log.i(LOG_TAG, "Subject : " + edtHostSubject.getText().toString());
                Log.i(LOG_TAG, "Content : " + edtHostContent.getText().toString());

                if(isEmpty(edtHostSubject) || isEmpty(edtHostContent)){
                    Log.i(LOG_TAG, "empty subject & message");
                    Toast.makeText(getActivity(), "Please enter your subject and comment message.", Toast.LENGTH_SHORT).show();
                } else {
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

            }
        });
        return view;
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
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
            client.post(getActivity(), URL, new StringEntity(jsonString, "utf-8"),
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
        progress = ProgressDialog.show(getActivity(), null,
                "Processing", true);
    }

    private void closeLoadingDialog(){
        progress.dismiss();
    }

    private void callNextActivity(String _id) {
        Bundle data = new Bundle();
        data.putString("_id", _id);
        data.putString("room_id", room_id);
        Fragment fragment = new BoardContentFragment();
        fragment.setArguments(data);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("tag").commit();
    }

    private AlertDialog.Builder showErrorDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setMessage("Error Occurred!!!");
        alert.setCancelable(true);
        return alert;
    }

}
