package com.shaddock.power;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PowerActivity extends Activity {

    private static final String TAG = "shaddock";  
    private static final String KCPUFreqPath = "/sys/devices/system/cpu/cpu0/cpufreq";  
    
    private TextView mTV = null;
    private Button mButton = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power);
        
        mTV = (TextView)findViewById(R.id.tv_power);
        mButton = (Button)findViewById(R.id.btn_gotosetting);
        
        mButton.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                boolean result = gotoBatterySettings();
                if(!result) {
                    gotoFail();
                }
            }});
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateGovernor();
    }

    private void updateGovernor() {
        StringBuilder sb = new StringBuilder(10);
        sb.append(this.getString(R.string.cur_governor) + " - " + getCurrentGovernor() + "\n\n");
        sb.append(this.getString(R.string.all_governor) + "\n\n");
        String all[] = getAllGovernor();
        if(all != null) {
            for(String x : all) {
                sb.append(x + "\n");
            }
        }
        
        sb.append(getString(R.string.governor_desp0));
        sb.append(getString(R.string.governor_desp1));
        sb.append(getString(R.string.governor_desp2));
        sb.append(getString(R.string.governor_desp3));
        sb.append(getString(R.string.governor_desp4));
        sb.append(getString(R.string.governor_desp5));
        sb.append(getString(R.string.governor_desp6));
        
        mTV.setText(sb.toString());
    }

    private boolean gotoBatterySettings() {
        Intent i = new Intent();
        i.setClassName("com.android.settings", "com.android.settings.Settings");
        i.setAction(Intent.ACTION_MAIN);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                 | Intent.FLAG_ACTIVITY_CLEAR_TASK
                 | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        i.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, "com.android.settings.fuelgauge.BatterySettings");

        if(getPackageManager().resolveActivity(i, PackageManager.MATCH_DEFAULT_ONLY) != null) {
            try {
                startActivity(i);
                return true;
            } catch (android.content.ActivityNotFoundException anfe) {
                anfe.printStackTrace();
            } catch (SecurityException se) {
                se.printStackTrace();
            }
        }
        return false;
    }

    private void gotoFail(){
        Toast.makeText(this, R.string.goto_fail, Toast.LENGTH_LONG).show();
    }
    
    private static String[] getAllGovernor() {
        Process process;
        try {
            process = Runtime.getRuntime().exec("cat " + KCPUFreqPath + "/scaling_available_governors");
            Scanner scanner = new Scanner(process.getInputStream());
            return scanner.nextLine().split(" ");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getCurrentGovernor() {
        Process process;
        try {
            process = Runtime.getRuntime().exec("cat " + KCPUFreqPath + "/scaling_governor");
            Scanner scanner = new Scanner(process.getInputStream());
            return scanner.nextLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void setCurrentGovernor(String governor) {
        Process process;
        try {
            process = Runtime.getRuntime().exec("su");
            OutputStream os = process.getOutputStream();
            String command = "echo " + governor + " > " + KCPUFreqPath + "/scaling_governor\n";
            String exit = "exit\n";
            os.write(command.getBytes());
            os.write(exit.getBytes());
            os.flush();
            process.waitFor();
            Log.i(TAG, "exit value = " + process.exitValue());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void setCurrentGovernor2(String governor) {
        Process process;
        try {
            String[] command = {"echo ", governor, ">", KCPUFreqPath + "/scaling_governor"};
            process = Runtime.getRuntime().exec(command);
            process.waitFor();
            Log.i(TAG, "exit value = " + process.exitValue());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void gotoBatterySettings2() {
        Intent i = new Intent(Settings.ACTION_SETTINGS);
        i.setComponent(new ComponentName("com.android.settings","com.android.settings.SubSettings"));
        i.putExtra( PreferenceActivity.EXTRA_SHOW_FRAGMENT, "com.android.settings.fuelgauge.BatterySettings" );
        //i.putExtra( PreferenceActivity.EXTRA_NO_HEADERS, true );
        startActivity(i);
    }
    
}
