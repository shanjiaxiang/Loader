package com.xx.loader;

import android.app.Application;
import android.util.Log;

import com.xx.loader.utils.FileUtils;

import java.io.File;
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        copyFiles();
    }

    // 判断是否需要copy训练模型
    private void copyFiles(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = FileUtils.PATH + "model.txt";
                File file = new File(path);
                if (!file.exists()) {
                    Log.d("classify", "文件不存在, 开始拷贝....");
                    FileUtils.copyFilesFassets(App.this, "model.txt", path);
                    Log.d("classify", "拷贝完成....");
                }
            }
        }).start();

    }
}
