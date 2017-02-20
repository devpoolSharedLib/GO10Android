package th.co.gosoft.go10.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by manitkannika on 2/20/2017 AD.
 */

public class AvatarImageUtils {

    private static final String LOG_TAG = "AvatarImageUtils";
    private static int resourceId ;

    public static void setAvatarImage(Context context, final ImageView imageView, String imageName) {
        String URL = PropertyUtility.getProperty("httpUrlSite", context )+"GO10WebService/DownloadServlet";
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir("imageDir", Context.MODE_PRIVATE);
        final String fileName = imageName;
        final File imgFile = new File(directory, fileName);
        Resources resources = context.getResources();
        Log.i(LOG_TAG, "Exits file : "+imgFile.exists());
        if(imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
        } else if(isExitInDrawable(context, fileName)) {
            resourceId = resources.getIdentifier(imageName, "drawable", context.getPackageName());
            imageView.setImageResource(resourceId);
        } else {
            final ImageView finalImageView = imageView;
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    FileOutputStream fileOutputStream = null;
                    try {
                        fileOutputStream = new FileOutputStream(imgFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                        imageView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        Log.e(LOG_TAG, e.getMessage(), e);
                    }
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            String imageURL = URL + "?imageName="+fileName;
            Log.i(LOG_TAG,"myImage : "+imageURL);
            Picasso.with(context)
                    .load(imageURL)
                    .into(target);
        }
    }

    private static boolean isExitInDrawable(Context context, String fileName) {
        Resources resources = context.getResources();
        resourceId = resources.getIdentifier(fileName, "drawable",
                context.getPackageName());
        return resourceId != 0;
    }
}
