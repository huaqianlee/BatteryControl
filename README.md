# android_battery_control
An simple project to show how to get and set android battery control solutions.  
  
getCurrentGovernor = cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor  
getAllGovernor = cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors  
setCurrentGovernor = echo governor > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor  
goto_system_battery_control = startActivity  
    with Intent **com.android.settings, com.android.settings.Settings**  
    and fragment **com.android.settings.fuelgauge.BatterySettings**  
