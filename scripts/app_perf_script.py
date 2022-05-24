import os
import sys

args = sys.argv[1:]

count = args[0]
close_package = args[1]
activity = args[2]

script = 'for i in `seq 1 ' + count + '`; do adb shell am force-stop ' + close_package + '; sleep 1; adb shell am start-activity -W -n ' + close_package + '/.' + activity + '; done > app_run_file.txt'

os.system(script)

s2 = "TotalTime: "
li = []
with open('app_run_file.txt', 'r+') as file:
    for i in file.readlines():
        if(s2 in i):
            li.append(int(i[i.index(s2) + len(s2):len(i) - 1]))
            
file.write('Average Time : ')
file.write(str(sum(li) / len(li)))

