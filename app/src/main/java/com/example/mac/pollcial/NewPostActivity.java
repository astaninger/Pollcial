package com.example.mac.pollcial;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Date;

public class NewPostActivity extends AppCompatActivity {

    private SinglePoll poll;
    private Button btnCancel;

    private EditText pollTitle;
    private EditText pollDescription;
    private EditText pollChoiceA;
    private EditText pollChoiceB;
    private EditText pollChoiceC;
    private EditText pollChoiceD;
    private CheckBox anon;

    private String uid;
    private String username;
    private String email;
    private String postTime;
    private String title;
    private String description;
    private String choiceA;
    private String choiceB;
    private String choiceC;
    private String choiceD;
    private int numVote;
    private int numVoteA;
    private int numVoteB;
    private int numVoteC;
    private int numVoteD;

    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.newPostToolBar);
        setSupportActionBar(toolbar);

        //this block adds a back button to the upper left of screen
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Initialize Firebase Database
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

    }

    private boolean validatePollInfo() {
        boolean valid = true;
        if(TextUtils.isEmpty(title)) {
            pollTitle.setError(getString(R.string.error_field_required));
            valid = false;
        }
        if(TextUtils.isEmpty(description)) {
            pollDescription.setError(getString(R.string.error_field_required));
            valid = false;
        }
        if(TextUtils.isEmpty(choiceA)) {
            pollChoiceA.setError(getString(R.string.error_field_required));
            valid = false;
        }
        if(TextUtils.isEmpty(choiceB)) {
            pollChoiceB.setError(getString(R.string.error_field_required));
            valid = false;
        }
        return valid;
    }

    private boolean publishPoll() {
        DatabaseReference mPollReference = mFirebaseDatabaseReference.child("polls").push();
        mPollReference.setValue(poll);
        String pollId = mPollReference.getKey();
        DatabaseReference mUserReference = mFirebaseDatabaseReference.child("users").child(user.getUid());
        mUserReference.child("polls").child(pollId).setValue("true");

        Context context = getApplicationContext();
        CharSequence text = "POLL PUBLISHED!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        finish();

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu_search; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_post, menu);
        MenuItem submitPoll = menu.findItem(R.id.action_post_poll);
        submitPoll.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                //The fields for making a new poll
                pollTitle = (EditText)findViewById(R.id.newPostTitle);
                pollDescription = (EditText)findViewById(R.id.newPostContent);
                pollChoiceA = (EditText)findViewById(R.id.choiceA);
                pollChoiceB = (EditText)findViewById(R.id.choiceB);
                pollChoiceC = (EditText)findViewById(R.id.choiceC);
                pollChoiceD = (EditText)findViewById(R.id.choiceD);
                anon = (CheckBox)findViewById(R.id.cb_post_as_anonymous);


                //poll object. Not sure how to get username, useremail, time, and pollID.
                user = FirebaseAuth.getInstance().getCurrentUser();
                uid = user.getUid();
                username = (user.getDisplayName() == null) ? "Guest" : user.getDisplayName();
                email = user.getEmail();
                postTime = DateFormat.getDateTimeInstance().format(new Date());
                title = pollTitle.getText().toString();
                description = pollDescription.getText().toString();
                choiceA = pollChoiceA.getText().toString();
                choiceB = pollChoiceB.getText().toString();

                choiceC = pollChoiceC.getText().toString();
                choiceD = pollChoiceD.getText().toString();
                // check if c is empty
                if(choiceC.isEmpty() && !choiceD.isEmpty()) {
                        choiceC = choiceD;
                        choiceD = "";
                }
                numVote = 0;
                numVoteA = 0;
                numVoteB = 0;
                numVoteC = 0;
                numVoteD = 0;

                if(validatePollInfo()) {
                    //need to pass poll object to firebase.
                    poll = new SinglePoll(title, description, choiceA, choiceB, choiceC,
                            choiceD, postTime, uid, username, email, anon.isChecked(), numVote,
                            numVoteA, numVoteB, numVoteC, numVoteD);
                    publishPoll();
                }

                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_searching) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}