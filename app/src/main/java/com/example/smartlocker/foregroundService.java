package com.example.smartlocker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class foregroundService extends Service
{
    public Handler handler = null;
    public static Runnable runnable = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        NotificationChannel channel = new NotificationChannel("foreground Service ID","foreground Service ID", NotificationManager.IMPORTANCE_LOW);
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this,"foreground Service ID")
                .setContentText("Smart lock is running")
                .setSmallIcon(R.drawable.timer_lock_icon);
        startForeground(1001,notification.build());

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                checkApps();
                handler.postDelayed(runnable, 2000);
            }
        };
        handler.postDelayed(runnable, 2000);
        return super.onStartCommand(intent, flags, startId);
    }

    private void checkApps()
    {
        String topPackageName;
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP){
            UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            long currentTime = System.currentTimeMillis();
            List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,currentTime-1000*10,currentTime);
            if (stats!=null){
                SortedMap<Long,UsageStats> sortedMap = new TreeMap<Long,UsageStats>();
                for (UsageStats usageStats : stats){
                       sortedMap.put(usageStats.getLastTimeUsed(),usageStats);
                }
                if (!sortedMap.isEmpty()){
                    topPackageName = sortedMap.get(sortedMap.lastKey()).getPackageName();
                    sqlHelper helper = new sqlHelper(getApplicationContext());
                    StringBuilder aa = helper.displayApp(topPackageName);
                    if (topPackageName.equals(aa.toString())){
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.setClass(this,blockScreenActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        /*Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        this.startActivity(intent);*/
                    }
                   // Toast.makeText(this, topPackageName, Toast.LENGTH_SHORT).show();
                }
            }
        }


    }
}