package com.example.smartlocker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;

public class setCodeActivity extends AppCompatActivity {

    private CountryCodePicker ccp;
    private EditText num;
    private Button sendOTPBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_code);

        num = (EditText) findViewById(R.id.number);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(num);
        sendOTPBtn = (Button) findViewById(R.id.send_otp_btn);

        sendOTPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (num.getText().toString().isEmpty()){
                    num.setError("Required");
                }else {
                    Intent intent = new Intent(setCodeActivity.this, ManageOTPActivity.class);
                    intent.putExtra("number", ccp.getFullNumberWithPlus().replace(" ", ""));
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}