package com.secuxtech.libraxpay.Utility;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;



/**
 * Created by maochuns.sun@gmail.com on 2020/4/15
 */
public class SecuXUtility {

    static public String TAG = "SecuXUtility";

    public static void logByteArrayHexValue(byte[] data){
        if (data!=null){
            String strMsg = "";
            for (byte b: data){
                strMsg += String.format("0x%x ", b);
            }
            Log.i(TAG,  strMsg);
        }else{
            Log.i(TAG, "Null data");
        }
    }

    public static String dataToHexString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        String result = stringBuilder.toString();
        return result;
    }

    public static byte[] hexStringToData(String s) {
        int len = s.length();
        byte[] data = new byte[len/2];

        for(int i = 0; i < len; i+=2){
            data[i/2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }

        return data;
    }

    static public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public static String SHA1(String text) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] textBytes = text.getBytes();
        md.update(textBytes, 0, textBytes.length);
        byte[] sha1hash = md.digest();
        return dataToHexString(sha1hash);
    }

    public static File saveFWToFile(Context context, byte[] fwData, String devSN) throws IOException {
        LogHandler.Log("saveFWToFile " + devSN + ".zip");
        File thedir = context.getDir("FW", Context.MODE_PRIVATE);
        File file = new File(thedir, devSN + ".zip");
        if (file.exists()){
            file.delete();
        }

        FileOutputStream out = new FileOutputStream(file);
        out.write(fwData);
        out.close();

        return file;
    }

    public static void deleteFWFile(Context context, String devSN) {
        LogHandler.Log("deleteFWFile " + devSN + ".zip");
        File thedir = context.getDir("FW", Context.MODE_PRIVATE);
        File file = new File(thedir, devSN + ".zip");
        if (file.exists()){
            file.delete();
        }
    }

    public static String utcTimeToLocalTime(String utcTime){
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = df.parse(utcTime);
            df.setTimeZone(TimeZone.getDefault());
            String formattedDate = df.format(date);
            return formattedDate;
        }catch(Exception e){
            e.printStackTrace();
        }

        return "";
    }


    /**
     * Covert dp to px
     * @param dp
     * @param context
     * @return pixel
     */
    public static float convertDpToPixel(float dp, Context context){
        float px = dp * getDensity(context);
        return px;
    }

    /**
     * Covert px to dp
     * @param px
     * @param context
     * @return dp
     */
    public static float convertPixelToDp(float px, Context context){
        float dp = px / getDensity(context);
        return dp;
    }

    /**
     * 取得螢幕密度
     * 120dpi = 0.75
     * 160dpi = 1 (default)
     * 240dpi = 1.5
     * @param context
     * @return
     */
    public static float getDensity(Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.density;
    }
}
