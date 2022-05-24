set -x
cd $ANDROID_HOME/emulator
./emulator -avd rishi1 -writable-system &
sleep 1m
adb -s emulator-5554 root
adb -s emulator-5554 remount
ca=/home/abc/.mitmproxy/mitmproxy-ca-cert.pem
hash=$(openssl x509 -noout -subject_hash_old -in $ca)
adb -s emulator-5554 push $ca /system/etc/security/cacerts/$hash.0
adb -s emulator-5554 remount
