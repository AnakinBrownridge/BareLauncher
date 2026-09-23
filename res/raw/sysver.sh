#!/system/bin/sh
uname -a
printf '\n---\n'
cat /proc/version
printf '\n---\n'
getprop ro.build.display.id
printf '\n---\n'
getprop ro.build.version.release
