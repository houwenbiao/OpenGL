package com.jackhou.opengl.utils;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

/**
 * Author: JackHou
 * Date: 2020/5/8.
 */
public class AssetsUtils {
    public static String read(Context context, String fileName) {
        String result = null;
        try {
            InputStream is = context.getResources().getAssets().open(fileName);
            int length = is.available();
            byte[] buffer = new byte[length];
            is.read(buffer);
            result = new String(buffer, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
