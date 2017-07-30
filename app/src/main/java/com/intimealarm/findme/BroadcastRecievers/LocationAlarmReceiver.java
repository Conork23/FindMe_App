package com.intimealarm.findme.BroadcastRecievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.intimealarm.findme.Services.LocationService;
import com.intimealarm.findme.Utils.AlarmController;
import com.intimealarm.findme.Utils.Constants;

/**
 * @author Conor Keenan
 * Student No: x13343806
 * Created on 03/03/2017.
 */

public class LocationAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(Constants.TAG_SETTINGS_ACTIVITY, "onReceive: ");

        if (intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            AlarmController aController = new AlarmController(context);
            aController.setLocationAlarm();
        }else {
            context.startService(new Intent(context, LocationService.class));
        }
    }
}
