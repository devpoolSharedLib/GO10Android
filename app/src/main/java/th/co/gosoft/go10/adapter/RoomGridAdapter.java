package th.co.gosoft.go10.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import th.co.gosoft.go10.R;
import th.co.gosoft.go10.model.RoomModel;

public class RoomGridAdapter extends ArrayAdapter<RoomModel> {

    private final String LOG_TAG = "RoomGridAdapter";
    private Map<String, Integer> imageIdMap = new HashMap<>();

    public RoomGridAdapter(Context context, int resource, List<RoomModel> items) {
        super(context, resource, items);
        generateImageToMap(imageIdMap);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        try{
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater;
                inflater = LayoutInflater.from(getContext());
                view = inflater.inflate(R.layout.room_grid, null);
            }

            RoomModel room = getItem(position);

            if (room != null) {
                ImageView imgRoomIcon = (ImageView) view.findViewById(R.id.roomIcon);
                TextView txtRoomName = (TextView) view.findViewById(R.id.roomName);

                if (imgRoomIcon != null) {
                    imgRoomIcon.setImageResource(imageIdMap.get(room.get_id()));
                }

                if (txtRoomName != null) {
                    txtRoomName.setText(room.getName());
                }
            }
//            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)view .getLayoutParams();
//            params.width = 130;
//            view.setLayoutParams(params);
            return view;
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void generateImageToMap(Map<String, Integer> imageIdMap) {
        imageIdMap.put("rm01", R.drawable.general);
        imageIdMap.put("rm02", R.drawable.it_knowledge);
        imageIdMap.put("rm03", R.drawable.sport);
        imageIdMap.put("rm04", R.drawable.entertainment);
        imageIdMap.put("rm05", R.drawable.training);
    }

}