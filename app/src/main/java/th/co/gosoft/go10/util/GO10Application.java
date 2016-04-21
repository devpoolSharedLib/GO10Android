package th.co.gosoft.go10.util;

import android.app.Application;
import android.os.Bundle;

/**
 * Created by manitkan on 21/04/16.
 */
public class GO10Application extends Application {

    private Bundle facebookBundle;

    public Bundle getFacebookBundle() {
        return facebookBundle;
    }

    public void setFacebookBundle(Bundle facebookBundle) {
        this.facebookBundle = facebookBundle;
    }
}
