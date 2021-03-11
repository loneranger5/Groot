package com.jsb.youtubetest;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Intent;
import android.graphics.Path;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.jsb.youtubetest.FloatingWindow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class accessibilityService extends AccessibilityService  {


        private Handler mHandler,GHandler;
        private int mX;
        private int mY;
        private int eX;
        private int eY;


        @Override
        public void onCreate() {
            super.onCreate();
            HandlerThread handlerThread = new HandlerThread("auto-handler");
            handlerThread.start();
            mHandler = new Handler(handlerThread.getLooper());
            GHandler = new Handler(handlerThread.getLooper());







        }

        @Override
        protected void onServiceConnected() {

        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.d("Service", "SERVICE STARTED");
            if (intent != null) {
               // Bundle extras = intent.getExtras();
                String action = intent.getStringExtra("action");
                if (action.equals("Tap")) {


                    mX =intent.getIntExtra("x",0);
                    mY = intent.getIntExtra("y",0);


                    Log.d("mX","mX and mY "+mX+" "+mY);


                    //Log.d("x_value",Integer.toString(mX));


                   // Toast.makeText(getApplicationContext(), " X "+mX+ "Y "+mY, Toast.LENGTH_SHORT).show();


                    try {
                        Thread.sleep(2000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (mRunnable == null) {

                        mRunnable = new IntervalRunnable();
                    }
                    //playTap(mX,mY);
                    //mHandler.postDelayed(mRunnable, 1000);
                    mHandler.post(mRunnable);




                } else if (action.equals("Swipe")) {


                    mX =intent.getIntExtra("x",0);
                    mY = intent.getIntExtra("y",0);
                    eX = intent.getIntExtra("dX",0);
                    eY = intent.getIntExtra("dY",0);





                    if (GRunnable == null) {

                        GRunnable = new GIntervalRunnable();
                    }
                    //playTap(mX,mY);
                    //mHandler.postDelayed(mRunnable, 1000);
                    GHandler.post(GRunnable);



                }else if(action.equals("Replay"))
                {
                    Toast.makeText(this, "Replay Action", Toast.LENGTH_SHORT).show();

                    try {
                        parseConfig();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return super.onStartCommand(intent, flags, startId);
        }

        //@RequiresApi(api = Build.VERSION_CODES.N)
        @RequiresApi(api = Build.VERSION_CODES.N)
        private void playTap(int x, int y) {
            //Log.d("TAPPED","STARTED TAPpING");
            Toast.makeText(getApplicationContext(), "Tap Action " + y, Toast.LENGTH_LONG).show();
            Path swipePath = new Path();
            swipePath.moveTo((float) x, (float) y);
            swipePath.lineTo((float)x,(float) y);


            GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
            gestureBuilder.addStroke(new GestureDescription.StrokeDescription(swipePath, 0, 3));
            // gestureBuilder.addStroke(new Ges)
            //dispatchGesture(gestureBuilder.build(), null, null);
            //Log.d("hello","hello?");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                dispatchGesture(gestureBuilder.build(), new GestureResultCallback() {
                    @Override
                    public void onCompleted(GestureDescription gestureDescription) {
                        Log.d("Gesture Completed", "Gesture Completed");
                        super.onCompleted(gestureDescription);
                        //mHandler.postDelayed(mRunnable, 1);

                    }

                    @Override
                    public void onCancelled(GestureDescription gestureDescription) {
                        //Log.d("Gesture Cancelled","Gesture Cancelled");
                        super.onCancelled(gestureDescription);
                    }
                }, null);
            }
            //Log.d("hi","hi?");
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        private void playSwipe(int x, int y, int endx, int endy, int duration) {
            //Log.d("TAPPED","STARTED TAPpING");
            Toast.makeText(getApplicationContext(), "Swipe Action ", Toast.LENGTH_LONG).show();
            Path swipePath = new Path();
            swipePath.moveTo(x, y);
            swipePath.lineTo(endx, endy);

            GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
            gestureBuilder.addStroke(new GestureDescription.StrokeDescription(swipePath, 0, duration));
            // gestureBuilder.addStroke(new Ges)
            //dispatchGesture(gestureBuilder.build(), null, null);
            //Log.d("hello","hello?");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                dispatchGesture(gestureBuilder.build(), new GestureResultCallback() {
                    @Override
                    public void onCompleted(GestureDescription gestureDescription) {
                        Log.d("Gesture Completed", "Gesture Completed");
                        super.onCompleted(gestureDescription);
                        //mHandler.postDelayed(mRunnable, 1);

                    }

                    @Override
                    public void onCancelled(GestureDescription gestureDescription) {
                        //Log.d("Gesture Cancelled","Gesture Cancelled");
                        super.onCancelled(gestureDescription);
                    }
                }, null);
            }
            //Log.d("hi","hi?");
        }

        @Override
        public void onAccessibilityEvent(AccessibilityEvent event) {
            //       Boolean Touch = onTouchEvent(event);

        }


        @Override
        public void onInterrupt() {
        }


        private IntervalRunnable mRunnable;

        private class IntervalRunnable implements Runnable {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                //Log.d("clicked","click");t


                playTap(mX, mY);


            }
        }

        private GIntervalRunnable GRunnable;

        private class GIntervalRunnable implements Runnable {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                //Log.d("clicked","click");
               playSwipe(mX, mY, eX, eY, 10);
            }
        }



        //parse groot config for replaying gestures.

        public void parseConfig() throws IOException {
          //  Toast.makeText(this, "Entered parseConfig", Toast.LENGTH_SHORT).show();
            File configFile = new File(this.getFilesDir()+"/groot.config");
            if(configFile.exists())
            { //Toast.makeText(this, "Entered parseConfig File Check", Toast.LENGTH_SHORT).show();
               BufferedReader br = new  BufferedReader(new FileReader(configFile));
               String line;
               while((line = br.readLine())!=null)
               {
                   String[] split = line.split(",");
                   for(int i=0; i <split.length; i++)
                   {
                      // Toast.makeText(this, "value " + i + ": " + split[i], Toast.LENGTH_SHORT).show();

                       if(split[i].equals("Tap")){
                           mX = Integer.parseInt(split[i+1]);
                           mY = Integer.parseInt(split[i+2]);

                           try {
                               Thread.sleep(5000);

                           } catch (InterruptedException e) {
                               e.printStackTrace();
                           }
                           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                               playTap(mX,mY);
                           }
                       }else if(split[i].equals("Swipe"))
                       {
                           mX = Integer.parseInt(split[i+1]);
                           mY = Integer.parseInt(split[i+2]);
                           eX = Integer.parseInt(split[i+3]);
                           eY = Integer.parseInt(split[i+4]);
                           try {
                               Thread.sleep(2000);

                           } catch (InterruptedException e) {
                               e.printStackTrace();
                           }
                           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                               playSwipe(mX,mY,eX,eY,5);
                           }
                       }
                   }
               }
               br.close();

            }else{
                Toast.makeText(this, "No Recordings found. Please Complete Groot Process", Toast.LENGTH_SHORT).show();
            }

        }
    }