package com.example.mac.pollcial;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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

                String email = emailEditText.getText().toString();

                // check empty email
                if(TextUtils.isEmpty(email)) {
                    emailEditText.setError(getString(R.string.error_field_required));

                    // display message
                    Context context = getApplicationContext();
                    CharSequence noEmailInfo = "No email provided.";
                    int duration = Toast.LENGTH_SHORT;

                    Toast displayError = Toast.makeText(context, noEmailInfo, duration);
                    displayError.show();

                    emailEditText.requestFocus();
                }
                else {
                    // send email
                    FirebaseAuth auth = FirebaseAuth.getInstance();

                    // try to send password reset email
                    auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    // prepare toast
                                    Context context = getApplicationContext();
                                    int duration = Toast.LENGTH_SHORT;

                                    // success
                                    if (task.isSuccessful()) {
                                        Log.d("sendResetEmail", "Email sent.");
                                        CharSequence successInfo = "An email containing password reset instruction has been sent to your email address.";

                                        Toast displaySuccess = Toast.makeText(context, successInfo, duration);
                                        displaySuccess.show();

                                        // go back to login page
                                        //startActivity(new Intent(ForgetPasswordActivity.this, LoginActivity.class));
                                        finish();
                                    } else {
                                        Log.e("sendResetEmail", "Email not found");

                                        emailEditText.setError("The email could not be found.");
                                        emailEditText.requestFocus();
                                    }
                                }
                            });
                }
            }
        });
    }

}