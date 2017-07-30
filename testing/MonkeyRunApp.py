from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice
from com.android.monkeyrunner.easy import EasyMonkeyDevice, By
import datetime
today = datetime.date.today()
date = today.strftime('%d-%m-%y')

package = 'com.intimealarm.findme'

print "starting...";
print "Connecting to Device..."
device = MonkeyRunner.waitForConnection()

print "Starting MainActivity..."
activity = 'com.intimealarm.findme.MainActivity'
runComponent = package + '/' + activity
device.startActivity(component=runComponent)

MonkeyRunner.sleep(8)

print "Taking Snapshot..."
result = device.takeSnapshot()
result.writeToFile("screenshots/running/MainActivity_"+date+".png",'png')
