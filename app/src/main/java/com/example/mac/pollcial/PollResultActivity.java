package com.example.mac.pollcial;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PollResultActivity extends AppCompatActivity {

    private DatabaseReference mFirebaseDatabaseReference;
    private DatabaseReference mPollReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private String mPollUid;
    private String mPollId;

    private SinglePoll currPoll;



    String currPollTitle;
    String currPollAuthor;
    String currPollTime;
    String currTotalNumVotes;
    String currDescription;
    String currChoiceA;
    String currChoiceB;
    String currChoiceC;
    String currChoiceD;
    String currNumA;
    String currNumB;
    String currNumC;
    String currNumD;

    String pollID;

    private View currView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_result);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

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

        Intent trigerIntent = getIntent();
        final String currPollID = trigerIntent.getStringExtra("currPollId");
        pollID = currPollID;


        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mPollReference = mFirebaseDatabaseReference.child("polls").child(currPollID);
        mPollReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d("PollResultActivity", "on data change");
                    currPoll = dataSnapshot.getValue(SinglePoll.class);
                    Log.d("PollResultActivity", "currPoll key: " + dataSnapshot.getKey());

                    currPollTitle = currPoll.getPollTitle();
                    Log.d("PollResultActivity", "poll title: " + currPollTitle);
                    currPollAuthor = currPoll.getUserName();
                    currPollTime = currPoll.getPollPostTime();
                    currTotalNumVotes = Integer.toString(currPoll.getNumVote());
                    currDescription = currPoll.getPollDecription();
                    currChoiceA = currPoll.getPollChoiceA();
                    currChoiceB = currPoll.getPollChoiceB();
                    currChoiceC = currPoll.getPollChoiceC();
                    currChoiceD = currPoll.getPollChoiceD();
                    currNumA = Integer.toString(currPoll.getNumVoteA());
                    currNumB = Integer.toString(currPoll.getNumVoteB());
                    currNumC = Integer.toString(currPoll.getNumVoteC());
                    currNumD = Integer.toString(currPoll.getNumVoteD());

                    //calculate percentage
                    int totalVotes = currPoll.getNumVote();
                    int percentA = (int)((currPoll.getNumVoteA() * 100.0f) / totalVotes);
                    int percentB = (int)((currPoll.getNumVoteB() * 100.0f) / totalVotes);
                    int percentC = (int)((currPoll.getNumVoteC() * 100.0f) / totalVotes);
                    int percentD = (int)((currPoll.getNumVoteD() * 100.0f) / totalVotes);

                    currView = findViewById(R.id.pollResultView);

                    TextView title = (TextView)currView.findViewById(R.id.txt_Title);
                    TextView time_n_author = (TextView)currView.findViewById(R.id.txt_time_n_author);
                    TextView numVote = (TextView)currView.findViewById(R.id.num_votes);
                    TextView description = (TextView)currView.findViewById(R.id.txt_description);
                    TextView choiceA = (TextView)currView.findViewById(R.id.txt_choicea);
                    TextView choiceB = (TextView)currView.findViewById(R.id.txt_choiceb);
                    TextView choiceC = (TextView)currView.findViewById(R.id.txt_choicec);
                    TextView choiceD = (TextView)currView.findViewById(R.id.txt_choiced);

                    TextView choiceARes = (TextView)currView.findViewById(R.id.txt_choicea_result);
                    TextView choiceBRes = (TextView)currView.findViewById(R.id.txt_choiceb_result);
                    TextView choiceCRes = (TextView)currView.findViewById(R.id.txt_choicec_result);
                    TextView choiceDRes = (TextView)currView.findViewById(R.id.txt_choiced_result);

                    TextView cTitle = (TextView)currView.findViewById(R.id.txt_c);
                    TextView dTitle = (TextView)currView.findViewById(R.id.txt_d);

                    //if the no choice C or D then do not display them
                    if (currChoiceC.equals("")) choiceC.setVisibility(View.INVISIBLE);
                    if (currChoiceD.equals("")) choiceD.setVisibility(View.INVISIBLE);
                    if (currChoiceC.equals("")) choiceCRes.setVisibility(View.INVISIBLE);
                    if (currChoiceD.equals("")) choiceDRes.setVisibility(View.INVISIBLE);
                    if (currChoiceC.equals("")) cTitle.setVisibility(View.INVISIBLE);
                    if (currChoiceD.equals("")) dTitle.setVisibility(View.INVISIBLE);

                    title.setText(currPollTitle);
                    time_n_author.setText(currPollTime + " by " + currPollAuthor);
                    numVote.setText("Total vote(s) made: " + currTotalNumVotes);
                    description.setText(currDescription);
                    choiceA.setText(currChoiceA);
                    choiceB.setText(currChoiceB);
                    choiceC.setText(currChoiceC);
                    choiceD.setText(currChoiceD);

                    choiceARes.setText(currNumA + " vote(s), " + percentA + "%");
                    choiceBRes.setText(currNumB + " vote(s), " + percentB + "%");
                    choiceCRes.setText(currNumC + " vote(s), " + percentC + "%");
                    choiceDRes.setText(currNumD + " vote(s), " + percentD + "%");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Button btnBack = (Button)findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
    }

    private void deletePoll() {
        String pollId = getIntent().getStringExtra("currPollId");
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mPollReference = mFirebaseDatabaseReference.child("polls");
        mPollReference.child(pollId).removeValue();
        Context context = getApplicationContext();
        CharSequence text = "Poll Deleted";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_search; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_share, menu);

        MenuItem delete = menu.findItem(R.id.action_delete);
        String uid = mFirebaseUser.getUid();
        String pollUid = getIntent().getStringExtra("currPollUid");
        if(uid.equals(pollUid)) {
            delete.setVisible(true);
        }

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
        //TODO: SHOULD DISPLAY NUM VOTED ON POLL
        String currPollID = trigerIntent.getStringExtra("currPollID");

        if (id == R.id.action_sharing) {
            Toast.makeText(getApplicationContext(), "The poll ID saved. Paste this into the search bar to open the poll",
                    Toast.LENGTH_LONG).show();
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", pollID);
            clipboard.setPrimaryClip(clip);
            return true;
        }
        else if (id == R.id.action_delete) {
            deletePoll();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
