package com.intimealarm.findme.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.annotation.ColorInt;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.paolorotolo.appintro.ISlidePolicy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.intimealarm.findme.R;
import com.intimealarm.findme.Utils.Constants;
import com.intimealarm.findme.Utils.Helper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Conor Keenan
 * Student No: x13343806
 * Created on 23/03/2017.
 */


public class LoginFragment extends Fragment implements ISlidePolicy {

    // Variables
    private FirebaseAuth auth;
    private Helper helper;
    Activity activity;
    Boolean registrationSuccessful;
    int type;

    // Butterknife Bindings
    @BindView(R.id.frag_login_et_email)
    EditText emailEt;

    @BindView(R.id.frag_login_et_password)
    EditText passwordEt;

    @BindView(R.id.frag_login_et_password_confirm)
    EditText confirmPassEt;

    @BindView(R.id.frag_auth_btn)
    Button authBtn;

    @BindView(R.id.auth_title)
    TextView authLbl;

    @BindView(R.id.frag_alt_btn)
    Button altBtn;

    @OnClick(R.id.frag_alt_btn)
    public void changeAuthType(View v){
        switch(type){
            case Constants.AUTH_REGISTER:
                type = Constants.AUTH_LOGIN;
                break;
            case Constants.AUTH_LOGIN:
                type = Constants.AUTH_REGISTER;
                break;
        }

        setViews();
    }

    // Sign Up Button Listener
    @OnClick(R.id.frag_auth_btn)
    public void authenticate(View v){
        hideKeyboard();
        String email = emailEt.getText().toString();
        String password = passwordEt.getText().toString();
        String confirmPass = confirmPassEt.getText().toString();

        if (helper.checkFields(email, password)){
            if (type == Constants.AUTH_REGISTER){
                if (helper.checkFields(confirmPass)){
                    if (password.equals(confirmPass)){
                        signup(email, password);
                    }else{
                        Toast.makeText(activity, R.string.password_not_match,Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(activity, R.string.complete_all_fields,Toast.LENGTH_SHORT).show();
                }
            }else if (type == Constants.AUTH_LOGIN){
                login(email, password);
            }

        }else{
            Toast.makeText(activity, R.string.complete_all_fields,Toast.LENGTH_SHORT).show();
        }


    }

    public LoginFragment() {}

    private void setViews(){
        if (type == Constants.AUTH_REGISTER){
            authBtn.setText(R.string.btn_sign_up);
            authLbl.setText(R.string.register);
            altBtn.setText(R.string.frag_alt_signin);
            confirmPassEt.setVisibility(View.VISIBLE);
        }else if (type == Constants.AUTH_LOGIN){
            authLbl.setText(R.string.signin);
            authBtn.setText(R.string.btn_sign_in);
            altBtn.setText(R.string.frag_alt_register);
            confirmPassEt.setVisibility(View.INVISIBLE);

        }
    }


    private void setViewsSuccess() {
        authBtn.setEnabled(false);
        emailEt.setEnabled(false);
        passwordEt.setEnabled(false);
        authLbl.setText(R.string.success);
        confirmPassEt.setEnabled(false);
        altBtn.setEnabled(false);
    }

    public static Fragment newInstance(@ColorInt int bgColor, int type) {

        Bundle args = new Bundle();
        LoginFragment fragment = new LoginFragment();
        args.putInt(Constants.ARG_BG_COLOR, bgColor);
        args.putInt(Constants.ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        registrationSuccessful = false;
        helper = new Helper();

        auth = FirebaseAuth.getInstance();
        FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(Constants.TAG_LOGIN_FRAG, "onAuthStateChanged:signed_In");
//                    startService(new Intent(LoginActivity.this, LocationService.class));
//                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                    finish();
                }
            }
        };
        auth.addAuthStateListener(authListener);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        view.setBackgroundColor(getArguments().getInt(Constants.ARG_BG_COLOR,R.color.colorAccent));
        type = getArguments().getInt(Constants.ARG_TYPE, Constants.AUTH_REGISTER);
        ButterKnife.bind(this, view);
        setViews();
        return view;
    }


    @Override
    public boolean isPolicyRespected() {
        return registrationSuccessful;
    }

    @Override
    public void onUserIllegallyRequestedNextPage() {
        Toast.makeText(activity, R.string.please_register, Toast.LENGTH_SHORT).show();
    }

    // Method to Log In
    private void login(String email, String password){
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(Constants.TAG_LOGIN_FRAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(Constants.TAG_LOGIN_FRAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(activity, R.string.signin_failed, Toast.LENGTH_SHORT).show();
                        }else{
                            registrationSuccessful = true;
                            setViewsSuccess();
                        }
                    }
                });
    }

    // Method to Register New User
    private void signup(String email, String password){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(Constants.TAG_LOGIN_FRAG, "createUserWithEmail:onComplete:" + task.isSuccessful() );
                        if (!task.isSuccessful()) {
                            Toast.makeText(activity, R.string.signup_failed, Toast.LENGTH_SHORT).show();
                        }else{
                            registrationSuccessful = true;
                            setViewsSuccess();
                        }
                    }
                });
    }


    private void hideKeyboard(){
        View view = this.getView();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view != null ? view.getWindowToken() : null, 0);
    }
}
