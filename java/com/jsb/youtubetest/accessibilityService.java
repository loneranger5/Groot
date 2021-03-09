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

public class accessibilityService extends AccessibilityService {


        private Handler mHandler;
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






        }

        @Override
        protected void onServiceConnected() {

        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.d("Service", "SERVICE STARTED");
            if (intent != null) {Bundle extras = intent.getExtras();
                String action = extras.getString("action");
                if (action.equals("tap")) {


                    mX =extras.getInt("x");
                    mY = extras.getInt("y");


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

                } else if (action.equals("record")) {
                    if (GRunnable == null) {

                        GRunnable = new GIntervalRunnable();
                    }
                    //playTap(mX,mY);
                    //mHandler.postDelayed(mRunnable, 1000);
                    mHandler.post(GRunnable);

                } else if (action.equals("stop")) {
                    mHandler.removeCallbacksAndMessages(null);
                }
            }
            return super.onStartCommand(intent, flags, startId);
        }

        //@RequiresApi(api = Build.VERSION_CODES.N)
        @RequiresApi(api = Build.VERSION_CODES.N)
        private void playTap(int x, int y) {
            //Log.d("TAPPED","STARTED TAPpING");
            Toast.makeText(getApplicationContext(), "playTap x = " + x + " " + y, Toast.LENGTH_LONG).show();
            Path swipePath = new Path();
            swipePath.moveTo((float) x, (float) y);
            swipePath.lineTo((float)x,(float) y);


            GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
            gestureBuilder.addStroke(new GestureDescription.StrokeDescription(swipePath, 0, 1));
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
            Toast.makeText(getApplicationContext(), "playswipe x = " + x + " " + y + " " + endx + " " + endy, Toast.LENGTH_LONG).show();
            Path swipePath = new Path();
            swipePath.moveTo(x, y);
            swipePath.lineTo(endx, endy);

            GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
            gestureBuilder.addStroke(new GestureDescription.StrokeDescription(swipePath, 0, 1));
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
               // playSwipe(mX, mY, eX, eY, 10);
            }
        }
    }