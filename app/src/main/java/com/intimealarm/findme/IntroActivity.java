package com.intimealarm.findme;

import android.Manifest;
import android.app.DialogFragment;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.intimealarm.findme.Fragments.LoginFragment;
import com.intimealarm.findme.Services.LocationService;
import com.intimealarm.findme.Utils.Constants;
import com.intimealarm.findme.Fragments.Dialog;

/**
 * @author Conor Keenan
 * @reference https://github.com/apl-devs/AppIntro
 * Student No: x13343806
 * Created on 23/03/2017.
 *
 * This Activity extends the AppIntro Activity that is sourced a https://github.com/apl-devs/AppIntro.
 */

public class IntroActivity extends AppIntro2 {

    Fragment locationFragment, loginFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Slide 1 - Welcome
            addSlide(AppIntroFragment.newInstance(
                    getString(R.string.intro_welcome),
                    getString(R.string.intro_slide1),
                    R.drawable.logo,
                    getColor(R.color.slide1)));

        // Slide 2 - Permissions
            askForPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_SMS}, 2);
            addSlide(AppIntroFragment.newInstance(getString(R.string.intro_permissions),
                    getString(R.string.Intro_slide2),
                    R.drawable.lock_icon,
                    getColor(R.color.slide2)));

        // Slide 3 - Location Service
            locationFragment = AppIntroFragment.newInstance(getString(R.string.intro_location),
                    getString(R.string.intro_slide3),
                    R.drawable.location_icon,
                    getColor(R.color.slide3));
            try {
                if (Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE) == Constants.LOCATION_OFF){
                    addSlide(locationFragment);
                }
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }


        // Slide 4 - Login / Register
            loginFragment = LoginFragment.newInstance(getColor(R.color.white),Constants.AUTH_REGISTER);
            addSlide(loginFragment);


        showSkipButton(false);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        startService(new Intent(this, LocationService.class));
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        if (newFragment == locationFragment){
            DialogFragment newFrag = new Dialog();
            Bundle args = new Bundle();
            args.putInt(Constants.BUNDLE_DIALOG_MESSAGE, R.string.turn_on_location);
            args.putBoolean(Constants.BUNDLE_ISLOCATION, true);
            newFrag.setArguments(args);
            newFrag.show(getFragmentManager(), "Dialog");
        }
    }
}
