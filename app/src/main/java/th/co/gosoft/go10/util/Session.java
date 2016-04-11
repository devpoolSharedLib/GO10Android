package th.co.gosoft.go10.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import th.co.gosoft.go10.model.RoomModel;
import th.co.gosoft.go10.model.TopicModel;

public class Session {

    private SharedPreferences prefs;

    public Session(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setusename(String usename) {
        prefs.edit().putString("usename", usename).commit();
    }

    public String getusename() {
        String usename = prefs.getString("usename","");
        return usename;
    }

    public void setRoom(RoomModel roomModel) {
        prefs.edit().putString("room_id", roomModel.get_id()).commit();
        prefs.edit().putString("room_name", roomModel.getName()).commit();
    }

    public RoomModel getRoom() {
        RoomModel roomModel = new RoomModel();
        roomModel.set_id(prefs.getString("room_id", ""));
        roomModel.setName(prefs.getString("room_name", ""));
        return roomModel;
    }

    public void setTopic(TopicModel topicModel) {
        prefs.edit().putString("topic_id", topicModel.get_id()).commit();
//        prefs.edit().putString("room_name", roomModel.getName()).commit();
    }

    public TopicModel getTopic() {
        TopicModel topicModel = new TopicModel();
        topicModel.set_id(prefs.getString("topic_id", ""));
//        roomModel.setName(prefs.getString("room_name", ""));
        return topicModel;
    }
}