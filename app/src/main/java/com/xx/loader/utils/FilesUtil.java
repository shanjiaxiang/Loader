package com.xx.loader.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.RandomAccessFile;

public class FilesUtil {

    public void initData(String content, long curTime, String state) {
        File sd = Environment.getExternalStorageDirectory();
        String mPath = sd.getPath() + "/2sensor_datas/";
        Log.d("filePath", mPath);

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String mFile;
            mFile = state + "_" + curTime + ".txt";
            writeTxtToFile(content, mPath, mFile);
        } else {
            Log.e("error", "file write error");
        }

    }

    // 将字符串写入到文本文件中
    public void writeTxtToFile(String strContent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);
        String strFilePath = filePath + fileName;
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("error", "Error on write File:" + e);
        }
    }

    // 生成文件
    public File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }



}
