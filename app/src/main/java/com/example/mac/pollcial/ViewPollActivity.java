package com.example.mac.pollcial;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class ViewPollActivity extends AppCompatActivity {

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
        //TODO: SHOULD DISPLAY NUM VOTED ON POLL
        String currTitle = trigerIntent.getStringExtra("currTitle");
        String currPostTimeAndAuthor = trigerIntent.getStringExtra("currPostTimeAndAuthor");
        String currNumVotes = trigerIntent.getStringExtra("currNumVotes");
        String currDescription = trigerIntent.getStringExtra("currDescription");
        String currChoiceA = trigerIntent.getStringExtra("currChoiceA");
        String currChoiceB = trigerIntent.getStringExtra("currChoiceB");
        String currChoiceC = trigerIntent.getStringExtra("currChoiceC");
        String currChoiceD = trigerIntent.getStringExtra("currChoiceD");

        View currView = this.findViewById(android.R.id.content);

        TextView title = (TextView)currView.findViewById(R.id.txt_Title);
        TextView time_n_author = (TextView)currView.findViewById(R.id.txt_time_n_author);
        TextView numVote = (TextView)currView.findViewById(R.id.num_votes);
        TextView description = (TextView)currView.findViewById(R.id.txt_description);
        TextView choiceA = (TextView)currView.findViewById(R.id.rdb_choice_a);
        TextView choiceB = (TextView)currView.findViewById(R.id.rdb_choice_b);
        TextView choiceC = (TextView)currView.findViewById(R.id.rdb_choice_c);
        TextView choiceD = (TextView)currView.findViewById(R.id.rdb_choice_d);

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sharing) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
