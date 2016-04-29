package th.co.gosoft.go10.util;

import android.app.Fragment;

import th.co.gosoft.go10.fragment.BoardContentFragment;
import th.co.gosoft.go10.fragment.SelectRoomFragment;

public class FragmentFactory {

    public FragmentFactory() {
    }

    public Fragment getFactory(String key){
        if(key.equals("selectRoom")){
            return new SelectRoomFragment();
        }
        if(key.equals("boardContent")){
            return new BoardContentFragment();
        }
        return null;
    }
}
