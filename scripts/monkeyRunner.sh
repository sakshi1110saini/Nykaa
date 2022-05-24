dir=$(pwd)
apk=$1
env=$2
platform=$3
echo "env is $env"
echo "platforn is $platform"
echo 'starting test'
current_time=$(date "+%Y.%m.%d-%H.%M.%S")
mkdir $dir/MonkeyRunner
file=$dir/MonkeyRunner/$current_time'-'exe_log
echo "log file created  $file"

echo "getting data from dataengine"
test_url='http://13.127.152.76:9090/nyk/getdata'
data_raw="{\"key\":\"sp_less_than_500_plp_url\",\"environment\":\"${env}\",\"platform\":\"${platform}\"}"
a=$(curl --request POST "$test_url" \
--header 'nyk-header: nyk-auth' \
--header 'Content-Type: application/json' \
--data-raw "$data_raw")
plp=$(echo $a | sed 's/\\//g' | cut -d '"' -f 4)
echo "plp url less than 500 : $plp" 


data_raw="{\"key\":\"sp_greater_than_500_plp_url\",\"environment\":\"${env}\",\"platform\":\"${platform}\"}"
a=$(curl --request POST "$test_url" \
--header 'nyk-header: nyk-auth' \
--header 'Content-Type: application/json' \
--data-raw "$data_raw")
plp2=$(echo $a | sed 's/\\//g' | cut -d '"' -f 4)
echo "plp url greater than 500 : $plp2" 


data_raw="{\"key\":\"both_nykaa_pro_product_plp\",\"environment\":\"${env}\",\"platform\":\"${platform}\"}"
a=$(curl --request POST "$test_url" \
--header 'nyk-header: nyk-auth' \
--header 'Content-Type: application/json' \
--data-raw "$data_raw")
plppro=$(echo $a | sed 's/\\//g' | cut -d '"' -f 4)
echo "plp url both pro and nykaa products: $plppro" 

data_raw="{\"key\":\"offer_product_pdp_url\",\"environment\":\"${env}\",\"platform\":\"${platform}\"}"
a=$(curl --request POST "$test_url" \
--header 'nyk-header: nyk-auth' \
--header 'Content-Type: application/json' \
--data-raw "$data_raw")
pdpoffer=$(echo $a | sed 's/\\//g' | cut -d '"' -f 4)
echo "pdp offer page : $pdpoffer" 


data_raw="{\"key\":\"sp_less_than_500_pdp_url\",\"environment\":\"${env}\",\"platform\":\"${platform}\"}"
a=$(curl --request POST "$test_url" \
--header 'nyk-header: nyk-auth' \
--header 'Content-Type: application/json' \
--data-raw "$data_raw")
pdp=$(echo $a | sed 's/\\//g' | cut -d '"' -f 4)
echo "pdp url  less than 500 : $pdp" 

data_raw="{\"key\":\"sp_greater_than_500_pdp_url\",\"environment\":\"${env}\",\"platform\":\"${platform}\"}"
a=$(curl --request POST "$test_url" \
--header 'nyk-header: nyk-auth' \
--header 'Content-Type: application/json' \
--data-raw "$data_raw")
brand=$(echo $a | sed 's/\\//g' | cut -d '"' -f 4)
echo "pdp url  for brand : $brand" 


echo 'killing emulator'
adb devices | grep emulator | cut -f1 | while read line; do adb -s $line emu kill; done | exit 0
sleep 5s

echo "launching the device"
device=$(emulator -list-avds)
echo $device
emulator -avd $device -no-snapshot-load &
echo 'sleeping for 10s'
sleep 10s


monkey_command='monkey --ignore-crashes -p com.fsn.nykaa -v 8000 --pct-syskeys 1 --pct-appswitch 1 --pct-touch 80 logcat -d'

echo $monkey_command

echo 'Turning wifi ON'
adb shell svc wifi enable 
sleep 7s

echo 'installing apk'
adb uninstall com.fsn.nykaa | exit 0apkapk
echo "received apk $apk"
adb install -r $apk
sleep 5s

echo 'launching app using adb'
adb shell am start -n com.fsn.nykaa/com.fsn.nykaa.SplashScreenActivity
sleep 10s

echo 'skipping login'
adb shell input tap 300 1334
sleep 30s

echo 'starting monkey runner'
adb shell $monkey_command >> $file

echo '1. trying again ----'
echo 'launching app using adb'
adb shell am start -a android.intent.action.VIEW -d "$plp"
sleep 30s
echo 'starting monkey runner'
adb shell $monkey_command >> $file


echo 'Turning wifi ON'
adb shell svc wifi enable
sleep 20s


echo '2. trying again ----'

echo 'launching app using adb'
adb shell am start -a android.intent.action.VIEW -d "$pdp"
sleep 30s
echo 'starting monkey runner'
adb shell $monkey_command >> $file
\
echo 'Turning wifi ON'
adb shell svc wifi enable
sleep 20s


echo '3. trying again ----'

echo 'launching app using adb'
adb shell am start -a android.intent.action.VIEW -d "$brand"
sleep 30s
echo 'starting monkey runner'
adb shell $monkey_command >> $file

echo 'Turning wifi ON'
adb shell svc wifi enable
sleep 20s


echo '4. trying again ----'

echo 'launching app using adb'
adb shell am start -a android.intent.action.VIEW -d "$pdp2"
sleep 30s
echo 'starting monkey runner'
adb shell $monkey_command >> $file

echo 'Turning wifi ON'
adb shell svc wifi enable
sleep 20s


echo '5. trying again ----'
echo 'launching app using adb'
adb shell am start -a android.intent.action.VIEW -d "$plppro"
sleep 30s
echo 'starting monkey runner'
adb shell $monkey_command >> $file


echo '6. trying again ----'

echo 'launching app using adb'
adb shell am start -a android.intent.action.VIEW -d "$pdpoffer"
sleep 30s
echo 'starting monkey runner'
adb shell $monkey_command >> $file

echo 'killing emulator'
adb emu kill

echo 'assertion to check the crash'
a=$(cat $file | grep -i 'CRASH: com.fsn.nykaa')
if [ $a != '' ]
then
	echo '********************** FOUND CRASH **********************'
	exit 1

else
	echo '##################### NO FOUND CRASH #####################'
fi
