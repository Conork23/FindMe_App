package com.intimealarm.findme.Services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.intimealarm.findme.BroadcastRecievers.SmsReceiver;
import com.intimealarm.findme.Models.DeviceLocation;
import com.intimealarm.findme.Utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Conor Keenan
 * Student No: x13343806
 * Created on 20/02/2017.
 */

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // Variables
    GoogleApiClient googleApiClient;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference dB;
    Location lastLocation;
    ResultReceiver resultReceiver;
    String sender;

    // On Create Method
    @Override
    public void onCreate() {
        super.onCreate();
        dB = FirebaseDatabase.getInstance().getReference("FindMe");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        sender = intent.getStringExtra(Constants.EXTRA_SMS_SENDER);
        resultReceiver = intent.getParcelableExtra(Constants.RESULT_RECEIVER);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Log.d(Constants.TAG_L_SERVICE, "onStartCommand:signed_out");
            onDestroy();
        } else {
            user = auth.getCurrentUser();
            Log.d(Constants.TAG_L_SERVICE, "onStartCommand: " + user.getEmail());
            googleApiClient = new GoogleApiClient.Builder(LocationService.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(LocationService.this)
                    .addOnConnectionFailedListener(LocationService.this)
                    .build();
            googleApiClient.connect();

        }

        return START_REDELIVER_INTENT;
    }


    // On destroy Method
    @Override
    public void onDestroy() {
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
        super.onDestroy();
    }


    // Google Client Connected Method.
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(Constants.TAG_L_SERVICE, "onConnected: ");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Getting Location
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            // If Location isn't Null Add it to Firebase DB
            if (lastLocation != null) {
                getAddress(lastLocation);
            }else{
                Log.d(Constants.TAG_L_SERVICE, "location: null ");
            }
        }

    }

    // Convert LatLng to address string
    private void getAddress(Location lastLocation) {
        List<Address> addresses = null;
        int error = Constants.SUCCESS_RESULT;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses=geocoder.getFromLocation(lastLocation.getLatitude(),lastLocation.getLongitude(),1);
        } catch (IOException e ) {
            error = Constants.FAILURE_RESULT;
            e.printStackTrace();
        }

        if (addresses == null || addresses.size() == 0) {
            if (error == Constants.FAILURE_RESULT) {
                addToFirebase(lastLocation, "");
            }
        }else{
            Address add = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<>();

            for(int i = 0; i < add.getMaxAddressLineIndex(); i++) {
                addressFragments.add(add.getAddressLine(i));
                Log.d(Constants.TAG_A_SERVICE, "onHandleIntent: " + add.getAddressLine(i));
            }

            addToFirebase(lastLocation,
                    TextUtils.join(", ", addressFragments));

        }
    }

    // Google Client Connection Suspended Method
    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();

    }

    // Connection to Google Client Failure Method
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(Constants.TAG_L_SERVICE, "onConnectionFailed: " + connectionResult.getErrorMessage());
    }


    private void addToFirebase(Location loc, String lbl) {
        DeviceLocation dLoc = new DeviceLocation(loc.getLatitude(), loc.getLongitude(), loc.getTime(), lbl);

        String key = dB.child("users").child(user.getUid()).child("locations").push().getKey();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + user.getUid() + "/locations/" + key, dLoc);

        dB.updateChildren(childUpdates);

        if (sender != null){
            Intent i = new Intent(this, SmsReceiver.class);
            i.putExtra(Constants.EXTRA_SEND_SMS, true);
            i.putExtra(Constants.EXTRA_SMS_SENDER, sender);
            i.putExtra(Constants.EXTRA_DEVICE_LOCATION, dLoc);
            sendBroadcast(i);
        }

        stopSelf();
    }

}
