package com.coc.hrngtextfile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.READ_PHONE_STATE;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "myMain";
    TextView tvSensor, txtWork;
    String myDeviceModel = "", myDeviceSerial = "";
    private static final int REQUEST_PHONE_STATE = 0;
    public SensorManager sensorManager;
    Sensor sensor;
    private String datePattern = "ddMMyyyyHHmmss";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
    String tmFile = "";
    private int sensorType;
    public static final String strWork = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSensor = (TextView)findViewById(R.id.tvSensor);
        txtWork = (TextView)findViewById(R.id.txtWork);
        myDeviceSerial = null;
        myDeviceModel = null;

//        String str = "07102019093111_5.txt";
//        String sub = str.substring(str.indexOf('_')+1, str.indexOf('.'));
//        Log.i(TAG, "sub: " + sub);
        getPhoneInfo();

        /*
        else {
            //requestPhonePermission();
            getPhoneInfo();
        }

         */

        /*
        String sID = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.i(TAG, "sID: " + sID);

        String imei = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                imei = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getImei();
            }
        } else {
            imei = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getImei();
        }
        Log.i(TAG, "telephony:" + imei);

         */
    }

    /*
    private void getPhoneInformation(){
        Log.i(TAG, "Checking permission.");
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            requestPhonePerm();
        }
        else {
            //Permission has already been granted
            Log.i(TAG, "PHONE permission has already been granted.");
            myDeviceModel = android.os.Build.MODEL.trim();
            myDeviceSerial = android.os.Build.getSerial().trim();
            Log.i(TAG, "PHONE info: " + myDeviceModel + " ;" + myDeviceSerial);
            tvSensor.setText("Model: " + myDeviceModel + "\nSerial: " + myDeviceSerial);
            SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            List<Sensor> deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
            String[] sensorList = {null, "ACCELEROMETER", "MAGNETIC_FIELD", null, "GYROSCOPE", "LIGHT",
                    "PRESSURE", null, "PROXIMITY", "GRAVITY", "LINEAR_ACCELERATION", null, "RELATIVE_HUMIDITY",
                    "AMBIENT_TEMPERATURE"};
            tvSensor.append("\nFound Sensors");
            int c = 0;
            for (Sensor ds : deviceSensors) {
                if (ds.getType() <= 13 && sensorList[ds.getType()] != null) {
                    //Log.i(TAG, "sensorType: " + ds.getType());
                    tvSensor.append("\n" + sensorList[ds.getType()]);

                    Intent intent = new Intent(getApplicationContext(), MyService.class);
                    intent.putExtra("KEY_SENSOR_TYPE", ds.getType());
                    intent.putExtra("SENSOR_TYPE", sensorList[ds.getType()]);
                    intent.putExtra("DEVICE_MODEL", myDeviceModel);
                    intent.putExtra("DEVICE_SERIAL", myDeviceSerial);
                    startService(intent);
                }
            }

        }

    }

    private void requestPhonePerm(){
        Log.i(TAG, "PHONE permission has NOT been granted. Requesting permission.");
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)){
            // sees the explanation, try again to request the permission.
            //Log.i(TAG, "try again to request the permission");
            requestPhonePerm();
        }
        else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_PHONE_STATE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_PHONE_STATE: {
                Log.i(TAG, "Received response for PHONE permission request.");
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // permission was granted
                    Log.i(TAG, "PHONE permission has now been granted.");
                    getPhoneInformation();
                }
                else {
                    // permission denied
                    Log.i(TAG, "PHONE permission was NOT granted.");
                    requestPhonePerm();
                }
                break;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }
*/

    @SuppressLint({"MissingPermission", "RestrictedApi"})
    private void getPhoneInfo() {
        Log.i(TAG, "Checking permission.");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPhonePermission();
            //return;
        } else {
            Log.i(TAG, "PHONE permission has already been granted.");
            myDeviceModel = android.os.Build.MODEL.trim();
            myDeviceSerial = android.os.Build.getSerial().trim();
            Log.i(TAG, "PHONE info: " + myDeviceModel + " ;" + myDeviceSerial);
            tvSensor.setText("Model: " + myDeviceModel + "\nSerial: " + myDeviceSerial);
            //Reading Sensor
//            readingSensors();

            SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            List<Sensor> deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
            String[] sensorList = {null, "ACCELEROMETER", "MAGNETIC_FIELD", null, "GYROSCOPE", "LIGHT",
                    "PRESSURE", null, "PROXIMITY", "GRAVITY", "LINEAR_ACCELERATION", null, "RELATIVE_HUMIDITY",
                    "AMBIENT_TEMPERATURE"};
            tvSensor.append("\nFound Sensors");
            for (Sensor ds : deviceSensors) {
                if (ds.getType() <= 13 && sensorList[ds.getType()] != null ) {
                    //Log.i(TAG, "sensorType: " + ds.getType());
                    tvSensor.append("\n" + sensorList[ds.getType()]);

                    Intent intent = new Intent(getApplicationContext(), MyService.class);
                    intent.putExtra("KEY_SENSOR_TYPE", ds.getType());
                    intent.putExtra("SENSOR_TYPE", sensorList[ds.getType()]);
                    intent.putExtra("DEVICE_MODEL", myDeviceModel);
                    intent.putExtra("DEVICE_SERIAL", myDeviceSerial);
                    startService(intent);
//                    startForegroundService(intent);
//                    Boolean isServiceRunning = ServiceTools.isServiceRunning(MainActivity.this.getApplicationContext(), MyService.class);
//                    if(!isServiceRunning){
//                        startService(intent);
//                    }
                }
            }


            //WorkManager
            WorkManager mWorkManager = WorkManager.getInstance();

            @SuppressLint("RestrictedApi")
            Constraints cons = new Constraints();
            cons.setRequiredNetworkType(NetworkType.CONNECTED);

            Data dataInput = new Data.Builder()
                    .putString("KEY_MODEL", myDeviceModel)
                    .putString("KEY_SERIAL", myDeviceSerial)
                    .build();

            PeriodicWorkRequest workRequest = new PeriodicWorkRequest
                    .Builder(MyWorker.class, 15, TimeUnit.MINUTES)
                    .setConstraints(cons)
                    .setInputData(dataInput)
                    .addTag("WorkTag")
                    .build();

            mWorkManager.enqueueUniquePeriodicWork("WorkTag", ExistingPeriodicWorkPolicy.KEEP, workRequest);

            WorkManager.getInstance().getWorkInfoByIdLiveData(workRequest.getId()).observe(this, new Observer<WorkInfo>() {
                @Override
                public void onChanged(WorkInfo workInfo) {
                    if(workInfo != null){
                        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
                        String tm = dateFormat.format(new Date());
                        txtWork.append( tm + ":" + workInfo.getState().name() + "\n");

//                        makeNotificationChannel("CHANNEL_0", "CHANNEL_Main", NotificationManager.IMPORTANCE_DEFAULT);
//                        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "CHANNEL_0");
//                        notification.setSmallIcon(R.mipmap.ic_launcher)
//                                .setContentTitle(getString(R.string.app_name))
//                                .setContentText("WorkManager: " + workInfo.getState().name());
//                        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//                        assert notificationManager != null;
//                        notificationManager.notify(1, notification.build());

//                        Log.i(TAG, "outputData: " + workInfo.getOutputData().getString("KEY_RESULT"));
                    }
                }
            });



        }

    }

    private void requestPhonePermission() {
        Log.i(TAG, "PHONE permission has NOT been granted. Requesting permission.");
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, READ_PHONE_STATE)) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{READ_PHONE_STATE}, REQUEST_PHONE_STATE);
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{READ_PHONE_STATE}, REQUEST_PHONE_STATE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE_STATE:
                Log.i(TAG, "Received response for PHONE permission request.");
                // Check if the only required permission has been granted
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Phone permission has been granted, preview can be displayed
                    Log.i(TAG, "PHONE permission has now been granted.");
                    getPhoneInfo();
                } else {
                    Log.i(TAG, "PHONE permission was NOT granted.");
                    requestPhonePermission();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    private void makeNotificationChannel(String id, String name, int importance){
        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.setShowBadge(true);
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);
    }

//    public void readingSensors(){
//        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        List<Sensor> deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
//        String[] sensorList = {null, "ACCELEROMETER", "MAGNETIC_FIELD", null, "GYROSCOPE", "LIGHT",
//                "PRESSURE", null, "PROXIMITY", "GRAVITY", "LINEAR_ACCELERATION", null, "RELATIVE_HUMIDITY",
//                "AMBIENT_TEMPERATURE"};
//        tvSensor.append("\nFound Sensors");
//        for (Sensor ds : deviceSensors) {
//            if (ds.getType() <= 13 && sensorList[ds.getType()] != null) {
//                Log.i(TAG, "sensorType: " + ds.getType());
//                tvSensor.append("\n" + sensorList[ds.getType()]);
//                sensorType = ds.getType();
//                sensor = sensorManager.getDefaultSensor(ds.getType());
//                sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
//            }
//        }
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        sensorManager.unregisterListener(sensorListener);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
//    }
//
//    SensorEventListener sensorListener = new SensorEventListener() {
//        @Override
//        public void onSensorChanged(SensorEvent sensorEvent) {
//            Log.i(TAG, "len:" + sensorEvent.values.length + " x:" + sensorEvent.values[0]);
//            String tm = dateFormat.format(new Date());
//            if(tmFile == ""){
//                tmFile = tm;
//            }
//            else if((Long.parseLong(tm) - Long.parseLong(tmFile)) > (60*10)) { //10 minutes
//                tmFile = tm;
//                //Log.i(TAG, "Change file to: " + tm);
//            }
//
//            final String sFileName = tmFile + "_" + sensorType + ".txt";
//            Log.i(TAG, "type: " + sensorType);
//            FileOutputStream fos = null;
//            File myDir = new File(String.valueOf(getApplicationContext().getFilesDir()));
//        }
//
//        @Override
//        public void onAccuracyChanged(Sensor sensor, int i) {
//
//        }
//    };
}
