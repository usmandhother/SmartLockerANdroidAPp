package com.example.smartlocker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class ManageOTPActivity extends AppCompatActivity {

    private EditText otp, pin, pinConform;
    private Button verifyOTPBtn, setPinBtn;
    private TextView textView;
    private String phoneNumber, otpId;
    private FirebaseAuth mAuth;
    private sqlHelper helper;
    private ProgressDialog progressDialog;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mcallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_otpactivity);

        phoneNumber = getIntent().getStringExtra("number");
        otp = (EditText) findViewById(R.id.otp_txt);
        pin = (EditText) findViewById(R.id.pin_txt);
        textView = (TextView) findViewById(R.id.txt_View);
        pinConform = (EditText) findViewById(R.id.pinConform_txt);
        verifyOTPBtn = (Button) findViewById(R.id.verify_otp_btn);
        setPinBtn = (Button) findViewById(R.id.setPin_btn);
        mAuth = FirebaseAuth.getInstance();
        helper = new sqlHelper(this);
        progressDialog = new ProgressDialog(this);

        otp.setVisibility(View.VISIBLE);
        verifyOTPBtn.setVisibility(View.VISIBLE);
        textView.setVisibility(View.INVISIBLE);
        pin.setVisibility(View.INVISIBLE);
        pinConform.setVisibility(View.INVISIBLE);
        setPinBtn.setVisibility(View.INVISIBLE);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        initiateOTP();
        verifyOTPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (otp.getText().toString().isEmpty()) {
                    otp.setError("Required");
                } else if (otp.getText().toString().length() != 6) {
                    otp.setError("Invalid OTP");
                } else {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otpId, otp.getText().toString());
                    signIn(credential);
                }
            }
        });

        setPinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pin.getText().toString().isEmpty()) {
                    pin.setError("Required");
                } else if (pinConform.getText().toString().isEmpty()) {
                    pinConform.setError("Required");
                } else {
                    String p = pin.getText().toString();
                    String p2 = pinConform.getText().toString();
                    if (p.equals(p2)) {
                        boolean check = helper.insertPin(p, phoneNumber);
                        if (check) {
                            progressDialog.setMessage("Please wait...");
                            progressDialog.show();
                            Toast.makeText(ManageOTPActivity.this, "Pin set", Toast.LENGTH_LONG).show();
                            getPreferences.preferences = getSharedPreferences("codeRegistered", MODE_PRIVATE);
                            getPreferences.preferences.edit().putString("register", "true").apply();
                            Intent intent = new Intent(ManageOTPActivity.this, MainActivity.class);
                            startActivity(intent);
                            progressDialog.dismiss();
                            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                            finish();
                        } else {
                            Toast.makeText(ManageOTPActivity.this, "Failed", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(ManageOTPActivity.this, "Pin not Matched", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void initiateOTP() {
        mcallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                signIn(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                Toast.makeText(ManageOTPActivity.this, "Verification Failed" + e.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                otpId = s;
                if (progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }
        };


        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mcallback)         // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signIn(PhoneAuthCredential credential) {
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    if (progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    Toast.makeText(ManageOTPActivity.this, "Number Verified", Toast.LENGTH_LONG).show();
                    otp.setVisibility(View.INVISIBLE);
                    verifyOTPBtn.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.VISIBLE);
                    pin.setVisibility(View.VISIBLE);
                    pinConform.setVisibility(View.VISIBLE);
                    setPinBtn.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}