package com.classy.kidspyservice;


import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;

import android.content.Intent;

import android.os.IBinder;
import android.util.Log;


import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class BackgroundService extends Service {
    //private FirebaseAuth mAuth ;
    public FirebaseFirestore db;
    //change email, password to check on your phone else you might see my app usage :D
    //  public String email ="test@email.com", password = "123455";
    public String uid;

    public BackgroundService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        db = FirebaseFirestore.getInstance();
        // mAuth = FirebaseAuth.getInstance();
        //mAuth.createUserWithEmailAndPassword(email, password);
        // mAuth.signInWithEmailAndPassword(email,password);
        //  uid = mAuth.getCurrentUser().getUid();
        // uid 1234 12345 123456 used
        uid = "1234567";
        TimerTask detectApp = new TimerTask() {
            @Override
            public void run() {
                UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
                long endTime = System.currentTimeMillis();
                long beginTime = endTime - (1000);
                Map<String, Object> apps = new HashMap<>();
                List<UsageStats> usageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, beginTime, endTime);
                if (usageStats != null) {
                    for (UsageStats usageStat : usageStats) {
                        long time;
                        if (usageStat.getPackageName().toLowerCase().contains("com.whatsapp")) {
                            time = usageStat.getTotalTimeInForeground();
                            Log.e("test", String.valueOf(time));
                            apps.put("Whatsapp", calculate_time(time));
                            db.collection(uid).document("AppUsage").set(apps);
                        }
                        if (usageStat.getPackageName().toLowerCase().contains("com.facebook.katana")) {
                            time = usageStat.getTotalTimeInForeground();
                            Log.e("test", String.valueOf(time));
                            apps.put("Facebook", calculate_time(time));
                            db.collection(uid).document("AppUsage").set(apps);
                        }
                        if (usageStat.getPackageName().toLowerCase().contains("com.netflix")) {
                            time = usageStat.getTotalTimeInForeground();
                            Log.e("test", String.valueOf(time));
                            apps.put("Netflix", calculate_time(time));
                            db.collection(uid).document("AppUsage").set(apps);

                        }
                    }
                }

            }

            ;
        };
        Timer detectAppTimer = new Timer();
        detectAppTimer.scheduleAtFixedRate(detectApp, 0, 1000);
        return super.onStartCommand(intent, flags, startId);

    }

    public String calculate_time(long time) {
        long second, minute, hour;
        second = (time / 1000) % 60;
        minute = (time / (1000 * 60)) % 60;
        hour = (time / (1000 * 60 * 60)) % 60;
        String time_val = hour + " h " + minute + " m " + second + " s ";
        return time_val;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}