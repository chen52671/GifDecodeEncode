package chen.zheng.gifdecodeencode.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by ChenZheng on 2015/12/18.
 */
public class BitmapUtils {
    /*图像大小不变，降低图片质量*/
    public static Bitmap compressBitmapQuality(Bitmap image, float quality) {
        if (quality >= 1l) {
            return image;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = (int) (100 * quality);
        image.compress(Bitmap.CompressFormat.JPEG, options, baos);
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inPreferredConfig = Bitmap.Config.ARGB_4444;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, o);
       /* image.recycle();*/
        return bitmap;
    }

    /*缩放图片大小*/
    public static Bitmap scaledBitmap(Bitmap bitmap, float scale) {
        if (scale >= 1l) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
       /* bitmap.recycle();*/
        return resizeBmp;
    }
}
