package com.jsb.youtubetest;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class FloatingWindow extends Service  {
    private Handler mHandler,gHandler;

    private Boolean gestureFlag = false;







    ArrayList<Integer> tapSeqX = new ArrayList<>();
    ArrayList<Integer> tapSeqY= new ArrayList<>();






    //secondWidget Drag variables

    private int initialXX,startXX,endXX;
    private int initialYY,startYY,endYY;
    private int initialTouchXX,startTapTouchXX;
    private int initialTouchYY,startTapTouchYY;
    public int endTapTouchYY,endTapTouchXX;


    WindowManager wm,wm2;
    View floatingView, collapsedView, expandedView,secondWidget;





    @Override
    public void onCreate() {
        super.onCreate();


        //additional widget and gesture Inject Handler
        HandlerThread handlerThread = new HandlerThread("auto-handler");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());




        //floating view
        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_widget_view,null);
        secondWidget = LayoutInflater.from(this).inflate(R.layout.second_widget,null);

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm2 = (WindowManager) getSystemService(WINDOW_SERVICE);

//        ll = new LinearLayout(this);
//        ll.setBackgroundColor(Color.TRANSPARENT);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT);
//        ll.setLayoutParams(layoutParams);
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE// | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS ,
                ,PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = 0;



        wm.addView(floatingView,params);



        expandedView = floatingView.findViewById(R.id.layoutExpanded);

        collapsedView = floatingView.findViewById(R.id.layoutCollapsed);



        floatingView.findViewById(R.id.Widget_Close_Icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FloatingWindow.this, "Stop is Clicked", Toast.LENGTH_SHORT).show();
                stopSelf();
            }
        });





        expandedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);

            }
        });

//        ImageView openapp = new ImageView(this);
//        openapp.setImageResource(R.mipmap.ic_launcher_round);
//        ViewGroup.LayoutParams butnparams = new ViewGroup.LayoutParams(
//                100,50);
//        openapp.setLayoutParams(butnparams);
//
//        ll.addView(openapp);
//        wm.addView(ll,params);

        floatingView.setOnTouchListener(new View.OnTouchListener() {
            WindowManager.LayoutParams updatepar = params;
            double x;
            double y;
            double px;
            double py;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:


                        x = updatepar.x;
                        y = updatepar.y;

                        px = motionEvent.getRawX();
                        py = motionEvent.getRawY();

                        break;


                    case MotionEvent.ACTION_MOVE:

                        updatepar.x = (int) (x+(motionEvent.getRawX()-px));
                        updatepar.y = (int) (y+(motionEvent.getRawY()-py));

                        wm.updateViewLayout(floatingView,updatepar);

                    default:
                        break;
                }

                return false;

            }
        });

        floatingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                collapsedView.setVisibility(View.GONE);
                expandedView.setVisibility(View.VISIBLE);
            }
        });



        //expanded view controls touch listeners

        floatingView.findViewById(R.id.Widget_Start_Rec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FloatingWindow.this, "Start Record", Toast.LENGTH_SHORT).show();
                if (mRunnable == null) {
                    mRunnable = new IntervalRunnable();
                }


                mHandler.post(mRunnable);
            }









        });

        floatingView.findViewById(R.id.Widget_Add_Rec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FloatingWindow.this, "Add Record", Toast.LENGTH_SHORT).show();

   //passing coordinate  variable to gesture intent fails gesture rather sending variables , sending coordinates it accepts and gesture is performed



                tapSeqX.add(endTapTouchXX);
                tapSeqY.add(endTapTouchYY);

                if(gestureFlag == false) {
                    Intent gestureIntent = new Intent(getApplicationContext(), accessibilityService.class);


                   Bundle gestureBundle = new Bundle();
                   gestureBundle.putString("action","tap");
                   gestureBundle.putInt("x",endTapTouchXX);
                   gestureBundle.putInt("y",endTapTouchYY);

                   gestureIntent.putExtras(gestureBundle);






                    gestureFlag = true;
                    //@loneranger
                    //Making sure to remove the second widget before calling gesture injection or else gesture inject will work on top of second widget which results the widget being tapped not the app or option whats behind the scenes
                    //wm2.removeView(secondWidget); -- important thing before injecting gestures.
                    wm2.removeView(secondWidget);
                   startService(gestureIntent);
                }



            }
        });






    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        wm.removeView(floatingView);
        wm2.removeView(secondWidget);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private IntervalRunnable mRunnable;

    private class IntervalRunnable implements Runnable {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {
            //Log.d("clicked","click");



            final WindowManager.LayoutParams params2 = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    552//| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    , PixelFormat.TRANSLUCENT);

            wm2.addView(secondWidget,params2);

            params2.gravity = Gravity.CENTER;
            params2.x = 0;
            params2.y = 0;



            secondWidget.findViewById(R.id.secondWidgetMainLayout).setOnTouchListener(new View.OnTouchListener() {
                WindowManager.LayoutParams updatepar = params2;

                //this code is helping the widget to move around stable the screen with fingers
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:

                            initialXX = updatepar.x ;
                            initialYY = updatepar.y ;
                            startXX = updatepar.x;
                            startYY = updatepar.y;
                            startTapTouchXX = (int) motionEvent.getRawX();
                            startTapTouchYY = (int) motionEvent.getRawY();

                            Log.d("Coordinates "," startX and startY "+startXX+" "+startYY);
                            Log.d("Coordinates "," startTouchX and startTouchY "+startTapTouchXX+" "+startTapTouchYY);

                            initialTouchXX = (int) motionEvent.getRawX();
                            initialTouchYY = (int) motionEvent.getRawY();
                            return true;

                        case MotionEvent.ACTION_MOVE:

                            updatepar.x = initialXX+(int) (-initialTouchXX+motionEvent.getRawX()); // Formula for smooth movement
                            updatepar.y = initialYY+(int) (-initialTouchYY+motionEvent.getRawY()); // Formula for smooth movement
                            wm2.updateViewLayout(secondWidget, updatepar);
                            return true;


                        case MotionEvent.ACTION_UP:

                            endXX = updatepar.x;
                            endYY = updatepar.y;

                            endTapTouchXX = (int) motionEvent.getRawX();
                            endTapTouchYY = (int) motionEvent.getRawY();



                            Log.d("Coordinates "," endX and endY "+endXX+" "+endYY);

                            Log.d("Coordinates "," endTouchX and endTouchY "+endTapTouchXX+" "+endTapTouchYY);


                            return true;



                    } return false;}});

        }





        }




    private GIntervalRunnable GmRunnable;

    private class GIntervalRunnable implements Runnable {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {



        }


    }






}





