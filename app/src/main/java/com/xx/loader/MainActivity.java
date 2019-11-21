package com.xx.loader;

import android.annotation.SuppressLint;
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

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private TextView x_acc;
    private TextView y_acc;
    private TextView z_acc;
    private TextView sum_acc;
    private Button start_collect;
    private Button stop_collect;
    private Button change_collect_pattern;
//    Ture 为三维 FALSE为合加速度
    public static Boolean pattern = Boolean.TRUE;
    Boolean is_writing = Boolean.FALSE;
    long curTime;


    String filePath = "/sdcard/";
    String fileName = "sensor_data.txt";
    float a_x ;   // 获取x轴的加速度
    float a_y ;   // 获取y轴的加速度
    float a_z ;   // 获取z轴的加速度
//    private final

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PermisionUtils.verifyStoragePermissions(this);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = (Sensor) mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        x_acc = (TextView)findViewById(R.id.x_acc_text);
        y_acc = (TextView)findViewById(R.id.y_acc_text);
        z_acc = (TextView)findViewById(R.id.z_acc_text);
        sum_acc = (TextView)findViewById(R.id.sum_acc);
        start_collect = (Button)findViewById(R.id.start_collect);
        stop_collect = (Button)findViewById(R.id.stop_collect);
        change_collect_pattern = (Button)findViewById(R.id.pattern_change);


        start_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!is_writing){
                    is_writing = !is_writing;
                }
                curTime = System.currentTimeMillis();
                Toast.makeText(MainActivity.this, "start", Toast.LENGTH_SHORT).show();
            }
        });
        stop_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (is_writing){
                    is_writing = !is_writing;
                }
                Toast.makeText(MainActivity.this, "stop", Toast.LENGTH_SHORT).show();
            }
        });
        change_collect_pattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pattern = !pattern;
                Toast.makeText(MainActivity.this, "pattern changed", Toast.LENGTH_SHORT).show();
                if (pattern){
                    change_collect_pattern.setText("三维加速度");
                }else {
                    change_collect_pattern.setText("合加速度");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(sensorEventListener, mSensor, 20000);
//                registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListener);
    }

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @SuppressLint({"SetTextI18n", "ShowToast"})
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            a_x = sensorEvent.values[0];   // 获取x轴的加速度
            a_y = sensorEvent.values[1];   // 获取y轴的加速度
            a_z = sensorEvent.values[2];   // 获取z轴的加速度
            x_acc.setText("x轴加速度:" + a_x);
            y_acc.setText("y轴加速度:" + a_y);
            z_acc.setText("z轴加速度:" + a_z);
            String sum_acc_temp = Math.sqrt(a_x*a_x + a_y*a_y + a_z*a_z ) + "";
            sum_acc.setText("合加速度:" + sum_acc_temp);
            String write2file;
            if (is_writing){
                if (!pattern){
                    write2file = Math.sqrt(a_x*a_x + a_y*a_y + a_z*a_z ) + "\n";
                }else {
                    write2file = a_x + "\t" + a_y + "\t" + a_z + "\t" + "\n";
                }
                FilesUtil writer = new FilesUtil();
                writer.initData(write2file, curTime);
            }
//            Toast.makeText(MainActivity.this, "onSensorChanged", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
            Toast.makeText(MainActivity.this, "onAccuracyChanged", Toast.LENGTH_SHORT).show();
        }
    };

}
