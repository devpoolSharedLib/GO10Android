package th.co.gosoft.go10.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;
import java.util.Map;

import th.co.gosoft.go10.R;
import th.co.gosoft.go10.adapter.PollAdapter;
import th.co.gosoft.go10.util.OnDataPass;

/**
 * Created by ASUS on 30/3/2560.
 */

public class PollFragment  extends Fragment implements OnDataPass {

    private final String LOG_TAG = "PollFragment";
    private List<Map> pollModel;
    private ListView pollListView;

    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        pollModel = (List<Map>) bundle.get("pollModel");
        Log.i(LOG_TAG, "onCreate : PollFragment");
        Log.i(LOG_TAG, "pollModel : " + pollModel);
        Log.i(LOG_TAG, "questionMap : " + pollModel);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_poll, container ,false);
        pollListView = (ListView) view.findViewById(R.id.pollList);
        pollListView.post(new Runnable() {
            public void run() {
                pollListView.setAdapter(new PollAdapter(getActivity(), pollModel));
            }
        });
        Log.i(LOG_TAG,"OnCreateView ");

        return view;
    }

    @Override
    public void onStart() {
            super.onStart();
            Log.i(LOG_TAG," onStart ");

//        test.setText((String) pollModel.get(0).get("questionTitle"));
//        test1.setText((String) pollModel.get(0).get("date"));
        Log.i(LOG_TAG,"PollListView : "+pollModel);
        //generateListView();

//        createPoll();
    }

    private void generateListView(){
//        pollListView.setAdapter(null);

       // PollAdapter pollAdapter = new PollAdapter(getActivity(), pollModel);
        //PollAdapter2 pollAdapter = new PollAdapter2(getActivity(), pollModel);
        //pollListView.setAdapter(pollAdapter);


//        Log.i(LOG_TAG,"GenerateListView : "+pollAdapter);
    }

    @Override
    public void onDataPass(String data) {
        Log.i(LOG_TAG,"onDATAPASS    ");
    }}
