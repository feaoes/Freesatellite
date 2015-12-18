package com.feaoes.freesatellite;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by ff on 2015/12/18.
 */
public class MBitmapUtils {

    public static Bitmap decodeSampledBitmapFromResource(Resources res,
                                                         int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        //先将inJustDecodeBounds属性设置为true,解码避免内存分配
        options.inJustDecodeBounds = true;
        // 将图片传入选择器中
        BitmapFactory.decodeResource(res, resId, options);
        // 对图片进行指定比例的压缩
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        //待图片处理完成后再进行内存的分配，避免内存泄露的发生
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    // 计算图片的压缩比例
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择长宽高较小的比例，成为压缩比例
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
}
