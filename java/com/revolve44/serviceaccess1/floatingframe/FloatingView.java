package com.revolve44.serviceaccess1.floatingframe;


import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.revolve44.serviceaccess1.MainActivity;
import com.revolve44.serviceaccess1.R;
import com.revolve44.serviceaccess1.floatingframe.AutoService;


public class FloatingView extends Service implements View.OnClickListener {
    private WindowManager mWindowManager,mWindowManager2;
    private View myFloatingView,myFloatingView2;

    int layout_parms,layout_parms2;
    WindowManager.LayoutParams params2;
    private int initialXX;
    private int initialYY;
    private float initialTouchXX;
    private float initialTouchYY;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();


        //getting the widget layout from xml using layout inflater
        myFloatingView = LayoutInflater.from(this).inflate(R.layout.floating_view, null);
        myFloatingView2=LayoutInflater.from(this).inflate(R.layout.floating_view2,null);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)

        {
            layout_parms = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            layout_parms2 = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }

        else {

            layout_parms = WindowManager.LayoutParams.TYPE_PHONE;
            layout_parms2 = WindowManager.LayoutParams.TYPE_PHONE;
        }

        //setting the layout parameters
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layout_parms,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);


        //getting windows services and adding the floating view to it
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(myFloatingView, params);



        //adding an touchlistener to make drag movement of the floating widget
        myFloatingView.findViewById(R.id.thisIsAnID).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("TOUCH","THIS IS TOUCHED");
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;

                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_UP:

                        return true;

                    case MotionEvent.ACTION_MOVE:
                        //this code is helping the widget to move around the screen with fingers
                        params.x = initialX + 60 + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        mWindowManager.updateViewLayout(myFloatingView, params);
                        return true;
                }
                return false;
            }
        });

        Button startButton = (Button) myFloatingView.findViewById(R.id.start);
        startButton.setOnClickListener(this);
        Button stopButton = (Button) myFloatingView.findViewById(R.id.stop);
        stopButton.setOnClickListener(this);
        Button createButton = (Button) myFloatingView.findViewById(R.id.create);
        createButton.setOnClickListener(this);


        //secondWidget




    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (myFloatingView != null) mWindowManager.removeView(myFloatingView);
    }


    @Override
    public void onClick(View v) {
        //Log.d("onClick","THIS IS CLICKED");
        Intent intent = new Intent(getApplicationContext(), AutoService.class);
        switch (v.getId()) {
            case R.id.start:
                //Log.d("START","THIS IS STARTED");
                int[] location = new int[2];
                myFloatingView.getLocationOnScreen(location);

                intent.putExtra("action", "swipe");
                intent.putExtra("x", 100);
                intent.putExtra("y", 600);
                intent.putExtra("eX", 700);
                intent.putExtra("eY", 900);
                getApplication().startService(intent);

                break;
            case R.id.stop:
                intent.putExtra("action", "stop");
                mWindowManager.removeView(myFloatingView);
                Intent appMain = new Intent(getApplicationContext(), MainActivity.class);

                //getApplication().startActivity(appMain);
                //requires the FLAG_ACTIVITY_NEW_TASK flag


            case R.id.create:
                Toast.makeText(this, "Create CASE", Toast.LENGTH_SHORT).show();
                //secondWidget
                final WindowManager.LayoutParams params2 = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        layout_parms2,
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);


                //getting windows services and adding the floating view to it
                mWindowManager2 = (WindowManager) getSystemService(WINDOW_SERVICE);
                mWindowManager2.addView(myFloatingView2, params2);


                myFloatingView2.findViewById(R.id.thisIsAnID2).setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        Log.d("TOUCH", "CREATE IS TOUCHED");
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                initialXX = params2.x;
                                initialXX = params2.y;

                                initialTouchXX = motionEvent.getRawX();
                                initialTouchYY = motionEvent.getRawY();
                                return true;

                            case MotionEvent.ACTION_UP:

                                return true;

                            case MotionEvent.ACTION_MOVE:
                                //this code is helping the widget to move around the screen with fingers
                                params2.x = initialXX + 60 + (int) (motionEvent.getRawX() - initialTouchXX);
                                params2.y = initialYY + (int) (motionEvent.getRawY() - initialTouchYY);
                                mWindowManager.updateViewLayout(myFloatingView2, params2);
                                return true;

                        } return false;}});

                break;


                    }



    }





    }


