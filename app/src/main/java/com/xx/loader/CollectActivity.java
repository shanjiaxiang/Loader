package com.xx.loader;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.xx.loader.utils.FilesUtil;
import com.xx.loader.utils.PermisionUtils;

public class CollectActivity extends AppCompatActivity {
    private String state;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private TextView x_acc;
    private TextView y_acc;
    private TextView z_acc;
    private TextView sum_acc;
    private Button start_collect;

    long curTime;
    float a_x;   // 获取x轴的加速度
    float a_y;   // 获取y轴的加速度
    float a_z;   // 获取z轴的加速度

    private boolean writeToFile = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        getSupportActionBar().setTitle(R.string.collect);
        PermisionUtils.verifyStoragePermissions(this);
        state = getIntent().getStringExtra("type");
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        x_acc = findViewById(R.id.x_acc_text);
        y_acc = findViewById(R.id.y_acc_text);
        z_acc = findViewById(R.id.z_acc_text);
        sum_acc = findViewById(R.id.sum_acc);
        start_collect = findViewById(R.id.start_collect);
        start_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                curTime = System.currentTimeMillis();
                if (!writeToFile){
                    start_collect.setText("结束采集");
                    Toast.makeText(CollectActivity.this, "start", Toast.LENGTH_SHORT).show();
                }else {
                    start_collect.setText("开始采集");
                    Toast.makeText(CollectActivity.this, "start", Toast.LENGTH_SHORT).show();
                }
                writeToFile = !writeToFile;
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
            String sum_acc_temp = Math.sqrt(a_x * a_x + a_y * a_y + a_z * a_z) + "";
            sum_acc.setText("合加速度:" + sum_acc_temp);

            // 频率过高，应考虑缓存机制，现未考虑
            if (writeToFile){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String write2file;
                        write2file = a_x + "\t" + a_y + "\t" + a_z + "\t" +
                                Math.sqrt(a_x * a_x + a_y * a_y + a_z * a_z) + "\n";
                        FilesUtil writer = new FilesUtil();
                        writer.initData(write2file, curTime, state);
                    }
                }).start();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };
}
