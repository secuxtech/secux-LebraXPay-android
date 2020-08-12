package com.secuxtech.libraxpay.Utility;

import android.content.Context;
import android.util.Log;


import com.secuxtech.libraxpay.Model.Setting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;

/**
 * Created by maochuns.sun@gmail.com on 2020/5/11
 */
public class LogHandler {

    private static String  mLogFileName = "";
    private static Context mContext = null;

    public void setContext(Context context){
        mContext = context.getApplicationContext();
    }

    public static void writeToFile(final String fileContents, String fileName) {

        try {
            //File thedir = mContext.getFilesDir(); //getDir("Logs", Context.MODE_PRIVATE);

            File logDir = new File(getLogDirPath());
            if (!logDir.exists())
                logDir.mkdirs();

            FileWriter out = new FileWriter(new File(logDir, fileName), true);
            out.write(fileContents + "\n");
            //out.append(fileContents);
            out.close();
        } catch (IOException e) {
            Log.i("MerchantAPP", "Write log to file failed!");
        }
    }

    public String readFile(String fileName) {

        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader in = null;

        try {
            in = new BufferedReader(new FileReader(new File(mContext.getFilesDir(), fileName)));
            while ((line = in.readLine()) != null) stringBuilder.append(line);

        } catch (FileNotFoundException e) {
            //Logger.logError(TAG, e);
        } catch (IOException e) {
            //Logger.logError(TAG, e);
        }

        return stringBuilder.toString();
    }

    public static void Log(String msg){
        Log.i("SecuXMerchantAPP", msg);


        if (mLogFileName.length() == 0){
            String currentDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
            String currentTime = new SimpleDateFormat("HHmmss", Locale.getDefault()).format(new Date());
            mLogFileName = currentDate + "_" + currentTime + ".txt";
        }

        //File thedir = mContext.getDir("Logs", Context.MODE_PRIVATE);

        File logDir = new File(getLogDirPath());
        if (!logDir.exists()) {
            File file = new File(logDir, mLogFileName);
            if (file.exists() && file.length() > 1024 * 1024 * 5) {
                String currentDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
                String currentTime = new SimpleDateFormat("HHmmss", Locale.getDefault()).format(new Date());
                mLogFileName = currentDate + "_" + currentTime + ".txt";
            }
        }

        /*
        File dirFiles = mContext.getFilesDir();
        for (String strFile : dirFiles.list())
        {
            // strFile is the file name
        }
        */

        if (Setting.getInstance().mLogToFile) {
            String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
            writeToFile(currentTime + "  " + msg, mLogFileName);
        }
    }

    public static void deleteLogFiles(){
        String[] logFiles = getLogFileList();
        for(String path : logFiles){
            File logFile = new File(getLogDir(), path);
            if (logFile.exists()){
                logFile.delete();
            }
        }
    }

    public static String[] getLogFileList(){
        File thedir = new File(getLogDirPath());
        if (thedir != null && thedir.list() != null) {
            return thedir.list();
        }else{
            return new String[0];
        }
    }

    public static File getLogDir(){
        return new File(getLogDirPath());
    }

    public static String getLogDirPath(){
        return mContext.getFilesDir().getAbsolutePath() + File.separator + "Logs";
    }
}

