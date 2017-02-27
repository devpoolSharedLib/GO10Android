package th.co.gosoft.go10.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;

/**
 * Created by manitkannika on 2/20/2017 AD.
 */

public class AvatarImageUtils {

    private static final String LOG_TAG = "AvatarImageUtils";
    private static int resourceId ;

    public static void setAvatarImage(Context context, ImageView imageView, String imageName) {
        String URL = PropertyUtility.getProperty("httpUrlSite", context )+"GO10WebService/DownloadServlet";
        Resources resources = context.getResources();
        Log.i(LOG_TAG, "Exits file : "+imageName.toString());
        if(isExitInDrawable(context, imageName)) {
            resourceId = resources.getIdentifier(imageName, "drawable", context.getPackageName());
            imageView.setImageResource(resourceId);
        } else {
            String imageURL = URL + "?imageName="+imageName;
            Log.i(LOG_TAG,"Loading Image : "+imageURL);
            Glide.with(context)
                    .load(imageURL)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        }
    }

    private static boolean isExitInDrawable(Context context, String fileName) {
        Resources resources = context.getResources();
        resourceId = resources.getIdentifier(fileName, "drawable",
                context.getPackageName());
        return resourceId != 0;
    }
}
