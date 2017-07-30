package com.intimealarm.findme;


import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.intimealarm.findme.Adapters.LocationAdapter;
import com.intimealarm.findme.Models.DeviceLocation;
import com.intimealarm.findme.Services.LocationService;
import com.intimealarm.findme.Utils.AlarmController;
import com.intimealarm.findme.Utils.Constants;
import com.intimealarm.findme.Fragments.Dialog;
import com.intimealarm.findme.Utils.Helper;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Conor Keenan
 * @reference https://github.com/apl-devs/AppIntro
 * Student No: x13343806
 * Created on 20/02/2017.
 */

public class MainActivity extends AppCompatActivity{

    // Constants
    final static private int PERMISSION_LOCATION = 0;

    // Variables
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseUser user;
    private static SupportMapFragment mapFragment;
    DatabaseReference dB;
    LocationAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    Helper help;
    SharedPreferences sharedPref;

    // Butterknife Bindings
    @BindView(R.id.locationList)
    RecyclerView locList;

    // On Create Method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = getSharedPreferences(Constants.PREFS_FILE, MODE_PRIVATE);
        showIntro();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        help = new Helper();


//         Check if Location service is active
        boolean isDisabled = true;
        try {
            isDisabled = (Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE) == Constants.LOCATION_OFF);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if(isDisabled){
            DialogFragment newFragment = new Dialog();
            Bundle args = new Bundle();
            args.putInt(Constants.BUNDLE_DIALOG_MESSAGE, R.string.turn_on_location);
            args.putBoolean(Constants.BUNDLE_ISLOCATION, true);
            newFragment.setArguments(args);
            newFragment.show(getFragmentManager(), "Dialog");
        }


        // check Permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_SMS},
                    PERMISSION_LOCATION);

        }


        // Authenticate User
        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Log.d(Constants.TAG_MAIN_ACTIVITY, "onAuthStateChanged:signed_out");
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Log.d(Constants.TAG_MAIN_ACTIVITY, "onAuthStateChanged: ");
                    mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
                    dB = FirebaseDatabase.getInstance().getReference("FindMe/users/" + user.getUid() + "/locations");
                    layoutManager = new LinearLayoutManager(MainActivity.this);
                    adapter = new LocationAdapter(MainActivity.this, dB);
                    locList.setLayoutManager(layoutManager);
                    locList.setAdapter(adapter);

                    adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                        @Override
                        public void onChanged() {
                            super.onChanged();
                            DeviceLocation loc = adapter.getLatest();
                            if (loc != null) {
                                updateMap(adapter.getLatest());
                            }
                        }
                    });

                    // Check if location interval has been set
                    boolean isConfigured = sharedPref.getBoolean(Constants.SHARED_CONFIG, false);
                    if (!isConfigured) {
                        DialogFragment newFragment = new Dialog();
                        Bundle args = new Bundle();
                        args.putInt(Constants.BUNDLE_DIALOG_MESSAGE, R.string.dialogTxt);
                        args.putBoolean(Constants.BUNDLE_ISLOCATION, false);
                        newFragment.setArguments(args);
                        newFragment.show(getFragmentManager(), "Dialog");
                    }

                }
            }
        };
    }


    // Update Google Map Fragment
    public static void updateMap(final DeviceLocation loc){
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.clear();
                LatLng latLng = new LatLng(loc.getLat(), loc.getLng());
                googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Current Location")
                );

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                        .target(latLng)
                        .zoom(16)
                        .build()));
            }
        });

    }

    // On Start Method
    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    // On Stop Method
    @Override
    protected void onStop() {
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
        super.onStop();
    }

    // Option Menu Inflation and Click Handling
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                logout();
                break;
            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
//            case R.id.test_btn:
//                startService(new Intent(this, LocationService.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        auth.signOut();
        AlarmController aController = new AlarmController(this);
        aController.disableAlarm();
        help.disableSMS(this);
        sharedPref.edit()
                .putBoolean(Constants.SHARED_CONFIG, false)
                .putInt(Constants.SHARED_INTERVAL, 0)
                .putString(Constants.SHARED_KEYWORD, getResources().getString(R.string.default_keyword))
                .apply();
    }

    // Permission Request Result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(Constants.TAG_MAIN_ACTIVITY, "onRequestPermissionsResult: ");
        if (requestCode == PERMISSION_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startService(new Intent(MainActivity.this, LocationService.class));
            }
            else{
                // Permission request was denied.
                Toast.makeText(this, R.string.location_permission_denied,
                        Toast.LENGTH_SHORT)
                        .show();
                finish();
            }
        }
    }

/**
  * Show IntroActivity on first run
  * @reference https://github.com/apl-devs/AppIntro
  */
    private void showIntro() {
        boolean isFirstStart = sharedPref.getBoolean(Constants.SHARED_ISFIRST, true);
        Log.d("test", "showIntro: "+isFirstStart);
        if (isFirstStart) {
            startActivity(new Intent(MainActivity.this, IntroActivity.class));
            sharedPref.edit()
                    .putBoolean(Constants.SHARED_ISFIRST, false)
                    .apply();
            finish();
        }
    }

}


