package com.jsb.youtubetest;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class FloatingWindow extends Service {
    private Handler mHandler, GHandler;

    private Boolean gestureFlag = false;

    private String mode = "Tap";
    private Boolean recordFlag = false;

    private ImageView start_stop_rec;

    private int seq = 0;

    private File configFile;


    //data storage variables

    //  File


    //secondWidget Drag variables

    private int initialXX, initialSwipeXX, startSwipeXX, endSwipeXX, startXX, endXX;
    private int initialYY, initialSwipeYY, startSwipeYY, endSwipeYY, startYY, endYY;
    private int initialTouchXX, initialSwipeTouchXX, startSwipeTouchXX, startTapTouchXX;
    private int initialTouchYY, initialSwipeTouchYY, startSwipeTouchYY, startTapTouchYY;
    public int endTapTouchYY, endTapTouchXX, endSwipeTouchXX, endSwipeTouchYY;


    WindowManager wm, wm2, wm3;
    View floatingView, collapsedView, expandedView, secondWidget, thirdWidget;


    @Override
    public void onCreate() {
        super.onCreate();


        //additional widget and gesture Inject Handler
        HandlerThread handlerThread = new HandlerThread("tap-handler");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());

        GHandler = new Handler(handlerThread.getLooper());


        //floating view
        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_widget_view, null);
        secondWidget = LayoutInflater.from(this).inflate(R.layout.second_widget, null);
        thirdWidget = LayoutInflater.from(this).inflate(R.layout.third_widget, null);

        start_stop_rec = floatingView.findViewById(R.id.Widget_Start_Rec);


        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm2 = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm3 = (WindowManager) getSystemService(WINDOW_SERVICE);

//        ll = new LinearLayout(this);
//        ll.setBackgroundColor(Color.TRANSPARENT);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT);
//        ll.setLayoutParams(layoutParams);


        //config_create


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        seq = 1;

        //File Write for coordinates making a clean file

        if(create_file()) {
            //config_write("SEQ \tMODE \tX \tY \tdX \tdY\n");}
        }



        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE// | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS ,
                , PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = 0;


        wm.addView(floatingView, params);


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

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:


                        x = updatepar.x;
                        y = updatepar.y;

                        px = motionEvent.getRawX();
                        py = motionEvent.getRawY();

                        break;


                    case MotionEvent.ACTION_MOVE:

                        updatepar.x = (int) (x + (motionEvent.getRawX() - px));
                        updatepar.y = (int) (y + (motionEvent.getRawY() - py));

                        wm.updateViewLayout(floatingView, updatepar);

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


                if (!recordFlag) {
                    start_stop_rec.setImageResource(R.drawable.floating_widget_stop_record_foreground);
                    recordFlag = true;

                    if (mRunnable == null) {
                        mRunnable = new IntervalRunnable();
                    }

                    mHandler.post(mRunnable);
                } else if (recordFlag) {
                    start_stop_rec.setImageResource(R.drawable.floating_widget_start_rec_foreground);
                    recordFlag = false;
                    //flush hashmap or arraylist
                    if (secondWidget.isAttachedToWindow()) {
                        wm2.removeView(secondWidget); //once stop is pressed it will remove extra widgets.

                    }
                    if (thirdWidget.isAttachedToWindow()) {
                        wm3.removeView(thirdWidget);//once stop is pressed it will remove extra widgets
                    }
                }


                if (secondWidget.isAttachedToWindow()) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    Toast.makeText(FloatingWindow.this, "Tap Widget is already present", Toast.LENGTH_SHORT).show();
                }

            }

        });

        floatingView.findViewById(R.id.Widget_Add_Rec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(FloatingWindow.this, "Add Record", Toast.LENGTH_SHORT).show();

                //passing coordinate  variable to gesture intent fails gesture rather sending variables , sending coordinates it accepts and gesture is performed



             //   Toast.makeText(FloatingWindow.this, "Gesture Flag " + gestureFlag, Toast.LENGTH_SHORT).show();
                if (recordFlag) {
                    Intent gestureIntent = new Intent(getApplicationContext(), accessibilityService.class);
                    if (mode == "Tap") {
                        //Bundle gestureBundle = new Bundle();
                      gestureIntent.putExtra("action", mode);
                        gestureIntent.putExtra("x", endTapTouchXX);
                        gestureIntent.putExtra("y", endTapTouchYY);

                       // gestureIntent.putExtras(gestureBundle);







                    } else if (mode == "Swipe") {


                        //Bundle gestureBundle = new Bundle();
                        gestureIntent.putExtra("action", mode);
                        gestureIntent.putExtra("x", endTapTouchXX);
                        gestureIntent.putExtra("y", endTapTouchYY);
                        gestureIntent.putExtra("dX", endSwipeTouchXX);
                        gestureIntent.putExtra("dY", endSwipeTouchYY);

                        //gestureIntent.putExtras(gestureBundle);
                    }


                    //@loneranger
                    //Making sure to remove the second widget before calling gesture injection or else gesture inject will work on top of second widget which results the widget being tapped not the app or option whats behind the scenes
                    //wm2.removeView(secondWidget); -- important thing before injecting gestures.
                    // if condition checks whether there is secondWidget Present if there is no then it will endup in null pointer exception so adding this checks improves stability of device.
                    if (secondWidget.isAttachedToWindow()) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        wm2.removeView(secondWidget);

                    }
                    if (thirdWidget.isAttachedToWindow()) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        wm3.removeView(thirdWidget);
                    }


                    //config write

                    if(mode == "Tap")

                    {

                        try {
                            config_write(seq + "," + mode + "," + endTapTouchXX +"," + endTapTouchYY +"\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(mode == "Swipe")

                    {

                        try {
                            config_write(seq + "," + mode + "," + endTapTouchXX +"," + endTapTouchYY + "," + endSwipeTouchXX + "," +endSwipeTouchYY+ "," + "\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    startService(gestureIntent);

                    // Toast.makeText(FloatingWindow.this, "Tap Widget is already present", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FloatingWindow.this, "Start Recording First", Toast.LENGTH_SHORT).show();


                }
                seq++;



            }
        });


        floatingView.findViewById(R.id.Widget_Mode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog alertDialog = new AlertDialog.Builder(getBaseContext())
                        .setTitle("Input Mode")
                        .setMessage("Select your input mode ")
                        .setPositiveButton("Tap", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                mode = "Tap";


                                if (!recordFlag) {
                                    start_stop_rec.setImageResource(R.drawable.floating_widget_stop_record_foreground);
                                    recordFlag = true;

                                } else if (recordFlag) {
                                    start_stop_rec.setImageResource(R.drawable.floating_widget_start_rec_foreground);
                                    recordFlag = false;
                                }
                                //flush hashmap or arraylist


//                                Toast.makeText(FloatingWindow.this, ""+mode, Toast.LENGTH_SHORT).show();
                                if (!secondWidget.isAttachedToWindow()) {
                                    if (mRunnable == null) {
                                        mRunnable = new IntervalRunnable();
                                    }


                                    mHandler.post(mRunnable);
                                } else {

                                    if(thirdWidget.isAttachedToWindow()){
                                    wm3.removeView(thirdWidget);}

                                }
                            }
                        })
                        .setNegativeButton("Swipe", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mode = "Swipe";
                                //  Toast.makeText(FloatingWindow.this, ""+mode, Toast.LENGTH_SHORT).show();
                                if (!recordFlag) {
                                    start_stop_rec.setImageResource(R.drawable.floating_widget_stop_record_foreground);
                                    recordFlag = true;

                                } else if (recordFlag) {
                                    start_stop_rec.setImageResource(R.drawable.floating_widget_start_rec_foreground);
                                    recordFlag = false;
                                }
                                if (!secondWidget.isAttachedToWindow()) {
                                    if (mRunnable == null) {
                                        mRunnable = new IntervalRunnable();
                                    }


                                    mHandler.post(mRunnable);
                                }

                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                if (!thirdWidget.isAttachedToWindow()){
                                if (GmRunnable == null) {
                                    GmRunnable = new GIntervalRunnable();
                                }


                                GHandler.post(GmRunnable);

                            }}
                        })
                        .create();
                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                alertDialog.show();


            }
        });


        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        seq = 0;
        if (floatingView.isAttachedToWindow()){ wm.removeView(floatingView);} if(secondWidget.isAttachedToWindow()) { wm2.removeView(secondWidget);} if(thirdWidget.isAttachedToWindow()) { wm3.removeView(thirdWidget);}




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
            if (!secondWidget.isAttachedToWindow()) {
                wm2.addView(secondWidget, params2);
            }

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

                            initialXX = updatepar.x;
                            initialYY = updatepar.y;
                            startXX = updatepar.x;
                            startYY = updatepar.y;
                            startTapTouchXX = (int) motionEvent.getRawX();
                            startTapTouchYY = (int) motionEvent.getRawY();

                            Log.d("Coordinates ", " startX and startY " + startXX + " " + startYY);
                            Log.d("Coordinates ", " startTouchX and startTouchY " + startTapTouchXX + " " + startTapTouchYY);

                            initialTouchXX = (int) motionEvent.getRawX();
                            initialTouchYY = (int) motionEvent.getRawY();
                            return true;

                        case MotionEvent.ACTION_MOVE:

                            updatepar.x = initialXX + (int) (-initialTouchXX + motionEvent.getRawX()); // Formula for smooth movement
                            updatepar.y = initialYY + (int) (-initialTouchYY + motionEvent.getRawY()); // Formula for smooth movement
                            wm2.updateViewLayout(secondWidget, updatepar);
                            return true;


                        case MotionEvent.ACTION_UP:

                            endXX = updatepar.x;
                            endYY = updatepar.y;

                            endTapTouchXX = (int) motionEvent.getRawX();
                            endTapTouchYY = (int) motionEvent.getRawY();


                            Log.d("Coordinates ", " endX and endY " + endXX + " " + endYY);

                            Log.d("Coordinates ", " endTouchX and endTouchY " + endTapTouchXX + " " + endTapTouchYY);


                            return true;


                    }
                    return false;
                }
            });


        }


    }


    private GIntervalRunnable GmRunnable;

    private class GIntervalRunnable implements Runnable {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {


            final WindowManager.LayoutParams params3 = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    552//| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    , PixelFormat.TRANSLUCENT);

            //if condition check to check whether the widget is already available in the screen if not then add the view if this condition is not present then app will crash
            if (!thirdWidget.isAttachedToWindow()) {

                wm3.addView(thirdWidget, params3);

            }

            params3.gravity = Gravity.CENTER;
            params3.x = 0;
            params3.y = 0;


            thirdWidget.findViewById(R.id.thirdWidgetMainLayout).setOnTouchListener(new View.OnTouchListener() {
                WindowManager.LayoutParams updatepar2 = params3;

                //this code is helping the widget to move around stable the screen with fingers
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:

                            initialSwipeXX = updatepar2.x;
                            initialSwipeYY = updatepar2.y;
                            // startXX = updatepar2.x;
                            //  startYY = updatepar2.y;
                            startSwipeTouchXX = (int) motionEvent.getRawX();
                            startSwipeTouchYY = (int) motionEvent.getRawY();

                            Log.d("Coordinates ", " startX and startY " + startXX + " " + startYY);
                            Log.d("Coordinates ", " startTouchX and startTouchY " + startTapTouchXX + " " + startTapTouchYY);

                            initialSwipeTouchXX = (int) motionEvent.getRawX();
                            initialSwipeTouchYY = (int) motionEvent.getRawY();
                            return true;

                        case MotionEvent.ACTION_MOVE:

                            updatepar2.x = initialSwipeXX + (int) (-initialSwipeTouchXX + motionEvent.getRawX()); // Formula for smooth movement
                            updatepar2.y = initialSwipeYY + (int) (-initialSwipeTouchYY + motionEvent.getRawY()); // Formula for smooth movement
                            wm3.updateViewLayout(thirdWidget, updatepar2);
                            return true;


                        case MotionEvent.ACTION_UP:

                            endXX = updatepar2.x;
                            endYY = updatepar2.y;

                            endSwipeTouchXX = (int) motionEvent.getRawX();
                            endSwipeTouchYY = (int) motionEvent.getRawY();


                            Log.d("Coordinates ", " endX and endY " + endXX + " " + endYY);

                            Log.d("Coordinates ", " endTouchX and endTouchY " + endTapTouchXX + " " + endTapTouchYY);


                            return true;


                    }
                    return false;
                }
            });


        }
    }


    private Boolean create_file()
    {
        File testFile = new File(this.getFilesDir()+"/groot.config");
        if(testFile.exists()){testFile.delete();}
        File configFileDirectory = new File(getApplicationContext().getFilesDir().toString());
        configFile = new File(configFileDirectory+"/groot.config");
        if(!configFile.exists())
        {
            try {
                configFile.createNewFile();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            return true;
        }else{

            return  false;
        }


    }




    private void config_write(String dataToWrite) throws IOException {



        //configFile.mkdirs();
        try {



            FileWriter fr = new FileWriter(configFile, true);
            BufferedWriter br = new BufferedWriter(fr);
            br.write(dataToWrite);
            br.close();
            fr.close();

            //Clear Stream



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }




}

}

