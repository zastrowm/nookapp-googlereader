C:\SDKs\android\tools\adb.exe shell mount -w -o remount,rw /dev/block/mtdblock0 /system
C:\SDKs\android\tools\adb.exe shell mkdir /system/media/sdcard
C:\SDKs\android\tools\adb.exe shell mount -w -t vfat /dev/block/mmcblk0 /system/media/sdcard