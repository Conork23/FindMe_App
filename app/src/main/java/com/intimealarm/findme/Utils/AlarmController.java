package com.intimealarm.findme.Utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;

import com.intimealarm.findme.BroadcastRecievers.LocationAlarmReceiver;

import static android.content.Context.MODE_PRIVATE;

/**
 * @author Conor Keenan
 * Student No: x13343806
 * Created on 20/03/2017.
 */

public class AlarmController {
    Context context;
    AlarmManager aManager;
    final SharedPreferences sharedPref;

    public AlarmController(Context context) {
        this.context = context;
        aManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        sharedPref = context.getSharedPreferences(Constants.PREFS_FILE, MODE_PRIVATE);
    }

    public void setLocationAlarm(){
        int position = sharedPref.getInt(Constants.SHARED_INTERVAL, 0);
        setLocationServiceAlarm(position);
    }

    private void setLocationServiceAlarm(int arrPos) {
        Intent intent = new Intent(context, LocationAlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, Constants.ALARM_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if(arrPos == 0){
            stopLocations(alarmIntent);
        }else{

            long interval = getInterval(arrPos);

            aManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),
                    interval, alarmIntent);
        }

    }
    private long getInterval(int arrPos) {
        long l = 0;
        switch (arrPos){
            case 1:
                l = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
                break;
            case 2:
                l = AlarmManager.INTERVAL_HALF_HOUR;
                break;
            case 3:
                l = AlarmManager.INTERVAL_HOUR;
                break;
            case 4:
                l =  AlarmManager.INTERVAL_HALF_DAY;
                break;
            case 5:
                l =  AlarmManager.INTERVAL_DAY;
                break;
            default:
                return 0;
        }
        Log.d(Constants.TAG_SETTINGS_ACTIVITY, "getInterval: " + arrPos);
        return l;
    }

    private void stopLocations(PendingIntent alarmIntent){
        Log.d("test", "stopLocations: ");
        if (aManager != null) {
            aManager.cancel(alarmIntent);
        }
    }

    public void disableAlarm() {
        setLocationServiceAlarm(0);
    }
}
