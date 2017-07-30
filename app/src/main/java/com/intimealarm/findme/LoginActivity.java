package com.intimealarm.findme;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.intimealarm.findme.Fragments.LoginFragment;
import com.intimealarm.findme.Services.LocationService;
import com.intimealarm.findme.Utils.Constants;

import butterknife.ButterKnife;

/**
 * @author Conor Keenan
 * Student No: x13343806
 * Created on 20/02/2017.
 */

public class LoginActivity extends AppCompatActivity {

    // Constants
    final static private String TAG = "Login_Activity";

    // Variables
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;


    // On Create Method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        // Authenticate User Listener
        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_In");
                    startService(new Intent(LoginActivity.this, LocationService.class));
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
            }
        };

        Fragment loginFrag = new LoginFragment().newInstance(getColor(R.color.white),Constants.AUTH_LOGIN);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.activity_login, loginFrag).commit();

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
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

}
