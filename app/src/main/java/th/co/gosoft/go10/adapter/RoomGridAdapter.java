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
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater;
                inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.room_grid, null);

                holder.imgRoomIcon = (ImageView) convertView.findViewById(R.id.roomIcon);
                holder.txtRoomName = (TextView) convertView.findViewById(R.id.roomName);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            RoomModel room = getItem(position);

            if (room != null) {
                holder.imgRoomIcon.setImageResource(imageIdMap.get(room.get_id()));
                holder.txtRoomName.setText(room.getName());
            }

            return convertView;
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static class ViewHolder {
        ImageView imgRoomIcon;
        TextView txtRoomName;
    }

    private void generateImageToMap(Map<String, Integer> imageIdMap) {
        imageIdMap.put("rm01", R.drawable.general);
        imageIdMap.put("rm02", R.drawable.tell);
        imageIdMap.put("rm03", R.drawable.game);
        imageIdMap.put("rm04", R.drawable.food);
        imageIdMap.put("rm05", R.drawable.stock);
        imageIdMap.put("rm06", R.drawable.travel);
        imageIdMap.put("rm07", R.drawable.it);
        imageIdMap.put("rm08", R.drawable.sport);
        imageIdMap.put("rm09", R.drawable.newbie);
        imageIdMap.put("rm10", R.drawable.talktoadmin);
    }

}