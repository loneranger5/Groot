package com.jsb.youtubetest;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button start,replay,deleteRec;
    //FrameLayout mLayout;
    private static final int SYSTEM_ALERT_WINDOW_PERMISSION = 2084;
    private static final int SYSTEM_ACCESSIBILITY_PERMISSION = 1024;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        start = findViewById(R.id.button);
//        pause = findViewById(R.id.button2);
//
//        start.setOnClickListener(this);
//        pause.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            askPermission();
        }
        start = findViewById(R.id.start_stop);
        replay = findViewById(R.id.replay);
        deleteRec = findViewById(R.id.deleteRec);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    if(isAccessibilityServiceEnabled(getApplicationContext(),accessibilityService.class)){
                    startService(new Intent(MainActivity.this, FloatingWindow.class));
                    }else{
                        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS,Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent,SYSTEM_ACCESSIBILITY_PERMISSION );

                    }
                } else if (Settings.canDrawOverlays(getApplicationContext())) {
                    if(isAccessibilityServiceEnabled(getApplicationContext(),accessibilityService.class)){
                        startService(new Intent(MainActivity.this, FloatingWindow.class));
                    }else{
                        Intent gintent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        startActivityForResult(gintent,SYSTEM_ACCESSIBILITY_PERMISSION );

                    }

                } else {
                    askPermission();
                    Toast.makeText(getApplicationContext(), "You need System Alert Window Permission to do this", Toast.LENGTH_SHORT).show();
                }
            }

        });


        //Replay


        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent replay = new Intent(getApplicationContext(),accessibilityService.class);
                replay.putExtra("action","Replay");
                startService(replay);
            }
        });


        deleteRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File configFile  = new File(getFilesDir()+"/groot.config");
                if(configFile.exists())

                {

                    configFile.delete();
                    Toast.makeText(MainActivity.this, "Old Recordings are Deleted Sucessfully", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }




    private void askPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION);
    }



    public static boolean isAccessibilityServiceEnabled(Context context, Class<? extends AccessibilityService> service) {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);

        for (AccessibilityServiceInfo enabledService : enabledServices) {
            ServiceInfo enabledServiceInfo = enabledService.getResolveInfo().serviceInfo;
            if (enabledServiceInfo.packageName.equals(context.getPackageName()) && enabledServiceInfo.name.equals(service.getName()))
                return true;
        }

        return false;
    }

}



