package th.co.gosoft.go10.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by manitkan on 23/05/16.
 */
public class BitmapUtil {

    public static final int RESOLUTION_WIDTH = 500;
    public static final int RESOLUTION_HEIGHT = 300;

    public static int height;
    public static int width;

    public static Bitmap resizeBitmap(String picturePath) {
        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, sizeOptions);

        int inSampleSize = calculateInSampleSize(sizeOptions, RESOLUTION_WIDTH, RESOLUTION_HEIGHT);

        sizeOptions.inJustDecodeBounds = false;
        sizeOptions.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(picturePath, sizeOptions);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        height = options.outHeight;
        width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }
}
