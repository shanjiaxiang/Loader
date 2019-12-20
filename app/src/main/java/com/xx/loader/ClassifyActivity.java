package com.xx.loader;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.xx.loader.svm.svm_predict;
import com.xx.loader.svm.svm_train;
import com.xx.loader.utils.CalUtils;
import com.xx.loader.utils.FileUtils;
import com.xx.loader.utils.GenVectors;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;

public class ClassifyActivity extends AppCompatActivity {

    private float[][] rawDatas = new float[70][4];
    private int count = 0;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private TextView classify_result;
    private TextView x_acc;
    private TextView y_acc;
    private TextView z_acc;
    private TextView sum_acc;
    private Button start_classify;
    private Button start_train;

    long curTime;
    float a_x;   // 获取x轴的加速度
    float a_y;   // 获取y轴的加速度
    float a_z;   // 获取z轴的加速度

    private boolean classfying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classify);
        getSupportActionBar().setTitle(R.string.classify);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        classify_result = findViewById(R.id.classify_result);
        x_acc = findViewById(R.id.x_acc_text);
        y_acc = findViewById(R.id.y_acc_text);
        z_acc = findViewById(R.id.z_acc_text);
        sum_acc = findViewById(R.id.sum_acc);
        start_classify = findViewById(R.id.start_classify);
        start_train = findViewById(R.id.start_train);
        start_classify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start_classify.setText("正在识别");
                Toast.makeText(ClassifyActivity.this, "识别开始", Toast.LENGTH_SHORT).show();
                start_classify.setClickable(false);
                start_classify.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        classfying = !classfying;
                    }
                },3000);
            }
        });

        start_train.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start_train.setText("正在训练");
                Toast.makeText(ClassifyActivity.this, "训练开始", Toast.LENGTH_SHORT).show();
                start_train.setClickable(false);
                training();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(sensorEventListener, mSensor, 20000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListener);
    }

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            a_x = sensorEvent.values[0];   // 获取x轴的加速度
            a_y = sensorEvent.values[1];   // 获取y轴的加速度
            a_z = sensorEvent.values[2];   // 获取z轴的加速度
            x_acc.setText("x轴加速度:" + a_x);
            y_acc.setText("y轴加速度:" + a_y);
            z_acc.setText("z轴加速度:" + a_z);
            float acc = (float) Math.sqrt(a_x * a_x + a_y * a_y + a_z * a_z);
            sum_acc.setText("合加速度:" + acc);

            if (classfying) {
                if (count < 70) {
                    float[] data = new float[4];
                    data[0] = a_x;
                    data[1] = a_y;
                    data[2] = a_z;
                    data[3] = acc;
                    rawDatas[0] = data;
                    count++;
                } else {
                    count = 0;
                    classfying  = false;
                    classfy();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

    /**
     * 启动一个新的线程识别采集到的数据
     */
    private void classfy() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 低通滤波
                double[][] filted = CalUtils.getLowPass(rawDatas);
                // 获取特征向量
                float[] vector = CalUtils.getVector(filted);
                // 组装能够识别向量字符串
                String str = CalUtils.getVectorString(vector, 1);
                Log.d("classify", "vector:"+str);
                try {
                    // 特征向量写入到test文件
                    FileUtils.writeFile("test", str);
                    String[] parg = {FileUtils.PATH + "test.txt", FileUtils.PATH + "model.txt", FileUtils.PATH + "out.txt"};
                    // 清空输出文件内容
                    FileUtils.clearInfoForFile(parg[2]);
                    // 预测句柄
                    svm_predict p = new svm_predict();
                    // 预测执行
                    svm_predict.main(parg);
                    // 读取预测结果
                    final String result = FileUtils.readFile(parg[2]);
                    Log.d("classify", "result:"+result);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            classify_result.setText("识别结果为：" + result);
                            start_classify.setText("开始识别");
                            start_classify.setClickable(true);
                            Toast.makeText(ClassifyActivity.this, "识别结束", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
                rawDatas = new float[70][4];

            }
        }).start();
    }


    private void training(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                GenVectors.getTrainVectors(FileUtils.files, 70, 140, "train");
//                GenVectors.getTrainVectors(FileUtils.files, 70, 30, "test");
                String[] arg = {FileUtils.PATH + "train.txt", FileUtils.PATH + "model.txt"};
//                String[] parg = {FileUtils.PATH + "test.txt", FileUtils.PATH + "model.txt", FileUtils.PATH + "out.txt"};
                Log.d("classify", "开始训练");
                long start = System.currentTimeMillis();
                svm_train t = new svm_train();
                long end = System.currentTimeMillis();
                Log.d("classify", "训练结束，用时"+(end-start)/1000+"s");
//                svm_predict p = new svm_predict();
                try {
                    svm_train.main(arg);
//                    p.main(parg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("classify", "训练结束");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        start_train.setText("训练结束");
                        start_train.setClickable(true);
                        Toast.makeText(ClassifyActivity.this, "训练结束", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();

    }
}
