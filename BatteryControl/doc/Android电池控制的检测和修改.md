#0. 用途
Android设备默认电池策略是**均衡模式**或**省电模式**, 很少以**性能模式**运行. 而大型游戏需要CPU&GPU全速运行才流畅.  
第三方应用如何检测和修改设备电池策略?  
#1. 检测
**读cpu配置文件**  
路径 /sys/devices/system/cpu/cpu0/cpufreq/  
**文件解释**  
affected_cpus: 显示修改频率会影响哪些处理器  
cpuinfo_cur_freq: CPU的当前工作频率**(Android没有)**  
cpuinfo_min_freq: CPU的最低工作频率  
cpuinfo_max_freq: CPU的最高工作频率  
cpuinfo_transition_latency: CPU在两个不同频率之间切换所需时间  
cpu_utilization: CPU利用率  
related_cpus: 需要软件或者硬件来协调频率的CPU列表  
scaling_available_frequencies: 通过echo命令, 可调节的主频率列表  
scaling_available_governors: 通过echo命令, 可调节的策略模式列表  
scaling_cur_freq: 被governor和cpufreq决定的当前CPU工作频率. 该频率是内核认为该CPU当前运行的主频率  
scaling_driver: 该CPU当前使用的cpufreq驱动程序  
scaling_governor: 当前策略模式  
scaling_min_freq: 当前策略模式的下限  
scaling_max_freq: 当前策略模式的上限**(Android没有)**  
    在linux平台, 需要首先设置scaling_max_freq, 再设置scaling_min_freq  
scaling_setspeed: 如果用户选择了自定义模式(userspace), 那么可调节的频率  
    在scaling_min_freq 和 scaling_max_freq之间, **(Android不支持)**  

**策略模式:**  

- performance - 最强性能模式  
- powersave - 省电模式  
- ondemand - 自动调节  
- userspace - 用户自定义  

**机型实测:**  
小米手机的性能模式和均衡模式都是ondemand, 只是max_freq不同. 用户无法启用performance模式.  
那么最强performance模式的用途是什么? 猜测只在跑分时开启?  

#2. 静默修改
**需要root或system权限**  

```
su  
echo performance > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor  
```  

#3. 调用系统设置
1. API Level 22, Android 5.1, 精确跳转  
```
//android.provider.settings.ACTION_BATTERY_SAVER_SETTINGS  
Intent i = new Intent("android.settings.BATTERY_SAVER_SETTINGS");  
startActivity(i);
```
2. Android源码标准实现方式是系统设置Activity + 子设置项是fragment  
**MIUI虽然是自定义实现, 但接口实现与Android标准一致, 因此可用**
```
Intent i = new Intent();  
i.setClassName("com.android.settings", "com.android.settings.Settings");  
i.setAction(Intent.ACTION_MAIN);  
i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK  
         | Intent.FLAG_ACTIVITY_CLEAR_TASK  
         | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);  
i.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, "com.android.settings.fuelgauge.BatterySettings");  
startActivity(i);  
```
3. 华为手机, HWSettingsActivity  
```
TODO:  
```

#4. 源码  
[https://github.com/9468305/android](https://github.com/9468305/android)  