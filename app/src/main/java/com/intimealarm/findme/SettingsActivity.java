package com.intimealarm.findme;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.intimealarm.findme.Utils.AlarmController;
import com.intimealarm.findme.Utils.Constants;
import com.intimealarm.findme.Utils.Helper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Conor Keenan
 * Student No: x13343806
 * Created on 03/03/2017.
 */

public class SettingsActivity extends AppCompatActivity {

    ArrayAdapter<CharSequence> adapter;
    AlarmController aController;
    SharedPreferences sharedPref;
    Helper help;

    @BindView(R.id.location_interval_spinner)
    Spinner intervalSpinner;

    @BindView(R.id.location_keyword)
    TextView keyword;

    @BindView(R.id.location_keyword_lbl)
    TextView keywordLbl;

    @BindView(R.id.enableSMSToggle)
    ToggleButton enableToggle;

    @BindView(R.id.btn_change_keyword)
    Button changeBtn;

    @OnClick(R.id.btn_change_keyword)
    public void OnChangeClick(View v){
        String currentKeyword = keyword.getText().toString();

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_input, null);
        final EditText input = (EditText) dialogView.findViewById(R.id.keyword_change_input);
        input.setText(currentKeyword);
        input.setSelection(currentKeyword.length());


        new AlertDialog.Builder(this)
                .setTitle(R.string.change_keyword_dialog_title)
                .setView(dialogView)
                .setMessage(R.string.change_keyword_dialog_message)
                .setPositiveButton(R.string.change_keyword_dialog_positive_btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newKeyword = input.getText().toString();
                        if(help.checkFields(newKeyword)){
                            setKeyword(newKeyword);
                        }else{
                            Toast.makeText(SettingsActivity.this, R.string.complete_all_fields,Toast.LENGTH_SHORT).show();

                        }
                    }
                })
                .setNegativeButton(R.string.change_keyword_dialog_negative_btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();

    }

    private void setKeyword(String s) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.SHARED_KEYWORD, s);
        editor.apply();

        keyword.setText(s);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        aController = new AlarmController(this);
        help = new Helper();

        adapter = ArrayAdapter.createFromResource(this,R.array.interval_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        intervalSpinner.setAdapter(adapter);

        sharedPref = getSharedPreferences(Constants.PREFS_FILE, MODE_PRIVATE);
        int position = sharedPref.getInt(Constants.SHARED_INTERVAL, 0);
        intervalSpinner.setSelection(position);

        intervalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sharedPref.edit()
                        .putInt(Constants.SHARED_INTERVAL, i)
                        .apply();

                aController.setLocationAlarm();
                setIsConfigured();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Boolean isEnabled = sharedPref.getBoolean(Constants.SHARED_SMS_ENABLED, false);
        enableToggle.setChecked(isEnabled);
        setSMSEnable(isEnabled);
        enableToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setSMSEnable(b);
                setIsConfigured();
            }

        });

        keyword.setText(sharedPref.getString(Constants.SHARED_KEYWORD, getResources().getString(R.string.default_keyword)));


    }

    // Set if receiving SMS is enabled
    public void setSMSEnable(Boolean b){
        sharedPref.edit()
                .putBoolean(Constants.SHARED_SMS_ENABLED, b)
                .apply();

        if (b){
            changeBtn.setEnabled(true);
            keyword.setTextColor(getColor(R.color.black));
            keywordLbl.setTextColor(getColor(R.color.settings_text));
        }else{
            changeBtn.setEnabled(false);
            keyword.setTextColor(getColor(R.color.disabled));
            keywordLbl.setTextColor(getColor(R.color.disabled));
        }
    }

    private void setIsConfigured(){
        sharedPref.edit()
                .putBoolean(Constants.SHARED_CONFIG, true)
                .apply();
    }

}
