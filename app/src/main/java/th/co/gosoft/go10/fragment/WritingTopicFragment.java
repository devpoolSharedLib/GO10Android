package th.co.gosoft.go10.fragment;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import jp.wasabeef.richeditor.RichEditor;
import th.co.gosoft.go10.R;
import th.co.gosoft.go10.model.TopicModel;
import th.co.gosoft.go10.util.BitMapUtil;
import th.co.gosoft.go10.util.GO10Application;

public class WritingTopicFragment extends Fragment {

    private final String LOG_TAG = "WritingTopicFragmentTag";
    private final String URL = "http://go10webservice.au-syd.mybluemix.net/GO10WebService/api/topic/post";
    private final int RESULT_LOAD_IMAGE = 7;
    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 89;
    private ProgressDialog progress;
    private String room_id;
    private Bundle profileBundle;
    private RichEditor mEditor;
    private EditText edtHostSubject;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.activity_writing_topic, container, false);

        edtHostSubject = (EditText) view.findViewById(R.id.txtHostSubject);

        mEditor = (RichEditor) view.findViewById(R.id.richHostContent);
        mEditor.setEditorFontSize(22);
        mEditor.setPadding(10, 10, 10, 10);

        profileBundle = ((GO10Application) getActivity().getApplication()).getBundle();
        Bundle bundle = getArguments();
        room_id = bundle.getString("room_id");
        Log.i(LOG_TAG, "room_id : " + room_id);

        view.findViewById(R.id.action_undo).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.undo();
            }
        });

        view.findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setBold();
            }
        });

        view.findViewById(R.id.action_redo).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.redo();
            }
        });

        view.findViewById(R.id.action_indent).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setIndent();
            }
        });

        view.findViewById(R.id.action_outdent).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setOutdent();
            }
        });

        view.findViewById(R.id.action_align_left).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setAlignLeft();
            }
        });

        view.findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setAlignCenter();
            }
        });

        view.findViewById(R.id.action_align_right).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setAlignRight();
            }
        });

        view.findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= 23){
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        } else {
                            Log.i(LOG_TAG, "else");
                            requestPermissions(
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                        }
                    }else{
                        Log.i(LOG_TAG, "ELSE");
                        requestPermissions(
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                }else {

                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
                }

            }
        });

        view.findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.insertLink("https://github.com/wasabeef", "wasabeef");
            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        Log.i(LOG_TAG, "onRequestPermissionsResult");
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(LOG_TAG, "if onRequestPermissionsResult");
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);

                } else {
                    Log.i(LOG_TAG, "else onRequestPermissionsResult");
                }
                return;
            }
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == getActivity().RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getActivity().getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Log.i(LOG_TAG, "picturePath : "+picturePath);
            RequestParams params = new RequestParams();

            try {

                Bitmap bitmap = BitMapUtil.resizeBitmap(picturePath, 200, 200);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                byte[] myByteArray = stream.toByteArray();
                params.put("imageFile", new ByteArrayInputStream(myByteArray));

            } catch(Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }

            AsyncHttpClient client = new AsyncHttpClient();

            client.post("http://go10webservice.au-syd.mybluemix.net/GO10WebService/UploadServlet", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.i(LOG_TAG, String.format(Locale.US, "Return Status Code: %d", statusCode));
                    String responseString = new String(responseBody);
                    Log.i(LOG_TAG, "Path : "+responseString);

                    try {
                        String imgURL =  new JSONObject(responseString).getString("imgUrl");
                        Log.i(LOG_TAG, "imgURL : "+imgURL);

                        mEditor.insertImage("http://go10webservice.au-syd.mybluemix.net"+imgURL,
                                "insertImageUrl");
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, e.getMessage(), e);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable e) {
                    Log.e(LOG_TAG, String.format(Locale.US, "Return Status Code: %d", statusCode));
                    Log.e(LOG_TAG, e.getMessage(), e);
                    Log.e(LOG_TAG, "response body : "+new String(responseBody));
                }
            });
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.writing_post_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnPost:
                Log.i(LOG_TAG, "click post");
                String hostSubjectString = edtHostSubject.getText().toString();
                String hostContentString = mEditor.getHtml();
                Log.i(LOG_TAG, "Subject : " + hostSubjectString);
                Log.i(LOG_TAG, "Content : " + hostContentString);

                if(hostSubjectString == null || isEmpty(hostSubjectString) || hostContentString == null || isEmpty(hostContentString)){
                    Log.i(LOG_TAG, "empty subject & message");
                    Toast.makeText(getActivity(), "Please enter your subject and comment message.", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
                    TopicModel topicModel = new TopicModel();
                    topicModel.setSubject(hostSubjectString);
                    topicModel.setContent(hostContentString);
                    topicModel.setAvatarName(sharedPref.getString("avatarName", "Avatar Name"));
                    topicModel.setAvatarPic(sharedPref.getString("avatarPic", "default_avatar"));
                    topicModel.setType("host");
                    topicModel.setRoomId(room_id);
                    callPostWebService(topicModel);
                }
                return true;
            default:
                break;
        }

        return false;
    }

    private boolean isEmpty(String string) {
        return string.trim().length() == 0;
    }

}
