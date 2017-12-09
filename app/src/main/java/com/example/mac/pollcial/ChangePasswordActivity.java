package com.example.mac.pollcial;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String email;

    // validation codes
    private final int EMPTY_FIELD = 0;
    private final CharSequence EMPTY_FIELD_STR = "One or more fields is empty.";
    private final int NOT_MATCHING = 1;
    private final CharSequence NOT_MATCHING_STR = "Your second entry does not match the first.";
    private final int TOO_SHORT = 2;
    private final CharSequence TOO_SHORT_STR = "Your new password is too short.";
    private final int NO_ERROR = 3;
    private final CharSequence WRONG_PASSWORD = "Your password is incorrect.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        email = mFirebaseUser.getEmail();

        //this block adds a back button to the upper left of screen
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // change password
        Button changePasswordBtn = (Button) findViewById(R.id.btn_change_password);
        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email == null) {
                    CharSequence guestInfo = "You cannot do this as a guest.";

                    Toast displayGuestInfo = Toast.makeText(getApplicationContext(), guestInfo, Toast.LENGTH_SHORT);
                    displayGuestInfo.show();
                } else {
                    //render a new activity here
                    changePassword();
                }
            }
        });
    }

    private void changePassword() {
        EditText oldPasswordText = (EditText) findViewById(R.id.txt_old_password);
        EditText newPasswordText = (EditText) findViewById(R.id.txt_new_password);
        EditText newPasswordReText = (EditText) findViewById(R.id.txt_new_password_reentered);

        if(validateFields(oldPasswordText, newPasswordText, newPasswordReText) != NO_ERROR) {
                return;
        }

        final String oldPassword = oldPasswordText.getText().toString();
        final String newPassword = newPasswordText.getText().toString();

        // reauthenticate
        AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);
        mFirebaseUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Log.d("Re-authenticate", "User re-authenticated.");

                            // try to update pass
                            mFirebaseUser.updatePassword(newPassword)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("Change password", "User password updated.");
                                                Toast displaySuccess = Toast.makeText(getApplicationContext(), "Password successfully updated!", Toast.LENGTH_SHORT);
                                                displaySuccess.show();

                                                // go back to previous page
                                                finish();
                                            }
                                            else {
                                                Log.e("Change password", "User password change failed.");
                                                Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT);
                                            }
                                        }
                                    });
                        }
                        else {
                            Log.e("Re-authenticate", "User could not be re-authenticated.");
                            EditText oldPasswordText = (EditText) findViewById(R.id.txt_old_password);
                            oldPasswordText.setError(WRONG_PASSWORD);
                            oldPasswordText.requestFocus();
                        }
                    }
                });


    }

    // check if password fields are valid
    // 0 indicates missing field, 1 indicates new password is not matching, 2 indicates no problem
    private int validateFields(EditText oldPasswordText, EditText newPasswordText, EditText newPasswordReText) {
        String oldPassword = oldPasswordText.getText().toString();
        String newPassword = newPasswordText.getText().toString();
        String newPasswordRe = newPasswordReText.getText().toString();

        // check empty
        boolean empty = false;
        if(TextUtils.isEmpty(newPasswordRe)) {
            newPasswordReText.setError(getString(R.string.error_field_required));
            newPasswordReText.requestFocus();
            empty = true;
        }
        if(TextUtils.isEmpty(newPassword)) {
            newPasswordText.setError(getString(R.string.error_field_required));
            newPasswordText.requestFocus();

            empty = true;
        }
        if(TextUtils.isEmpty(oldPassword)) {
            oldPasswordText.setError(getString(R.string.error_field_required));
            oldPasswordText.requestFocus();

            empty = true;
        }
        if(empty) {
            return EMPTY_FIELD;
        }

        if(!isPasswordValid(newPassword)) {
            newPasswordText.setError(TOO_SHORT_STR);
            newPasswordText.requestFocus();

            return TOO_SHORT;
        }
        if(!newPassword.equals(newPasswordRe)) {
            newPasswordReText.setError(NOT_MATCHING_STR);
            newPasswordReText.requestFocus();

            return NOT_MATCHING;
        }

        return NO_ERROR;
    }

    private boolean isPasswordValid(String password) {
        if (password.length() < 8) {
            return false;
        }
        return true;
    }

    //this block clear focus when touch somewhere else
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

}
