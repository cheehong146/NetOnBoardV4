package com.example.netonboard.netonboardv4.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.netonboard.netonboardv4.R;

public class PasscodeActivity extends AppCompatActivity {

    public static boolean is_login = false;
    TextView tv_passcodeInfo;
    EditText tf_passcode;
    TextView tv_passcodeAttempt;
    Button btn_confirm;
    boolean is_firstTimeUser = true;
    String str_confirmPassword;
    String userType;
    int passcodeCase = 0;
    public static SharedPreferences sharedPreferences;
    public static String PREF_NAME = "user_passcode";
    public static String PREF_USERTYPE = "userType";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode);

        tv_passcodeInfo = (TextView) findViewById(R.id.tv_passcodeInfo);
        tv_passcodeAttempt = (TextView) findViewById(R.id.tv_passcodeAttempt);
        tf_passcode = (EditText) findViewById(R.id.tf_passcode);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        getUserLoginSetting();

        if (is_firstTimeUser) {
            tv_passcodeInfo.setText("Register your passcode");
            tv_passcodeAttempt.setText("");
        } else {
            tv_passcodeInfo.setText("Enter your passcode");
            tv_passcodeAttempt.setText("");
        }

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (passcodeCase) {
                    case 0:
                        if (tf_passcode.getText().toString().length() >= 4) {
                            str_confirmPassword = tf_passcode.getText().toString();
                            tf_passcode.setText("");
                            tv_passcodeInfo.setTextColor(Color.BLACK);
                            tv_passcodeInfo.setText("Confirm your passcode");
                            tv_passcodeAttempt.setText("");
                            passcodeCase = 1;
                        } else {
                            tv_passcodeInfo.setText("Passcode must be at least 4 in length");
                            tv_passcodeInfo.setTextColor(Color.RED);
                        }
                        break;
                    case 1:
                        if (str_confirmPassword.equals(tf_passcode.getText().toString())) {
                            tv_passcodeInfo.setText("Passcode registered");
                            tv_passcodeInfo.setTextColor(Color.GREEN);
                            passcodeCase = 2;
                            is_firstTimeUser = false;
                            registerCode();
                            verifyPasscode();
                        } else {
                            tf_passcode.setText("");
                            tv_passcodeAttempt.setText("Passcode doesn't match. Register your passcode again");
                            tv_passcodeInfo.setTextColor(Color.RED);
                            passcodeCase = 0;
                            tv_passcodeInfo.setText("Register your passcode");
                            tv_passcodeInfo.setTextColor(Color.BLACK);
                        }
                        break;
                    case 2:
                        verifyPasscode();
                        break;
                    default:
                }
            }
        });
    }

    public void getUserLoginSetting() {
        is_firstTimeUser = sharedPreferences.getBoolean("userFirstTime", true);
        passcodeCase = sharedPreferences.getInt("passcodeCase", 0);
        userType = LoginActivity.sp_name.getString("userType", null);
    }

    public void verifyPasscode() {
        int passcode = Integer.parseInt(tf_passcode.getText().toString());
        int storedPasscode = sharedPreferences.getInt("userLoginPasscode", -1);
        Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();

        if (passcode == storedPasscode) {
            switch (userType) {
                case "support":
                    Intent supportIntent = new Intent(this, NavActivity.class);
                    finish();
                    startActivity(supportIntent);
                    break;
                case "normal":
                    Intent normalIntent = new Intent(this, NavActivity.class);
                    finish();
                    startActivity(normalIntent);
                    break;
                default:
            }
        } else if (passcode == -1) {
        } else {
            tv_passcodeAttempt.setText("Invalid Credential");
            tv_passcodeAttempt.setTextColor(Color.RED);
            tf_passcode.setText("");
        }

    }

    public void registerCode() {
        int passcode = Integer.parseInt(tf_passcode.getText().toString());
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt("userLoginPasscode", passcode);
        edit.putInt("passcodeCase", passcodeCase);
        edit.putBoolean("userFirstTime", false);
        edit.commit();
        getUserLoginSetting();

    }
}