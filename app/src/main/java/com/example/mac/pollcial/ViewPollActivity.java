package com.example.mac.pollcial;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ClipboardManager;
import android.content.ClipData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewPollActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private DatabaseReference mPollReference;
    private DatabaseReference mUserReference;
    private DatabaseReference mVoteReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_poll);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        Intent trigerIntent = getIntent(); // get the last Intent that triger this current Intent
        // get all needed information about poll from trigerIntent
        String currTitle = trigerIntent.getStringExtra("currTitle");
        String currPostTimeAndAuthor = trigerIntent.getStringExtra("currPostTimeAndAuthor");
        final String currNumVotes = trigerIntent.getStringExtra("currNumVotes");
        String currDescription = trigerIntent.getStringExtra("currDescription");
        final String currChoiceA = trigerIntent.getStringExtra("currChoiceA");
        final String currChoiceB = trigerIntent.getStringExtra("currChoiceB");
        final String currChoiceC = trigerIntent.getStringExtra("currChoiceC");
        final String currChoiceD = trigerIntent.getStringExtra("currChoiceD");
        final String currNumVoteA = trigerIntent.getStringExtra("currNumVoteA");
        final String currNumVoteB = trigerIntent.getStringExtra("currNumVoteB");
        final String currNumVoteC = trigerIntent.getStringExtra("currNumVoteC");
        final String currNumVoteD = trigerIntent.getStringExtra("currNumVoteD");
        final String currPollID = trigerIntent.getStringExtra("currPollID"); // <--- use this as poll ID for share

        Button btnVote = (Button)findViewById(R.id.btn_vote);
        btnVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RadioGroup selectionGroup = findViewById(R.id.choice);

                if (selectionGroup.getCheckedRadioButtonId() == -1) {
                    Context context = getApplicationContext();
                    CharSequence text = "Please make a choice!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    return;
                }

                int choiceID = selectionGroup.getCheckedRadioButtonId();
                RadioButton selectedButton = findViewById(choiceID);

                mFirebaseAuth = FirebaseAuth.getInstance();
                mFirebaseUser = mFirebaseAuth.getCurrentUser();
                mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
                mPollReference = mFirebaseDatabaseReference.child("polls");
                mUserReference = mFirebaseDatabaseReference.child("users");
                mVoteReference = mUserReference.child(mFirebaseUser.getUid()).child("votes");

                // check if poll exists before voting
                DatabaseReference pollReference = mPollReference.child(currPollID);
                pollReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue(SinglePoll.class) == null) {
                            Toast displayError = Toast.makeText(getApplicationContext(), "This poll no longer exists!", Toast.LENGTH_SHORT);
                            displayError.show();
                            finish();
                        } else {
                            RadioGroup selectedGroup = findViewById(R.id.choice);
                            int choiceID = selectedGroup.getCheckedRadioButtonId();
                            RadioButton selectedButton = findViewById(choiceID);
                            int position = selectedGroup.indexOfChild(selectedButton);
                            castVote(currPollID, position);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("ViewPollActivity", "loadPost:onCancelled", databaseError.toException());
                    }
                });
            }
        });

        View currView = this.findViewById(android.R.id.content);

        TextView title = (TextView)currView.findViewById(R.id.txt_Title);
        TextView time_n_author = (TextView)currView.findViewById(R.id.txt_time_n_author);
        TextView numVote = (TextView)currView.findViewById(R.id.num_votes);
        TextView description = (TextView)currView.findViewById(R.id.txt_description);
        TextView choiceA = (TextView)currView.findViewById(R.id.rdb_choice_a);
        TextView choiceB = (TextView)currView.findViewById(R.id.rdb_choice_b);
        TextView choiceC = (TextView)currView.findViewById(R.id.rdb_choice_c);
        TextView choiceD = (TextView)currView.findViewById(R.id.rdb_choice_d);

        //if the no choice C or D then do not display them
        if (currChoiceC.equals("")) choiceC.setVisibility(View.GONE);
        if (currChoiceD.equals("")) choiceD.setVisibility(View.GONE);

        title.setText(currTitle);
        time_n_author.setText(currPostTimeAndAuthor);
        numVote.setText("Votes made: " + currNumVotes);
        description.setText(currDescription);
        choiceA.setText(currChoiceA);
        choiceB.setText(currChoiceB);
        choiceC.setText(currChoiceC);
        choiceD.setText(currChoiceD);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_search; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_share, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        View currView = this.findViewById(android.R.id.content);
        Intent trigerIntent = getIntent(); // get the last Intent that triger this current Intent
        // get all needed information about poll from trigerIntent
        String currPollID = trigerIntent.getStringExtra("currPollID");

        if (id == R.id.action_sharing) {
            Toast.makeText(getApplicationContext(), "The poll ID saved. Paste this into the search bar to open the poll",
                    Toast.LENGTH_LONG).show();
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", currPollID);
            clipboard.setPrimaryClip(clip);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void castVote(String currPollID, int position) {
        String voteChoice;
        switch(position) {
            case 0:
                voteChoice = "numVoteA";
                break;
            case 1:
                voteChoice = "numVoteB";
                break;
            case 2:
                voteChoice = "numVoteC";
                break;
            case 3:
                voteChoice = "numVoteD";
                break;
            default:
                voteChoice = "numVoteA";
                break;
        }

        // prevent concurrent voting issues
        DatabaseReference votesRef = mPollReference.child(currPollID).child(voteChoice);
        votesRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Integer currentValue = mutableData.getValue(Integer.class);
                if (currentValue == null) {
                    mutableData.setValue(1);
                } else {
                    mutableData.setValue(currentValue + 1);
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                System.out.println("Transaction completed");
            }
        });

        DatabaseReference totalVotesRef = mPollReference.child(currPollID).child("numVote");
        totalVotesRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Integer currentValue = mutableData.getValue(Integer.class);
                if (currentValue == null) {
                    mutableData.setValue(1);
                } else {
                    mutableData.setValue(currentValue + 1);
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                System.out.println("Transaction completed");
            }
        });

        // add poll to user's list of polls voted on
        mVoteReference.child(currPollID).setValue("true");

        Intent viewResultIntent = new Intent(ViewPollActivity.this, PollResultActivity.class);
        viewResultIntent.putExtra("currPollId", currPollID);
        startActivity(viewResultIntent);
        finish();

    }
}
