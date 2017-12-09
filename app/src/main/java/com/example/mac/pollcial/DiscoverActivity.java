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
                final SinglePoll poll = allPolls.get(position);
                final String pollId = allPollIDs.get(position);
                openPoll(poll, pollId);
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

        mSearchView.setQueryHint("Paste poll ID here");

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
                DatabaseReference mPollReference = mFirebaseDatabaseReference.child("polls").child(query);
                mPollReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            SinglePoll poll = dataSnapshot.getValue(SinglePoll.class);
                            String pollId = dataSnapshot.getKey();
                            openPoll(poll, pollId);
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

    private void openPoll(final SinglePoll poll, final String pollId) {
        // check if user is owner of poll
        String uid = mFirebaseUser.getUid();
        // if user is owner of poll, go to results
        if(uid.equals(poll.getUid())) {
            viewResult(poll, pollId);
        }
        // if user is not owner, go to voting page, or go to results if already voted
        else {
            // check if user has already voted
            DatabaseReference userVotesDatabaseReference = mFirebaseDatabaseReference.child("users").child(uid).child("votes").child(pollId);
            userVotesDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String vote = dataSnapshot.getValue(String.class);
                    if (vote == null) {
                        // view poll if not voted
                        viewPoll(poll, pollId);
                    } else {
                        // view results if already voted
                        viewResult(poll, pollId);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                }
            });
        }
    }

    private void viewPoll(SinglePoll poll, String pollId) {
        final Intent viewPollIntent = new Intent(DiscoverActivity.this, ViewPollActivity.class);

        String username;
        if(poll.isAnonymous()) {
            username = "Anonymous";
        }
        else {
            username = poll.getUserName();
        }

        String timeAndAuthor = poll.getPollPostTime() + " by " +username;
        // pass all info about current poll
        viewPollIntent.putExtra("currTitle", poll.getPollTitle());
        viewPollIntent.putExtra("currPostTimeAndAuthor", timeAndAuthor);
        viewPollIntent.putExtra("currNumVotes", Integer.toString(poll.getNumVote()));
        viewPollIntent.putExtra("currDescription", poll.getPollDecription());
        viewPollIntent.putExtra("currChoiceA", poll.getPollChoiceA());
        viewPollIntent.putExtra("currChoiceB", poll.getPollChoiceB());
        viewPollIntent.putExtra("currChoiceC", poll.getPollChoiceC());
        viewPollIntent.putExtra("currChoiceD", poll.getPollChoiceD());
        viewPollIntent.putExtra("currPollID", pollId);

        startActivity(viewPollIntent);
    }

    private void viewResult(SinglePoll poll, String pollId) {
        Intent viewResultIntent = new Intent(DiscoverActivity.this, PollResultActivity.class);

        String username;
        if(poll.isAnonymous()) {
            username = "Anonymous";
        }
        else {
            username = poll.getUserName();
        }

        String timeAndAuthor = poll.getPollPostTime() + " by " + username;
        // pass all info about current poll
        viewResultIntent.putExtra("currTitle", poll.getPollTitle());
        viewResultIntent.putExtra("currPostTimeAndAuthor", timeAndAuthor);
        viewResultIntent.putExtra("currNumVotes", Integer.toString(poll.getNumVote()));
        viewResultIntent.putExtra("currDescription", poll.getPollDecription());
        viewResultIntent.putExtra("currNumVoteA", Integer.toString(poll.getNumVoteA()));
        viewResultIntent.putExtra("currNumVoteB", Integer.toString(poll.getNumVoteB()));
        viewResultIntent.putExtra("currNumVoteC", Integer.toString(poll.getNumVoteC()));
        viewResultIntent.putExtra("currNumVoteD", Integer.toString(poll.getNumVoteD()));
        // pass in pollId and poll owner's uid for delete poll function
        viewResultIntent.putExtra("currPollId", pollId);
        viewResultIntent.putExtra("currPollUid", poll.getUid());

        startActivity(viewResultIntent);
    }
}
