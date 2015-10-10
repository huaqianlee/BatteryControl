package com.shaddock.batterycontrol;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.util.Log;

public class Governor {

    private static final String TAG = "shaddock";
    private static final String KCPUFreqPath = "/sys/devices/system/cpu/cpu0/cpufreq";

    public static String[] getAll() {
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

    public static String getCurrent() {
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

    public static boolean setCurrent(String governor) {
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
            return process.exitValue() == 0;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean setCurrent2(String governor) {
        Process process;
        try {
            String[] command = {"echo ", governor, ">", KCPUFreqPath + "/scaling_governor"};
            process = Runtime.getRuntime().exec(command);
            process.waitFor();
            return process.exitValue() == 0;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean gotoBatterySettings(Context ctx) {
        Intent i = null;
        PackageManager pm = ctx.getPackageManager();
        do {
            //API Level 22
            if(Build.VERSION.SDK_INT >= 22) {
                i = new Intent("android.settings.BATTERY_SAVER_SETTINGS");
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                if(null != i.resolveActivity(pm)) {
                    Log.i(TAG, "use API Level 22 - android.settings.BATTERY_SAVER_SETTINGS");
                    break;
                } else {
                    i = null;
                }
            }

            //intent with fragment
            i = new Intent();
            i.setClassName("com.android.settings", "com.android.settings.Settings");
            i.setAction(Intent.ACTION_MAIN);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                     | Intent.FLAG_ACTIVITY_CLEAR_TASK
                     | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            i.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, "com.android.settings.fuelgauge.BatterySettings");
            if(null != i.resolveActivity(pm)) {
                Log.i(TAG, "use fragment com.android.settings.fuelgauge.BatterySettings");
                break;
            } else {
                i = null;
            }

        } while(false);

        if(null != i) {
            try {
                ctx.startActivity(i);
                return true;
            } catch (ActivityNotFoundException anfe) {
                anfe.printStackTrace();
                return false;
            }  catch (SecurityException se) {
                se.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
        /*
        Intent i = new Intent();
        i.setClassName("com.android.settings", "com.android.settings.Settings");
        i.setAction(Intent.ACTION_MAIN);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                 | Intent.FLAG_ACTIVITY_CLEAR_TASK
                 | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        i.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, "com.android.settings.fuelgauge.BatterySettings");

        if(ctx.getPackageManager().resolveActivity(i, PackageManager.MATCH_DEFAULT_ONLY) != null) {
            try {
                ctx.startActivity(i);
                return true;
            } catch (android.content.ActivityNotFoundException anfe) {
                anfe.printStackTrace();
            } catch (SecurityException se) {
                se.printStackTrace();
            }
        }
        return false;*/
    }
    
    public static void gotoBatterySettings2(Context ctx) {
        Intent i = new Intent(Settings.ACTION_SETTINGS);
        i.setComponent(new ComponentName("com.android.settings","com.android.settings.SubSettings"));
        i.putExtra( PreferenceActivity.EXTRA_SHOW_FRAGMENT, "com.android.settings.fuelgauge.BatterySettings" );
        //i.putExtra( PreferenceActivity.EXTRA_NO_HEADERS, true );
        ctx.startActivity(i);
    }
}
