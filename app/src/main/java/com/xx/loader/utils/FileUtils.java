package com.xx.loader.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class FileUtils {
    static String[] files = {"背包", "背包拉箱", "兜里", "兜里拉箱", "拎包", "拎包拉箱", "手持", "手持拉箱"};
    static File sd = Environment.getExternalStorageDirectory();
    public static String PATH = sd.getPath() + "/2sensor_datas/";

    /*读取过滤器前的记录*/
    public static float[][] toRecordByWindowBeforFilter(float[][] raw, int width) {
        int len = raw.length;
        int start = (int) (Math.random() * (len - width)) + 10;
        float[][] record = new float[width][4];
        for (int j = start; j < start + width; j++) {
            record[j - start] = raw[j];
        }
        return record;
    }

    /*读取过滤后的记录*/
    public static double[][] toRecordByWindowAfterFilter(double[][] filted, int width) {
        int len = filted[0].length;
        int start = (int) (Math.random() * (len - width));
        double[][] record = new double[4][width];
        for (int i = 0; i < 4; i++) {
            for (int j = start; j < start + width; j++) {
                record[i][j - start] = (float) filted[i][j];
            }
        }
        return record;
    }

    /*从文件读取记录*/
    public static float[][] toFloatArrayByFile(String name) {
        float[][] fArr = null;
        String path = PATH + name + ".txt";
        ArrayList<String[]> strList = new ArrayList<>();
        try {
            FileReader fr = new FileReader(path);
            BufferedReader bf = new BufferedReader(fr);
            String str;
            int i = 0;
            while ((str = bf.readLine()) != null) {
                String[] strArr = str.split("\t");
                strList.add(strArr);
                i++;
            }
            fArr = new float[i][4];
            for (int j = 0; j < i; j++) {
                float[] f = DataConvertsUtils.parseFloatArray(strList.get(j));
                fArr[j] = f;
            }
            bf.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fArr;
    }

    /*创建文件*/
    public void createFile(String path, String name) throws IOException {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File checkFile = new File(path + name);
        if (!checkFile.exists()) {
            checkFile.createNewFile();
        }

    }

    /*写入识别结果*/
    public static void writeFile(String name, String content) throws IOException {
        File checkFile = new File(PATH + name + ".txt");
        FileWriter writer = null;
        try {
            if (!checkFile.exists()) {
                checkFile.createNewFile();
            }
            writer = new FileWriter(checkFile, false);
            writer.append(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != writer)
                writer.close();
        }
    }

    /*读取识别结果*/
    public static String readFile(String name) {
        File checkFile = new File(name);
        if (!checkFile.exists()) {
            return "none";
        }
        FileReader reader = null;

        try {
            reader = new FileReader(checkFile);
            BufferedReader bf = new BufferedReader(reader);
            String line;
            if ((line = bf.readLine()) != null) {
                int c = (int) Float.parseFloat(line);
                Log.d("classify", "result float:" + Float.parseFloat(line));
                Log.d("classify", "result int:" + c);
                return files[c];
            } else {
                return "none";
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "none";
    }

    /*拷贝assets文件到指定目录下*/
    public static void copyFilesFassets(Context context, String name, String newPath) {
        try {
            InputStream is = context.getClass().getClassLoader().getResourceAsStream("assets/" + name);
            FileOutputStream fos = new FileOutputStream(new File(newPath));
            byte[] buffer = new byte[10240];
            int count = 0;
            while ((count = is.read(buffer))!= -1) {
                fos.write(buffer, 0, count);
            }
            fos.flush();
            fos.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*清除文件信息*/
    public static void clearInfoForFile(String fileName) {
        File file = new File(fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
