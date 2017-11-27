package com.example.mac.pollcial;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.support.v7.widget.SearchView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class DiscoverActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Discover");

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_new_post);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //when click the fab render newpost activity
                startActivity(new Intent(DiscoverActivity.this, NewPostActivity.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




         /******************** Test **********************/
        /* TODO: The following lines between these 2 "Test" tags are hardcodings to test the Discover page layout.
         *  Currently, the layout works. The controller groups should implement a function that can retrieve data of polls and use
         *   that info to create SinglePoll objects. All SinglePoll objects should be put inside the ArrayList
         *   called "allPolls", which is already created below.
         */

        long pollId = 123;
        String pollTitle = "Test Poll #1";
        String pollDecription = "Test Poll #1's Description.";
        String pollChoiceA = "Test Poll #1 choice A";
        String pollChoiceB = "Test Poll #1 choice B";
        String pollChoiceC = "Test Poll #1 choice C";
        String pollChoiceD = "Test Poll #1 choice D";
        String pollPostTime = "Just Now";
        String userName = "User_Admin";
        String userEmail = "Admin@pollcial.com";
        boolean anonymous = false;
        int numVote = 12;

        SinglePoll test_poll_1 = new SinglePoll(pollTitle, pollDecription, pollChoiceA, pollChoiceB,
                            pollChoiceC, pollChoiceD, pollPostTime, "uid1", userName, userEmail, anonymous, numVote);

        long pollId2 = 456;
        String pollTitle2 = "Test Poll #2";
        String pollDecription2 = "Test Poll #2's Description.";
        String pollChoiceA2 = "Test Poll #2 choice A";
        String pollChoiceB2 = "Test Poll #2 choice B";
        String pollChoiceC2 = "Test Poll #2 choice C";
        String pollChoiceD2 = "Test Poll #2 choice D";
        String pollPostTime2 = "Yesterday";
        String userName2 = "User_Admin_2";
        String userEmail2 = "Admin_2@pollcial.com";
        boolean anonymous2 = false;
        int numVote2 = 300;

        SinglePoll test_poll_2 = new SinglePoll(pollTitle2, pollDecription2, pollChoiceA2, pollChoiceB2,
                pollChoiceC2, pollChoiceD2, pollPostTime2, "uid2", userName2, userEmail2, anonymous2, numVote2);

        /********************* Test ************************/





        ListView allPollsListView = (ListView)findViewById(R.id.polls_list);
        final ArrayList<SinglePoll> allPolls = new ArrayList<>();

        /* TODO: replace these two lines, store all SinglePoll objects inside "allPolls" */
        allPolls.add(test_poll_1);
        allPolls.add(test_poll_2);



        PollsAdapter allPollsAdapter = new PollsAdapter(this, allPolls); // create an adapter

        // connect ListView with the adapter
        allPollsListView.setAdapter(allPollsAdapter);

        allPollsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent viewPollIntent = new Intent(DiscoverActivity.this, ViewPollActivity.class);

                SinglePoll currPoll = allPolls.get(position);
                String timeAndAuthor = currPoll.getPollPostTime() + " by " + currPoll.getUserName();
                // pass all info about current poll
                viewPollIntent.putExtra("currTitle", currPoll.getPollTitle());
                viewPollIntent.putExtra("currPostTimeAndAuthor", timeAndAuthor);
                viewPollIntent.putExtra("currNumVotes", Integer.toString(currPoll.getNumVote()));
                viewPollIntent.putExtra("currDescription", currPoll.getPollDecription());
                viewPollIntent.putExtra("currChoiceA", currPoll.getPollChoiceA());
                viewPollIntent.putExtra("currChoiceB", currPoll.getPollChoiceB());
                viewPollIntent.putExtra("currChoiceC", currPoll.getPollChoiceC());
                viewPollIntent.putExtra("currChoiceD", currPoll.getPollChoiceD());

                startActivity(viewPollIntent);

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_search; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.action_searching);
        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(item);

        mSearchView.setQueryHint("Search words or poll ID");

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {

            //render profile activity
            startActivity(new Intent(DiscoverActivity.this, ProfileActivity.class));
        } else if (id == R.id.nav_discover) {

            //do nothing

        } else if (id == R.id.nav_my_poll) {

            //render my poll activity
            startActivity(new Intent(DiscoverActivity.this, MyPollActivity.class));

        } else if (id == R.id.nav_setting) {

            //render setting activity
            startActivity(new Intent(DiscoverActivity.this, SettingsActivity.class));

        } else if (id == R.id.nav_logout) {
            startActivity(new Intent(DiscoverActivity.this, LoginActivity.class));
            mFirebaseAuth.signOut();
            mFirebaseUser = null;
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
