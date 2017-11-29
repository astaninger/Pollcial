package com.example.mac.pollcial;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Button;
import android.support.v7.widget.SearchView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyPollActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList<SinglePoll> allPolls = new ArrayList<>();

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private PollsAdapter mPollsAdapter;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("My Polls");

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_new_post);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //.setAction("Action", null).show();

                //when click the fab render newpost activity
                startActivity(new Intent(MyPollActivity.this, NewPostActivity.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView username = (TextView) header.findViewById(R.id.txt_nav_username);

        if(mFirebaseUser.getDisplayName() != null) {
            username.setText(mFirebaseUser.getDisplayName());
        }
        else {
            username.setText("Guest");
        }

        ListView allPollsListView = (ListView)findViewById(R.id.polls_list);


        mPollsAdapter = new PollsAdapter(this, allPolls); // create an adapter

        // connect ListView with the adapter
        allPollsListView.setAdapter(mPollsAdapter);


        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mPollReference = mFirebaseDatabaseReference.child("polls");
        mPollReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allPolls.clear();

                for (DataSnapshot pollSnapshot: dataSnapshot.getChildren()) {
                    // TODO: handle the post
                    SinglePoll poll = pollSnapshot.getValue(SinglePoll.class);

                    user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = user.getUid();
                    if (uid.equals(poll.getUid())){
                        allPolls.add(0,poll);
                    }
                }
                mPollsAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        allPollsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent viewPollIntent = new Intent(MyPollActivity.this, ViewPollActivity.class);

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

                //TODO: Check if the user can edit.
                /*
                user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = user.getUid();
                if (uid == currPoll.getUid()){

                }

                else {

                }
                */

                startActivity(viewPollIntent);


            }
        });
 /*
        LinearLayout bViewPoll = (LinearLayout) findViewById(R.id.btn_view_poll);
        bViewPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyPollActivity.this, ViewPollActivity.class));
            }
        });

        LinearLayout bViewPoll2 = (LinearLayout) findViewById(R.id.btn_view_poll2);
        bViewPoll2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyPollActivity.this, TempViewPollActivity.class));
            }
        });
        */
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

        mSearchView.setQueryHint("Paste poll ID here");

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
            startActivity(new Intent(MyPollActivity.this, ProfileActivity.class));
        } else if (id == R.id.nav_discover) {

            //render discover activity
            startActivity(new Intent(MyPollActivity.this, DiscoverActivity.class));

        } else if (id == R.id.nav_my_poll) {

            //do nothing

        } else if (id == R.id.nav_setting) {

            //render setting activity
            startActivity(new Intent(MyPollActivity.this, SettingsActivity.class));

        } else if (id == R.id.nav_logout) {

            startActivity(new Intent(MyPollActivity.this, LoginActivity.class));
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
