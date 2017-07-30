package com.intimealarm.findme.BroadcastRecievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import com.intimealarm.findme.Models.DeviceLocation;
import com.intimealarm.findme.R;
import com.intimealarm.findme.Services.LocationService;
import com.intimealarm.findme.Utils.Constants;

import static android.content.Context.MODE_PRIVATE;

/**
 * @author Conor Keenan
 * Student No: x13343806
 * Created on 20/03/2017.
 */

public class SmsReceiver extends BroadcastReceiver {
    Context context;
    SharedPreferences sharedPref;


    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        sharedPref = context.getSharedPreferences(Constants.PREFS_FILE, MODE_PRIVATE);

        Boolean toSendSms = intent.getBooleanExtra(Constants.EXTRA_SEND_SMS,false);

        if (toSendSms){
            String sender = intent.getStringExtra(Constants.EXTRA_SMS_SENDER);
            DeviceLocation loc = (DeviceLocation) intent.getSerializableExtra(Constants.EXTRA_DEVICE_LOCATION);
            sendSMS(loc, sender);
        }else{
            SmsMessage[] pdus = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            SmsMessage sms = pdus[0];

            String body = sms.getMessageBody();
            String sender = sms.getOriginatingAddress();

            String keyword = sharedPref.getString(Constants.SHARED_KEYWORD, context.getResources().getString(R.string.default_keyword));
            Boolean isEnabled = sharedPref.getBoolean(Constants.SHARED_SMS_ENABLED, false);
            if ((!keyword.equals(context.getResources().getString(R.string.default_keyword)))
                    && (body.contains(keyword))
                    && isEnabled){
                Intent i = new Intent(context, LocationService.class);
                i.putExtra(Constants.EXTRA_SMS_SENDER,sender);
                context.startService(i);
            }
        }


    }

    private void sendSMS(DeviceLocation loc, String sender) {
        SmsManager response = SmsManager.getDefault();
        String body =  context.getResources().getString(R.string.text_format, loc.getLat(), loc.getLng(), loc.getLat(), loc.getLng());
        response.sendTextMessage(sender, null, body, null, null);
    }

}
