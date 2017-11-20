package com.example.mac.pollcial;

import android.content.Context;
import android.content.Intent;
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

import static com.example.mac.pollcial.R.id.action_post_poll;

public class NewPostActivity extends AppCompatActivity {

    private Button btnCancel;

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
                //need to pass poll object to firebase.
                SinglePoll poll = new SinglePoll(0, pollTitle.getText().toString(), pollDescription.getText().toString()
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
