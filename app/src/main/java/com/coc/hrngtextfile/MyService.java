package com.coc.hrngtextfile;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyService extends Service implements SensorEventListener {
    private static final String TAG = "myService";
    private SensorManager sensorManager = null;
    private Sensor sensor = null;
    private String deviceModel = "Not Found";
    private String deviceSerial = "Not Found";
    // have a default sensor configured
    private int sensorType = Sensor.TYPE_ACCELEROMETER;
    private String sensorTypeName = "ACCELEROMETER";
    private String datePattern = "ddMMyyyyHHmmss";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
    String tmFile = "";
    String[] sensorList = {null, "ACCELEROMETER", "MAGNETIC_FIELD", null, "GYROSCOPE", "LIGHT",
            "PRESSURE", null, "PROXIMITY", "GRAVITY", "LINEAR_ACCELERATION", null, "RELATIVE_HUMIDITY",
            "AMBIENT_TEMPERATURE"};
    private static final int NOTIF_ID = 1;
    private static final String NOTIF_CHANNEL_ID = "channel_id";

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
                 // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent.getExtras();
        if(extras == null)
            Log.d(TAG,"Bundle null");
        else {
            //Log.d(TAG, "Bundle not null");
            sensorType = (int) extras.get("KEY_SENSOR_TYPE");
            sensorTypeName = (String) extras.get("SENSOR_TYPE");
            deviceModel = (String) extras.get("DEVICE_MODEL");
            deviceSerial = (String) extras.get("DEVICE_SERIAL");

        }
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(sensorType);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        //Log.i(TAG, "SensorType:" + sensorType + " name:" + sensorTypeName);
        //startForeground();

//        makeNotificationChannel("CHANNEL_1", "CHANNEL_STRING", NotificationManager.IMPORTANCE_DEFAULT);
//        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, "CHANNEL_1");
//        notification.setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle(getString(R.string.app_name))
//                .setContentText("Service is running background")
//                .setNumber(3);
//        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//        assert notificationManager != null;
//        notificationManager.notify(1, notification.build());

//        return START_REDELIVER_INTENT;
        return START_STICKY;
//        return super.onStartCommand(intent, flags, startId);
    }

    private void makeNotificationChannel(String id, String name, int importance){
        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.setShowBadge(true);
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);
    }

    private void startForeground(){

//        Intent[] notificationIntent = new Intent[0];
//        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0, notificationIntent,0);
//        startForeground(NOTIF_ID, new NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
//                .setOngoing(true)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle(getString(R.string.app_name))
//                .setContentText("Service is running background")
//                //.setContentIntent(pendingIntent)
//                .build()
//        );
    }

    private class SensorEventLoggerTask extends AsyncTask<SensorEvent, Void, Void> {
        @Override
        protected Void doInBackground(SensorEvent... events) {
            //write data to text files
            //Log.i(TAG, "type:" + events[0].sensor.getType());
            SensorEvent event = events[0];
            int sType = event.sensor.getType();

            final String sFileName = tmFile + "_" + sType + ".txt";
            FileOutputStream fos = null;
            File myDir = new File(String.valueOf(getApplicationContext().getFilesDir()));
            /*
            File myDir = new File(getApplicationContext().getFilesDir(), deviceSerial);
            if(!myDir.exists()){
                myDir.mkdirs();
            }
             */

            try {
                fos = new FileOutputStream(new File(myDir, sFileName), true);
                //fos = getApplicationContext().openFileOutput(myDir + File.separator + sFileName, MODE_PRIVATE);

                String dataX = Float.toString(event.values[0]);
                String dataY = "-9";
                String dataZ = "-9";
//                String typeName = sensorTypeName;
//                int type = sType;
                String model = deviceModel;
                String serial = deviceSerial;
                String tm = dateFormat.format(new Date());
                if(event.values.length > 1){
                    dataY = Float.toString(event.values[1]);
                    dataZ = Float.toString(event.values[2]);
                }
                String str = dataX + "," + dataY + "," + dataZ + ","
                        + sType + "," + sensorList[sType] + "," + model + ","
                        + serial + "," + tm + "\n";
                fos.write(str.getBytes());
                //Log.i(TAG, "str:" + str);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            } finally {
                if(fos != null){
                    try {
                        fos.close();
                        //Log.i(TAG, "Wrote to " + myDir + File.separator + sFileName);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            return null;
        }

    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        String tm = dateFormat.format(new Date());
        if(tmFile == ""){
            tmFile = tm;
        }
        else if((Long.parseLong(tm) - Long.parseLong(tmFile)) > (60*10)) { //10 minutes
            tmFile = tm;
            //Log.i(TAG, "Change file to: " + tm);
        }
        //Log.i(TAG, "sensorChange: " + tm + "  " + tmFile + "  " + (Long.parseLong(tm) - Long.parseLong(tmFile)));




//        //write data to text files
//        final String sFileName = tmFile + "_" + sensorType + ".txt";
//        FileOutputStream fos = null;
//        File myDir = new File(String.valueOf(getApplicationContext().getFilesDir()));
//            /*
//            File myDir = new File(getApplicationContext().getFilesDir(), deviceSerial);
//            if(!myDir.exists()){
//                myDir.mkdirs();
//            }
//             */
//
//        try {
//            fos = new FileOutputStream(new File(myDir, sFileName), true);
//            //fos = getApplicationContext().openFileOutput(myDir + File.separator + sFileName, MODE_PRIVATE);
////            SensorEvent event = events[0];
//            String dataX = Float.toString(sensorEvent.values[0]);
//            String dataY = "-9";
//            String dataZ = "-9";
//            String typeName = sensorTypeName;
//            int type = sensorType;
//            String model = deviceModel;
//            String serial = deviceSerial;
////            String tm = dateFormat.format(new Date());
//            tm = dateFormat.format(new Date());
//            if(sensorEvent.values.length > 1){
//                dataY = Float.toString(sensorEvent.values[1]);
//                dataZ = Float.toString(sensorEvent.values[2]);
//            }
//            String str = dataX + "," + dataY + "," + dataZ + ","
//                    + type + "," + typeName + "," + model + ","
//                    + serial + "," + tm + "\n";
//            fos.write(str.getBytes());
//            //Log.i(TAG, "str:" + str);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e){
//            e.printStackTrace();
//        } finally {
//            if(fos != null){
//                try {
//                    fos.close();
//                    Log.i(TAG, "Wrote to " + myDir + File.separator + sFileName);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }

        new SensorEventLoggerTask().execute(sensorEvent);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
