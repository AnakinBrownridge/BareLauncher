#!/system/bin/sh

if [ -f /system/bin/sh ]; then
  /system/bin/sh /data/local/tmp/BareSysVer.sh
else
  echo "System shell unavailable"
fi
