package com.xx.loader;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Queue;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private SensorManager mSensorManager = null;
    private Sensor mPressureSensor = null;
    private Sensor mAccSensor = null;

    private TextView exit;
    private TextView load_status;
    private TextView altitude;
    private TextView start_altitude;
    private TextView arrived;
    private TextView move_type;
    private TextView move_status;

    private final SensorEventListener mPressureListener = new SensorEventListener() {
        double lowest = 1000;
        double hightest = -1000;

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_PRESSURE) {
                DecimalFormat df = new DecimalFormat("0.00");
                df.getRoundingMode();
                Double pressure = Double.parseDouble(df.format(sensorEvent.values[0]));
                // 海拔高度
                double height = 44330000 * (1 - (Math.pow((pressure / 1013.25),
                        (float) 1.0 / 5255.0)));
                height = Double.parseDouble(df.format(height));
                if (height < lowest) {
                    lowest = height;
                }
                if (height > hightest) {
                    hightest = height;
                }
                if (hightest - lowest < 4) {
                    exit.setText("请先行走至站厅层...");
                    load_status.setText("待识别");
                    altitude.setText(height + "m");
                    start_altitude.setText(lowest + "m");
                    arrived.setText("未到达");

                    move_status.setText("正在移动...");
                } else {
                    exit.setText("正在计算最优疏散闸机口...");
                    load_status.setText("正在识别当前负重状态...");
                    altitude.setText(height + "m");
                    start_altitude.setText(lowest + "m");
                    arrived.setText("已到达");
                    move_status.setText("正在移动...");
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };
    private final SensorEventListener mAccListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                solveAcc(sensorEvent, 20);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    private void solveAcc(SensorEvent sensorEvent, int size) {
        DecimalFormat df = new DecimalFormat("0.00");
        df.getRoundingMode();
        float a_x = sensorEvent.values[0];   // 获取x轴的加速度
        float a_y = sensorEvent.values[1];   // 获取y轴的加速度
        float a_z = sensorEvent.values[2];   // 获取z轴的加速度
        double sum_acc = Math.sqrt(a_x * a_x + a_y * a_y + a_z * a_z);
        sum_acc = Double.parseDouble(df.format(sum_acc));

        Queue<Double> queue = new LinkedList<>();
        queue.add(sum_acc);
        if (queue.size() > size) {
            queue.poll();
        }
        double sum = 0;
        for (double d : queue) {
            sum += Math.pow(d - 9.8, 2);
        }
        sum = sum / size;
        if (sum > 0.2){
            move_type.setText("步梯");
        }else {
            move_type.setText("电梯");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取传感器服务管理器
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mPressureSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        mAccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        exit = findViewById(R.id.tv_exit);
        load_status = findViewById(R.id.tv_status);
        altitude = findViewById(R.id.tv_altitude);
        start_altitude = findViewById(R.id.tv_lowest);
        arrived = findViewById(R.id.tv_arrived);
        move_type = findViewById(R.id.tv_move_type);
        move_status = findViewById(R.id.tv_move_status);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mPressureListener, mPressureSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mAccListener, mAccSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mPressureListener, mPressureSensor);
        mSensorManager.unregisterListener(mAccListener, mAccSensor);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent it;
        if (id == R.id.m_classify) {
            it = new Intent(MainActivity.this, ClassifyActivity.class);
            startActivity(it);
            return true;
        } else if (id == R.id.m_collect) {
            it = new Intent(MainActivity.this, SelectActivity.class);
            startActivity(it);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
