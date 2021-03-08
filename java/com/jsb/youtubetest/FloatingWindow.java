package com.jsb.youtubetest;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.Calendar;

public class FloatingWindow extends Service {
    private Handler mHandler;
    private int mX;
    private int mY;
    private int eX;
    private int eY;

    private Long clickStartTimer = 0L;


    private final Long CLICK_DELTA = 200L;




    //secondWidget Drag variables

    private int initialXX;
    private int initialYY;
    private int initialTouchXX;
    private int initialTouchYY;


    WindowManager wm,wm2;
    View floatingView, collapsedView, expandedView,secondWidget;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

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

//        ll = new LinearLayout(this);
//        ll.setBackgroundColor(Color.TRANSPARENT);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT);
//        ll.setLayoutParams(layoutParams);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

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

                if(mRunnable == null)
                {
                    mRunnable = new IntervalRunnable();
                }


                mHandler.post(mRunnable);


            }
        });

        floatingView.findViewById(R.id.Widget_Add_Rec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FloatingWindow.this, "Add Record", Toast.LENGTH_SHORT).show();
            }
        });




    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        wm.removeView(floatingView);
        wm2.removeView(secondWidget);
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
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
            wm2 = (WindowManager) getSystemService(WINDOW_SERVICE);
            wm2.addView(secondWidget,params2);

            params2.gravity = Gravity.CENTER;
            params2.x = 0;
            params2.y = 100;












            secondWidget.findViewById(R.id.secondWidgetMainLayout).setOnTouchListener(new View.OnTouchListener() {
                WindowManager.LayoutParams updatepar = params2;


                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:

                            initialXX = updatepar.x ;
                            initialYY = updatepar.y ;

                            initialTouchXX = (int) motionEvent.getRawX();
                            initialTouchYY = (int) motionEvent.getRawY();
                            return true;

                        case MotionEvent.ACTION_MOVE:

                            updatepar.x = initialXX+(int) (-initialTouchXX+motionEvent.getRawX());
                            updatepar.y = initialYY+(int) (-initialTouchYY+motionEvent.getRawY());
                            wm2.updateViewLayout(secondWidget, updatepar);


                            return true;








                            //this code is helping the widget to move around the screen with fingers










                    } return false;}});




        }
    }




}
