package com.example.smartlocker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;

public class lockScreenActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9;
    private ImageView dot_1, dot_2, dot_3, dot_4, btn_clear;
    private TextView forgotCode;
    private ImageView[] dots;
    private sqlHelper helper;


    private static String TRUE_CODE = "";
    private static final int MAX_LENGTH = 4;
    private String codeString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);
        btn0 = (Button) findViewById(R.id.btn0);btn0.setOnClickListener(this);
        btn1 = (Button) findViewById(R.id.btn1);btn1.setOnClickListener(this);
        btn2 = (Button) findViewById(R.id.btn2);btn2.setOnClickListener(this);
        btn3 = (Button) findViewById(R.id.btn3);btn3.setOnClickListener(this);
        btn4 = (Button) findViewById(R.id.btn4);btn4.setOnClickListener(this);
        btn5 = (Button) findViewById(R.id.btn5);btn5.setOnClickListener(this);
        btn6 = (Button) findViewById(R.id.btn6);btn6.setOnClickListener(this);
        btn7 = (Button) findViewById(R.id.btn7);btn7.setOnClickListener(this);
        btn8 = (Button) findViewById(R.id.btn8);btn8.setOnClickListener(this);
        btn9 = (Button) findViewById(R.id.btn9);btn9.setOnClickListener(this);
        btn_clear = (ImageView) findViewById(R.id.btn_clear);btn_clear.setOnClickListener(this);
        dot_1 = (ImageView) findViewById(R.id.dot_1);
        dot_2 = (ImageView) findViewById(R.id.dot_2);
        dot_3 = (ImageView) findViewById(R.id.dot_3);
        dot_4 = (ImageView) findViewById(R.id.dot_4);
        forgotCode = (TextView) findViewById(R.id.forgot_password);forgotCode.setOnClickListener(this);
       dots = new ImageView[]{dot_1, dot_2, dot_3, dot_4, btn_clear};
       helper= new sqlHelper(this);
       TRUE_CODE=helper.getPin();


        getPreferences.preferences = getSharedPreferences("codeRegistered",MODE_PRIVATE);
        String preferanceValue=getPreferences.preferences.getString("register","no");
        if (preferanceValue.contains("no"))
        {
            Intent intent = new Intent(this,setCodeActivity.class);
            startActivity(intent);
            finish();
        }

    }


    @Override
    public void onClick(View v) {
        getStringCode(v.getId());
        if (codeString.length() == MAX_LENGTH) {
            if (codeString.equals(TRUE_CODE)) {
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Wrong Pass code", Toast.LENGTH_SHORT).show();
            }
        } else if (codeString.length() > MAX_LENGTH){
            //reset the input code
            codeString = "";
            getStringCode(v.getId());
        }
        setDotImagesState();
    }


    private void getStringCode(int buttonId) {
        switch (buttonId) {
            case R.id.btn0:
                codeString += "0";
                break;
            case R.id.btn1:
                codeString += "1";
                break;
            case R.id.btn2:
                codeString += "2";
                break;
            case R.id.btn3:
                codeString += "3";
                break;
            case R.id.btn4:
                codeString += "4";
                break;
            case R.id.btn5:
                codeString += "5";
                break;
            case R.id.btn6:
                codeString += "6";
                break;
            case R.id.btn7:
                codeString += "7";
                break;
            case R.id.btn8:
                codeString += "8";
                break;
            case R.id.btn9:
                codeString += "9";
                break;
            case R.id.btn_clear:
                if (codeString.length()>0){
                    codeString = removeLastChar(codeString);
                    setDotImagesState();
                }
                break;
            case R.id.forgot_password:
                forgetPassword();
                break;
            default:
                break;
        }
    }

    private void forgetPassword()
    {
        View view = getLayoutInflater().inflate(R.layout.phone_no_picker,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Your Phone No");
        CountryCodePicker ccp = view.findViewById(R.id.ccp_forget);
        EditText phoneNoInput = view.findViewById(R.id.number_forget);
        phoneNoInput.setInputType(InputType.TYPE_CLASS_PHONE);
        //phoneNoInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        builder.setView(view);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ccp.registerCarrierNumberEditText(phoneNoInput);
                String a = ccp.getFullNumberWithPlus();
                checkPhoneNo(a);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void checkPhoneNo(String no)
    {
        if (no.equals(helper.getPhoneNo(no)))
        {
            Intent intent = new Intent(getApplicationContext(),ManageOTPActivity.class);
            intent.putExtra("number",no);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(getApplicationContext(), "Phone No Not Found", Toast.LENGTH_LONG).show();
        }
    }

    private String removeLastChar(String s){
        if (s == null || s.length() == 0) {
            return s;
        }
        return s.substring(0,s.length()-1);
    }

    private void setDotImagesState() {
        for (int i = 0; i < codeString.length(); i++) {
            dots[i].setImageResource(R.drawable.ic_baseline_brightness_1_24);
        }
        if (codeString.length()<4) {
            for (int j = codeString.length(); j<4; j++) {
                dots[j].setImageResource(R.drawable.dot_disable);
            }
        }
    }



}