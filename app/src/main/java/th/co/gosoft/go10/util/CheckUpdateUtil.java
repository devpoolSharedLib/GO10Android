package th.co.gosoft.go10.util;

import android.app.Activity;
import android.util.Log;

import com.rampo.updatechecker.UpdateChecker;

/**
 * Created by Plooer on 1/23/2017 AD.
 */

public class CheckUpdateUtil {
    private final String LOG_TAG = "CheckUpdate";

    public void checkUpdateVersion(Activity activity) {
        Log.i( LOG_TAG, "checkUpdateVersion" );

        UpdateChecker checker = new UpdateChecker(activity);
        checker.setSuccessfulChecksRequired(1);
        checker.showNotification();
        checker.start();
    }
}
