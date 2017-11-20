package com.example.mac.pollcial;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

public class NewPostActivity extends AppCompatActivity {

    private SinglePoll poll;
    private Button btnCancel;

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


        //TODO: switch
        //btnCancel = (Button)findViewById(R.id.cancelButton);
        /*btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewPostActivity.this, DiscoverActivity.class));
            }
        });*/
    }

    private boolean publishPoll() {
        DatabaseReference mPollReference = mFirebaseDatabaseReference.child("polls").push();
        mPollReference.setValue(poll);
        // String pollId = mPollReference.getKey();
        // DatabaseReference mUserReference = mFirebaseDatabaseReference.child("users").child(user.getUid());
        // mUserReference.child("messages").child(pollId).setValue("true");

        Context context = getApplicationContext();
        CharSequence text = "POLL PUBLISHED!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_post, menu);
        MenuItem submitPoll = menu.findItem(R.id.action_post_poll);
        submitPoll.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                //The fields for making a new poll
                EditText pollTitle = (EditText)findViewById(R.id.newPostTitle);
                EditText pollDescription = (EditText)findViewById(R.id.newPostContent);
                EditText pollChoiceA = (EditText)findViewById(R.id.choiceA);
                EditText pollChoiceB = (EditText)findViewById(R.id.choiceB);
                EditText pollChoiceC = (EditText)findViewById(R.id.choiceC);
                EditText pollChoiceD = (EditText)findViewById(R.id.choiceD);
                CheckBox anon = (CheckBox)findViewById(R.id.cb_post_as_anonymous);


                //poll object. Not sure how to get username, useremail, time, and pollID.
                // TODO: add Firebase authentication to this file so we can get user
                /* user = FirebaseAuth.getInstance().getCurrentUser();
                 * String uid = user.getUid();
                 * String username = user.geDisplayName();
                 * String email = user.getEmail();
                 */
                // TODO: change SinglePoll - remove pollId field, add Uid field
                //need to pass poll object to firebase.
                poll = new SinglePoll(0, pollTitle.getText().toString(), pollDescription.getText().toString()
                        , pollChoiceA.getText().toString(), pollChoiceB.getText().toString(), pollChoiceC.getText().toString(),
                        pollChoiceD.getText().toString(), "FAKETIME", "FAKEUSERNAME", "FAKEEMAIL", anon.isChecked());



                publishPoll();

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
