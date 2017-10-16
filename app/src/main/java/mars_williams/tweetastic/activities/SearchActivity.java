package mars_williams.tweetastic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;

import org.parceler.Parcels;

import butterknife.ButterKnife;
import mars_williams.tweetastic.R;
import mars_williams.tweetastic.TwitterApplication;
import mars_williams.tweetastic.fragments.SearchTweetsFragment;
import mars_williams.tweetastic.fragments.TweetsListFragment;
import mars_williams.tweetastic.models.Tweet;
import mars_williams.tweetastic.networking.TwitterClient;

import static mars_williams.tweetastic.activities.TimelineActivity.REQUEST_CODE_DETAILS;
import static mars_williams.tweetastic.activities.TimelineActivity.TWEET_POSITION;
import static mars_williams.tweetastic.activities.TimelineActivity.adapter;
import static mars_williams.tweetastic.activities.TimelineActivity.vpPager;

/**
 * Created by mars_williams on 10/16/17.
 */

public class SearchActivity extends AppCompatActivity implements TweetsListFragment.TweetSelectedListener {

    String searchQuery;
    SearchTweetsFragment searchFragment;
    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        client = TwitterApplication.getRestClient();

        // Fetch the search query entered in the search bar
        searchQuery = getIntent().getStringExtra("search_query");

        // Set up the action bar
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.action_bar, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.actionbar_title);
        mTitleTextView.setText(getString(R.string.search_title, searchQuery));

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

        // Inflate the fragment
        searchFragment = SearchTweetsFragment.getInstance(searchQuery);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flContainerSearch, searchFragment);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onTweetSelected(Tweet tweet, int position) {
        if (position != RecyclerView.NO_POSITION) {
            // Create an intent to the TweetDetailsActivity with the tweet
            Intent i = new Intent(this, TweetDetailsActivity.class);
            i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
            i.putExtra(TWEET_POSITION, position);
            startActivityForResult(i, REQUEST_CODE_DETAILS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // If returning successfully from DetailsActivity
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_DETAILS) {
            // Deserialize the tweet and its position
            Tweet newTweet = Parcels.unwrap(data.getParcelableExtra(Tweet.class.getSimpleName()));
            int position = data.getIntExtra(TWEET_POSITION, 0);
            // Update the tweet in the search result list and notify the adapter
            TweetsListFragment currentFragment = adapter.getRegisteredFragment(vpPager.getCurrentItem());
            currentFragment.tweets.set(position, newTweet);
            currentFragment.tweetAdapter.notifyItemChanged(position);
            currentFragment.rvTweets.scrollToPosition(position);
        }
    }
}