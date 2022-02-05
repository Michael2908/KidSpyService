package com.classy.kidspyservice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AppOpsManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!checkUsageStatsAllowedOrNot()){
            Intent usageAccessIntent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            usageAccessIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(usageAccessIntent);
            if (checkUsageStatsAllowedOrNot()){
                Log.e( "onCreate: ", "service started" );
                startService(new Intent(MainActivity.this,BackgroundService.class));
            }else{
                Toast.makeText(getApplicationContext(),"Please give Access",Toast.LENGTH_SHORT);
            }
        }else{
            Log.e( "onCreate: ", "service started" );
            startService(new Intent(MainActivity.this,BackgroundService.class));
        }
    }
    public boolean checkUsageStatsAllowedOrNot(){
        try{
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager)getSystemService(APP_OPS_SERVICE);
            int mode=appOpsManager.unsafeCheckOp(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid,applicationInfo.packageName);
            return (mode==AppOpsManager.MODE_ALLOWED);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"error cannot found any usage status manager",Toast.LENGTH_SHORT);
            return false;
        }

    }

    @Override
    protected void onDestroy() {
        if(checkUsageStatsAllowedOrNot()){
            startService(new Intent(MainActivity.this,BackgroundService.class));
        }
        super.onDestroy();
    }
}