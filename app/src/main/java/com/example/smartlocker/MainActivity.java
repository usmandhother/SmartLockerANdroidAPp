package com.example.smartlocker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hbb20.CountryCodePicker;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView RV;
    private InstalledAppsAdapter adapter;
    private StringBuffer stringBuffer=null;
    private long timeLeft;
    private CountDownTimer countDownTimer;
    private boolean timerRunning;
    private sqlHelper helper;
    private TextView title;
    private Toolbar toolbar;
    private FloatingActionButton fbtnLock,fbtnUnLock;

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.item_lockedApps){
            getLockedApps();
        }
        else if (id == R.id.item_AllApps){
            displayAllApps();
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fbtnLock=(FloatingActionButton)findViewById(R.id.f_btn_lock);
        fbtnUnLock=(FloatingActionButton)findViewById(R.id.f_btn_Unlock);
        title = (TextView) findViewById(R.id.title_apps);
        helper = new sqlHelper(this);
        Intent intent = new Intent(getApplicationContext(), foregroundService.class);
        startForegroundService(intent);

        displayAllApps();

        fbtnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                stringBuffer= new StringBuffer();
                for (AppInfo a : adapter.checkedApps)
                {
                    stringBuffer.append(a.getAppName());
                    stringBuffer.append("\n");
                }
                if (adapter.checkedApps.size()>0)
                {
                    //Toast.makeText(MainActivity.this, stringBuffer.toString(), Toast.LENGTH_LONG).show();
                    timerInputDialogBox();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Please Select Apps...", Toast.LENGTH_LONG).show();
                }
            }
        });
        fbtnUnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unLockApps();
                getLockedApps();
            }
        });

    }

    private void getLockedApps()
    {
        title.setText("Locked Apps");
        fbtnLock.setVisibility(Button.INVISIBLE);
        fbtnUnLock.setVisibility(Button.VISIBLE);
        ArrayList<AppInfo> holder = new ArrayList<>();
        List<ApplicationInfo> infos = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
        PackageManager packageManager = getPackageManager();
        for (ApplicationInfo info : infos)
        {
            if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 1)
            {
                continue;
            }
            if (info.packageName.equals(helper.displayApp(info.packageName).toString())) {
                AppInfo obj = new AppInfo();
                obj.setPackageName(info.packageName);
                obj.setAppName((String) info.loadLabel(packageManager));
                obj.setAppIcon(info.loadIcon(packageManager));
                holder.add(obj);
            }
        }
            RV = (RecyclerView) findViewById(R.id.rv_app);
            RV.setLayoutManager(new LinearLayoutManager(this));
            adapter = new InstalledAppsAdapter(this, holder);
            RV.setItemAnimator(new DefaultItemAnimator());
            RV.setAdapter(adapter);
    }

    private void displayAllApps()
    {
        title.setText("All Apps");
        fbtnUnLock.setVisibility(Button.INVISIBLE);
        fbtnLock.setVisibility(Button.VISIBLE);
        ArrayList<AppInfo> holder = new ArrayList<>();
        List<ApplicationInfo> infos = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
        PackageManager packageManager = getPackageManager();
        for (ApplicationInfo info : infos)
        {
            if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 1)
            {
                continue;
            }
            if (info.packageName.equals(helper.displayApp(info.packageName).toString()) || info.packageName.equals("com.example.smartlocker")){
                continue;
            }
            else {
                AppInfo obj = new AppInfo();
                obj.setPackageName(info.packageName);
                obj.setAppName((String) info.loadLabel(packageManager));
                obj.setAppIcon(info.loadIcon(packageManager));
                holder.add(obj);
            }
        }
        RV = (RecyclerView) findViewById(R.id.rv_app);
        RV.setLayoutManager(new LinearLayoutManager(this));
        adapter= new InstalledAppsAdapter(this,holder);
        RV.setItemAnimator(new DefaultItemAnimator());
        RV.setAdapter(adapter);
    }

    private void timerInputDialogBox()
    {
        View view = getLayoutInflater().inflate(R.layout.get_minutes_layout,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Set Timer to lock Apps");
        EditText minutesInput = view.findViewById(R.id.minute_txt);
        minutesInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(view);
        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                String input = minutesInput.getText().toString();
                if (input.length()==0){
                    Toast.makeText(MainActivity.this, "Field can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                long millisInput= Long.parseLong(input)*60000;
                if (millisInput==0){
                    Toast.makeText(MainActivity.this, "Please Enter Positive number", Toast.LENGTH_SHORT).show();
                    return;
                }
                setTimer(millisInput);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void setTimer(long inputTime)
    {
        countDownTimer = new CountDownTimer(inputTime,1000)
        {
            @Override
            public void onTick(long l) {
                timeLeft = l;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                Toast.makeText(MainActivity.this, "Timer End", Toast.LENGTH_LONG).show();
                for (AppInfo a : adapter.checkedApps) {
                    Boolean rzlt = helper.insertCheckedApps(a.getAppName(), a.getPackageName());
                }
            }
        }.start();
        timerRunning=true;
    }

    private void updateCountDownText()
    {
        int hours = (int) (timeLeft/1000)/3600;
        int minutes = (int) ((timeLeft/1000)%3600)/60;
        int seconds = (int) (timeLeft/1000)%60;
        String timeLeftFormatted = String.format(Locale.getDefault(),"%d:%02d:%02d",hours,minutes,seconds);
        Toast.makeText(this, "Time left: "+timeLeftFormatted, Toast.LENGTH_SHORT).show();
    }

    private void unLockApps()
    {
        ArrayList<String> list = new ArrayList<>();
        for (AppInfo a : adapter.checkedApps)
        {
            list.add(a.getAppName());
        }
        if (adapter.checkedApps.size()>0)
        {
            for (int i=0;i<list.size();i++){
                if (helper.deleteApps(list.get(i))){
                    Toast.makeText(this, "App Unlocked", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else
        {
            Toast.makeText(MainActivity.this, "Please Select Apps...", Toast.LENGTH_LONG).show();
        }
    }

}