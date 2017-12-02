package com.example.mac.pollcial;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
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
import android.widget.Toast;

import java.util.ArrayList;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class DiscoverActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final String TAG = "DiscoverActivity";

    ArrayList<SinglePoll> allPolls = new ArrayList<>();
    ArrayList<String> allPollIDs = new ArrayList<>();
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private PollsAdapter mPollsAdapter;

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
        View header = navigationView.getHeaderView(0);
        final TextView username = (TextView) header.findViewById(R.id.txt_nav_username);

        if(mFirebaseUser.isAnonymous()) {
            username.setText("Guest");
        }
        else {
            final String displayName = mFirebaseUser.getDisplayName();
            if(displayName == null) {
                mFirebaseUser.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        username.setText(mFirebaseUser.getDisplayName());
                    }
                });
            }
            username.setText(displayName);
        }


        ListView allPollsListView = getListView();




        allPollsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                //TODO: Check if the user can delete.
                String uid = mFirebaseUser.getUid();
                SinglePoll currPoll = allPolls.get(position);
                if(uid.equals(currPoll.getUid())){
                    //if user is the owner of poll, go to view result
                    startActivity(new Intent(DiscoverActivity.this, PollResultActivity.class));
                    return;
                }

                // check if user has already voted
                DatabaseReference temp = mFirebaseDatabaseReference.child("users").child(uid).child("votes").child(allPollIDs.get(position));
                temp.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String poll = dataSnapshot.getValue(String.class);
                        if(poll == null) {
                            // view poll if not voted
                            viewPoll(position);
                        }
                        else {
                            // view results if already voted
                            startActivity(new Intent(DiscoverActivity.this, PollResultActivity.class));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    }
                });



            }
        });
    }

    @NonNull
    private ListView getListView() {
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
                allPollIDs.clear();

                for (DataSnapshot pollSnapshot: dataSnapshot.getChildren()) {
                    // TODO: handle the post
                    SinglePoll poll = pollSnapshot.getValue(SinglePoll.class);
                    String pollID = pollSnapshot.getKey();
                    allPolls.add(0, poll);
                    allPollIDs.add(0,pollID); // <--- this is the ID list that has 1-to-1 relationship to allPolls

                }
                mPollsAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return allPollsListView;
    }


    @Override
    public void onRestart(){
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        TextView username = (TextView) header.findViewById(R.id.txt_nav_username);
        if(mFirebaseUser.isAnonymous()) {
            username.setText("Guest");
        }
        else {
            username.setText(mFirebaseUser.getDisplayName());
        }
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

        mSearchView.setQueryHint(" Paste poll ID here");

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
                DatabaseReference mPollReference = mFirebaseDatabaseReference.child("polls").child(query);
                mPollReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            SinglePoll currPoll = dataSnapshot.getValue(SinglePoll.class);
                            Intent viewPollIntent = new Intent(DiscoverActivity.this, ViewPollActivity.class);
                            // pass all info about current poll
                            viewPollIntent.putExtra("currTitle", currPoll.getPollTitle());
                            viewPollIntent.putExtra("currNumVotes", Integer.toString(currPoll.getNumVote()));
                            viewPollIntent.putExtra("currDescription", currPoll.getPollDecription());
                            viewPollIntent.putExtra("currChoiceA", currPoll.getPollChoiceA());
                            viewPollIntent.putExtra("currChoiceB", currPoll.getPollChoiceB());
                            viewPollIntent.putExtra("currChoiceC", currPoll.getPollChoiceC());
                            viewPollIntent.putExtra("currChoiceD", currPoll.getPollChoiceD());
                            viewPollIntent.putExtra("currPollID", dataSnapshot.getKey());

                            //TODO: Check if the user can edit.
                            String uid = mFirebaseUser.getUid();
                            if (uid == currPoll.getUid()) {

                            } else {

                            }
                            startActivity(viewPollIntent);
                        }
                        else
                        {
                            Toast.makeText(DiscoverActivity.this, "PollID does not exist.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

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

    public void refresh () {
        finish();
        startActivity(new Intent(DiscoverActivity.this, DiscoverActivity.class));
    }

    private void viewPoll(int position) {
        final Intent viewPollIntent = new Intent(DiscoverActivity.this, ViewPollActivity.class);

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
        viewPollIntent.putExtra("currPollID", allPollIDs.get(position));
        viewPollIntent.putExtra("currNumVoteA", Integer.toString(currPoll.getNumVoteA()));
        viewPollIntent.putExtra("currNumVoteB", Integer.toString(currPoll.getNumVoteB()));
        viewPollIntent.putExtra("currNumVoteC", Integer.toString(currPoll.getNumVoteC()));
        viewPollIntent.putExtra("currNumVoteD", Integer.toString(currPoll.getNumVoteD()));

        startActivity(viewPollIntent);
    }
}
