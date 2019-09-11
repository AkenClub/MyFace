package com.hong.myface.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.util.Base64;
import android.util.Log;

import com.hong.myface.bean.FaceppBean;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class BitmapUtil {

    /**
     * 将bitmap转为base64
     *
     * @param bitmap 源bitmap资源
     * @return base64
     */
    public static String bitmap2base64(Bitmap bitmap) {

        int quality = 100;
        float limitSize = 1700f;
        String result;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

        while (outputStream.toByteArray().length / 1024f > limitSize) {

            quality -= 4;
            if (quality <= 0) {
                byte[] bytes = outputStream.toByteArray();
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                quality = 100;
                continue;
            }
            outputStream.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
        }

        Log.e("base64", outputStream.toByteArray().length / 1024 + "");

        result = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);

        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    /**
     * 获取图片旋转角度
     *
     * @param path 图片路径
     * @return 图片旋转角度
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    degree = 0;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 将bitmap旋转
     *
     * @param bm 源bitmap
     * @param degree 需要旋转的角度
     * @return 旋转后的bitmap
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap mBitmap = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try{
            mBitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError | Exception e) {
            e.printStackTrace();
        }
        if (mBitmap == null) {
            mBitmap = bm;
        }
        if (bm != mBitmap) {
            bm.recycle();
        }
        return mBitmap;
    }


    /**
     * 功能：给原图标示Face红框
     * @param faces 需要标示的Face数据
     * @param bitmap 原图
     * @return 已经标示的Bitmap
     */
    public static Bitmap markFacesInPhoto(List<FaceppBean.FacesBean> faces, @NotNull Bitmap bitmap) {
        Bitmap tempBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(tempBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8);

        for (FaceppBean.FacesBean face:
             faces) {
            FaceppBean.FacesBean.FaceRectangleBean rectangle = face.getFace_rectangle();
            int left = rectangle.getLeft();
            int top = rectangle.getTop();
            int width = rectangle.getWidth();
            int height = rectangle.getHeight();
            canvas.drawRect(left, top, left + width, top + height, paint);
            Canvas canvas1 = new Canvas();
            canvas1.drawRect(left + 3, top + 3, left + width, top + height, paint);
        }



        return tempBitmap;
    }
}
