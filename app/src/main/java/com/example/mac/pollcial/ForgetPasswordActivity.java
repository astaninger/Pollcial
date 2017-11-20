package com.example.mac.pollcial;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;


public class ForgetPasswordActivity extends AppCompatActivity {


    private EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        emailEditText = (EditText) findViewById(R.id.txt_email);

        //this block adds a back button to the upper left of screen
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Button btnSubmitResetRequest = (Button) findViewById(R.id.submit_reset_request);
        btnSubmitResetRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // send email
                FirebaseAuth auth = FirebaseAuth.getInstance();

                auth.sendPasswordResetEmail(emailEditText.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("sendResetEmail", "Email sent.");
                                    Context context = getApplicationContext();
                                    CharSequence successInfo = "An email containing password reset instruction has been sent to your email address.";
                                    int duration = Toast.LENGTH_SHORT;

                                    Toast displaySuccess = Toast.makeText(context, successInfo, duration);
                                    displaySuccess.show();

                                    startActivity(new Intent(ForgetPasswordActivity.this, LoginActivity.class));
                                }
                                else {
                                    Log.e("sendResetEmail", "Email not found");
                                    Context context = getApplicationContext();

                                    CharSequence failInfo = "The email could not be found.";
                                    int duration = Toast.LENGTH_SHORT;

                                    Toast displayFail = Toast.makeText(context, failInfo, duration);
                                    displayFail.show();
                                }
                            }
                        });
            }
        });
    }

}