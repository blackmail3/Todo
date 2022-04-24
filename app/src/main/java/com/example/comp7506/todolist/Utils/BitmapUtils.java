package com.example.comp7506.todolist.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;

public class BitmapUtils {
    /**
     *  Compress images to prevent memory overflow
     *  @param context
     *  @param resId
     *  @return
     */
    public static Bitmap readBitMap(Context context, int resId){
        BitmapFactory.Options opt = new  BitmapFactory.Options();
        opt.inPreferredConfig =  Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;

        InputStream is =  context.getResources().openRawResource(resId);
        return  BitmapFactory.decodeStream(is, null, opt);

    }

}
