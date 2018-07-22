package com.vic.imageviewerapp;

import android.content.Context;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import java.util.ArrayList;

import io.flutter.app.FlutterActivity;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class MainActivity extends FlutterActivity implements SensorEventListener {

  MethodChannel flutterChannel;
  MethodChannel androidChannel ;
  SensorManager mSensorManager;
  Sensor mProximity;
  ArrayList<String> imagePath ;
  int index;
  int start_flag;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    GeneratedPluginRegistrant.registerWith(this);

    start_flag = 0 ;
    flutterChannel = new MethodChannel(getFlutterView(), "FLUTTER_CHANNEL");
    androidChannel = new MethodChannel(getFlutterView(),"ANDROID_CHANNEL");

    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

    imagePath = new ArrayList<>();

      androidChannel.setMethodCallHandler(new MethodChannel.MethodCallHandler() {
        @Override
        public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
          if(methodCall.method.equals("loadImage")){

            String[] projection = {MediaStore.Images.Media.DATA};

            Cursor cursor = getApplicationContext().getContentResolver().
                    query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            projection, null, null, null);

            cursor.moveToFirst();

            while(!cursor.isAfterLast()){
              int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
              imagePath.add(cursor.getString(columnIndex));
              cursor.moveToNext();
            }

            cursor.close();

            index = 0 ;

            flutterChannel.invokeMethod("nextImage",imagePath.get(index));
            
            start_flag = 1;
          }
        }
      });
  }

  public final void onAccuracyChanged(Sensor sensor, int accuracy) {
    //DO NOTHING
  }

  @Override
  public final void onSensorChanged(SensorEvent event) {
    float distance =  event.values[0];
    if(distance < mProximity.getMaximumRange() && start_flag==1){
      index = (index + 1) % imagePath.size();
      flutterChannel.invokeMethod("nextImage",imagePath.get(index));
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
  }

  @Override
  protected void onPause() {
    super.onPause();
    mSensorManager.unregisterListener(this);
  }
}

