package mars_williams.tweetastic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.ButterKnife;
import mars_williams.tweetastic.R;
import mars_williams.tweetastic.TwitterApplication;
import mars_williams.tweetastic.fragments.FollowersFragment;
import mars_williams.tweetastic.fragments.UsersListFragment;
import mars_williams.tweetastic.models.User;
import mars_williams.tweetastic.networking.TwitterClient;


/**
 * Created by mars_williams on 10/16/17.
 */

public class FollowersActivity extends AppCompatActivity
        implements UsersListFragment.UserSelectedListener,
        UsersListFragment.LoadingProgressDialog {

    TwitterClient client;
    String screenName;
    MenuItem miActionProgressItem;
    MenuItem miHomeItem;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        ButterKnife.bind(this);
        client = TwitterApplication.getRestClient();

        // Fetch the followers query entered in the followers bar
        screenName = getIntent().getStringExtra("screen_name");

        // Set up the action bar
        setupActionBar();

        // Inflate the fragment
        FollowersFragment followersFragment = FollowersFragment.getInstance(screenName);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flContainerFollowers, followersFragment);
        ft.commit();
    }

    public void setupActionBar() {
        // Set up the action bar
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.action_bar, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.actionbar_title);
        mTitleTextView.setText(getString(R.string.user_followers, screenName));

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
    }

    @Override
    public void onUserSelected(User user, int position) {
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra("screen_name", user.getScreenName());
        (this).startActivity(i);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.menu_followers, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        miHomeItem = menu.findItem(R.id.miHome);
        // Extract the action-view from the menu item
        progressBar = (ProgressBar) miActionProgressItem.getActionView();
        miHomeItem.isEnabled();
        miHomeItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i = new Intent(getBaseContext(), TimelineActivity.class);
                startActivity(i);
                return true;
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void showProgressBar() {
        if (miActionProgressItem != null) {
            miActionProgressItem.setVisible(true);
        }
    }

    @Override
    public void hideProgressBar() {
        if (miActionProgressItem != null) {
            miActionProgressItem.setVisible(false);
        }
    }
}
