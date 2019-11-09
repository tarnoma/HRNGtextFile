package com.coc.hrngtextfile;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MyWorker extends Worker {
    private static final String TAG = "myWorker";
    private static final String BUCKETstr = "hrngTextFile";
    private StorageReference mStorageRef;
    private long fileSize = 0;
    public static String deviceModelWorker = "Model";
    public static String deviceSerialWorker = "Serial";

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork called");
        /*
        String deviceModel = getInputData().getString(deviceModelWorker,null);
        String deviceSerial = getInputData().getString(deviceSerialWorker,null);
        Log.i(TAG, "m:" + deviceModel + " s:" + deviceSerial);
        if (deviceSerial != null && deviceModel != null){

        }
        else {
            Log.i(TAG, "Parameter not pass.");
        }
        String strPath = getApplicationContext().getFilesDir() + File.separator + deviceSerial;
         */
        try {
            deviceModelWorker = getInputData().getString("KEY_MODEL");
            deviceSerialWorker = getInputData().getString("KEY_SERIAL");
//            final String[] strFiles = {""};
            String strPath = String.valueOf(getApplicationContext().getFilesDir());
            //Log.i(TAG, "strPath:" + strPath);
            File myDir = new File(strPath);
            File[] files = myDir.listFiles();
            mStorageRef = FirebaseStorage.getInstance().getReference();
            String key = UUID.randomUUID().toString();
            //Log.i(TAG, "myDir:" + myDir + "  fileLength:" + files.length);

            for (int i = 0; i < files.length; i++) {
                if(files[i].isFile()){
                    final String sFileName = files[i].getName();
                    String subFile = sFileName.substring(sFileName.indexOf('_')+1, sFileName.indexOf('.'));
                    //Log.i(TAG, "0: " + sFileName);
                    final File fileObj = new File(myDir + "/" + sFileName);
                    Uri fileUpload = Uri.fromFile(fileObj);
                    StorageReference fileRef = mStorageRef.child(deviceSerialWorker).child(subFile).child(sFileName);
                    fileRef.putFile(fileUpload)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Get a URL to the uploaded content
                                    //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    Log.i(TAG, "File: " + sFileName + " upload successful");
                                    //remove file xxxxxxxxxxx
                                    boolean deleted = fileObj.delete();
                                    if (deleted){
                                        Log.i(TAG, "remove file: " + sFileName + " successful");
//                                        strFiles[0] += (sFileName + "\n");
//                                        Log.i(TAG, "strFiles Listener: " + strFiles[0]);
                                    }
                                    else Log.i(TAG, "can not remove file: " + sFileName);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                    Log.i(TAG, "File: " + sFileName + " upload fail.");
                                }
                            });
                }
                else {
                    Log.i(TAG, files[i].getName() + "is directory.");
                }

            }


        /*
        for (int i = 0; i < files.length; i++) {
            Log.i(TAG, "f: " + files[i].getName() + " a:" + files[i].getAbsolutePath());
            final String sFileName = files[i].getName();
            Log.i(TAG, "Length:" + files.length + " FileName:" + sFileName);
            final File fileObj = new File(myDir + "/" + sFileName);
            Uri fileUpload = Uri.fromFile(fileObj);
            StorageReference fileRef = mStorageRef.child(BUCKETstr + "/" + sFileName);
            fileRef.putFile(fileUpload)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            Log.i(TAG, "File: " + sFileName + " upload successful");
                            //remove file xxxxxxxxxxx
                            boolean deleted = fileObj.delete();
                            if (deleted) Log.i(TAG, "remove file: " + sFileName + " successful");
                            else Log.i(TAG, "can not remove file: " + sFileName);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Log.i(TAG, "File: " + sFileName + " upload fail.");
                        }
                    });
        }
        */

            Boolean isServiceRunning = ServiceTools.isServiceRunning(getApplicationContext(), MyService.class);
            if(!isServiceRunning){
                SensorManager sensorManager = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
                List<Sensor> deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
                String[] sensorList = {null, "ACCELEROMETER", "MAGNETIC_FIELD", null, "GYROSCOPE", "LIGHT",
                        "PRESSURE", null, "PROXIMITY", "GRAVITY", "LINEAR_ACCELERATION", null, "RELATIVE_HUMIDITY",
                        "AMBIENT_TEMPERATURE"};

                for (Sensor ds : deviceSensors) {
                    if (ds.getType() <= 13 && sensorList[ds.getType()] != null) {
                        //tvSensor.append("\n" + sensorList[ds.getType()]);
                        Intent intent = new Intent(getApplicationContext(), MyService.class);
                        intent.putExtra("KEY_SENSOR_TYPE", ds.getType());
                        intent.putExtra("SENSOR_TYPE", sensorList[ds.getType()]);
                        intent.putExtra("DEVICE_MODEL", deviceModelWorker);
                        intent.putExtra("DEVICE_SERIAL", deviceSerialWorker);
                        ContextCompat.startForegroundService(getApplicationContext(),intent);
//                        getApplicationContext().startService(intent);

                    }
                }
            }
            else{
                Log.i(TAG, "Service is running.");
            }
            sendNotif();
            return Result.success();
                    //WorkerResult.SUCCESS;
        }
        catch (Exception e){
            e.printStackTrace();
            Log.i(TAG, "Work fail. Retry work again.");
            //cal work again
            startNewRequest();
            return Result.failure();
                    //WorkerResult.FAILURE;
        }

    }

    public void sendNotif(){
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("default", "DefaultChannel", NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);
        NotificationCompat.Builder notif = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setContentTitle("HRNGtextFile")
                .setContentText("WorkManager is running")
                .setSmallIcon(R.mipmap.ic_launcher);
        notificationManager.notify(1, notif.build());
    }

    public long fileSizeInKB (String fn){
        File file = new File(fn);
        long fileSize = file.length();
        return fileSize/1024;
    }

    @SuppressLint("RestrictedApi")
    private void startNewRequest(){
        //WorkManager
        WorkManager mWorkManager = WorkManager.getInstance();

        @SuppressLint("RestrictedApi")
        Constraints cons = new Constraints();
        cons.setRequiredNetworkType(NetworkType.CONNECTED);

        Data dataInput = new Data.Builder()
                .putString("KEY_MODEL", deviceModelWorker)
                .putString("KEY_SERIAL", deviceSerialWorker)
                .build();

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest
                .Builder(MyWorker.class, 15, TimeUnit.MINUTES)
                .setConstraints(cons)
                .setInputData(dataInput)
                .addTag("WorkTag")
                .build();

        mWorkManager.enqueueUniquePeriodicWork("WorkTag", ExistingPeriodicWorkPolicy.KEEP, workRequest);

        WorkManager.getInstance().getWorkInfoByIdLiveData(workRequest.getId()).observe((LifecycleOwner) this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                if(workInfo != null){
                    SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
                    String tm = dateFormat.format(new Date());
                    //txtWork.append( tm + ":" + workInfo.getState().name() + "\n");

                }
            }
        });
    }
}
