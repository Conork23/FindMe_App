package com.intimealarm.findme.Fragments;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import com.intimealarm.findme.R;
import com.intimealarm.findme.SettingsActivity;
import com.intimealarm.findme.Utils.Constants;

/**
 * @author Conor Keenan
 * Student No: x13343806
 * Created on 04/03/2017.
 */

public class Dialog extends DialogFragment {

    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {

        int message = getArguments().getInt(Constants.BUNDLE_DIALOG_MESSAGE,R.string.dialogTxt );
        int positiveBtn = R.string.dialogPositiveBtn;
        final boolean isLocation = getArguments().getBoolean(Constants.BUNDLE_ISLOCATION, false);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setPositiveButton(positiveBtn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (isLocation){
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }else{
                            startActivity(new Intent(getActivity(), SettingsActivity.class));
                        }
                    }
                })
//                .setNegativeButton(R.string.DialogCancelBtn, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        getActivity().finish();
//                    }
//                })
        ;

        return builder.create();
    }
}
