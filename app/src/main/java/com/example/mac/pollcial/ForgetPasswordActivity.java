package com.example.mac.pollcial;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;




public class ForgetPasswordActivity extends AppCompatActivity {


    private EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        emailEditText = (EditText) findViewById(R.id.txt_email);

        Button btnSubmitResetRequest = (Button) findViewById(R.id.submit_reset_request);
        btnSubmitResetRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean accountExist = checkAccountExist(emailEditText.getText().toString());

                if(accountExist) {
                    Context context = getApplicationContext();
                    CharSequence successInfo = "An email containing password reset instruction has been send to your email address";
                    int duration = Toast.LENGTH_SHORT;

                    Toast displaySuccess = Toast.makeText(context, successInfo, duration);
                    displaySuccess.show();

                    startActivity(new Intent(ForgetPasswordActivity.this, LoginActivity.class));
                }
                else {
                    Context context = getApplicationContext();
                    CharSequence failInfo = "Account not exist, please verify again";
                    int duration = Toast.LENGTH_SHORT;

                    Toast displaySuccess = Toast.makeText(context, failInfo, duration);
                    displaySuccess.show();
                }
            }
        });
    }

    // TODO
    /*
     * This method should be implemented by controller team. It will take a string as its only
     * parameter, which represents the user entered email. It should verify with database to see
     * if the userEmail has been registered as an account already. Return true if account exist,
     * otherwise, return false.
     */
    private boolean checkAccountExist(String userEmail) {
        if (userEmail.equals("real")) {
            return true;
        }
        else {
            return false;
        }
    }

}