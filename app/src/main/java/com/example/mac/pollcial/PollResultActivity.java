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
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PollResultActivity extends AppCompatActivity {

    private DatabaseReference mFirebaseDatabaseReference;
    private DatabaseReference mPollReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private String mPollUid;
    private String mPollId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_result);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
            ClipData clip = ClipData.newPlainText("label", currPollID);
            clipboard.setPrimaryClip(clip);
            return true;
        }
        else if (id == R.id.action_delete) {
            deletePoll();
            startActivity(new Intent(PollResultActivity.this, DiscoverActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
