package mars_williams.tweetastic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import mars_williams.tweetastic.R;
import mars_williams.tweetastic.TwitterApplication;
import mars_williams.tweetastic.fragments.TweetsListFragment;
import mars_williams.tweetastic.fragments.UserTimelineFragment;
import mars_williams.tweetastic.models.Tweet;
import mars_williams.tweetastic.models.User;
import mars_williams.tweetastic.networking.TwitterClient;

import static mars_williams.tweetastic.activities.TimelineActivity.REQUEST_CODE_DETAILS;
import static mars_williams.tweetastic.activities.TimelineActivity.TWEET_POSITION;
import static mars_williams.tweetastic.models.User.fromJSON;


public class ProfileActivity extends AppCompatActivity
        implements TweetsListFragment.TweetSelectedListener,
        TweetsListFragment.LoadingProgressDialog {

    @BindView(R.id.tvUserName)
    TextView tvName;
    @BindView(R.id.tvTagline)
    TextView tvTagline;
    @BindView(R.id.tvFollowers)
    TextView tvFollowers;
    @BindView(R.id.tvFollowing)
    TextView tvFollowing;
    @BindView(R.id.ivProfileImage)
    ImageView ivProfileImage;

    TwitterClient client;
    Tweet tweet;
    UserTimelineFragment userTimelineFragment;
    TextView mTitleTextView;
    String screenName;
    MenuItem miActionProgressItem;
    MenuItem miHomeItem;
    ProgressBar progressBar;
    User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        client = TwitterApplication.getRestClient();

        screenName = getIntent().getStringExtra("screen_name");
        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        if (tweet != null) {
            screenName = tweet.getUser().getScreenName();
        }

        setupActionBar();

        // Create the user fragment
        userTimelineFragment = UserTimelineFragment.newInstance(screenName);
        // Display the user timeline fragment inside the container (dynamically)
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Make transaction
        ft.replace(R.id.flContainer, userTimelineFragment);
        // Commit transaction
        ft.commit();

        // If the tweet is valid, get another user's info
        if (tweet != null) {
            populateUserHeadline(tweet.getUser());
            // If the tweet is invalid, get this user's info
        } else if (screenName != null) {
            getUserInfo(screenName);
        } else {
            getCurrentUserInfo();
        }
    }

    public void setupActionBar() {
        // Set up the action bar
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.action_bar, null);

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
    }

    public void populateUserHeadline(User user) {
        mUser = user;
        // Populate profile info views
        tvName.setText(user.getName());
        tvTagline.setText(user.getTagLine());
        tvFollowers.setText(getString(R.string.followers_count, user.getFollowersCount()));
        tvFollowing.setText(getString(R.string.following_count, user.getFollowingCount()));
        mTitleTextView = (TextView) findViewById(R.id.actionbar_title);
        mTitleTextView.setText(getString(R.string.user_profile, user.getName()));

        // Load profile image with Glide
        Glide.with(this)
                .load(user.getProfileImageUrl())
                .bitmapTransform(new RoundedCornersTransformation(this, 25, 0))
                .placeholder(R.drawable.ic_profile_image_placeholder)
                .into(ivProfileImage);
    }

    @OnClick(R.id.tvFollowers)
    public void getFollowers() {
        // Create an intent to the FollowersActivity
        Intent i = new Intent(this, FollowersActivity.class);
        i.putExtra("screen_name", mUser.getScreenName());
        startActivityForResult(i, REQUEST_CODE_DETAILS);
    }

    @OnClick(R.id.tvFollowing)
    public void getFollowing() {
        // Create an intent to the FollowingActivity
        Intent i = new Intent(this, FollowingActivity.class);
        i.putExtra("screen_name", mUser.getScreenName());
        startActivityForResult(i, REQUEST_CODE_DETAILS);
    }

    public void getCurrentUserInfo() {
        client.verifyCredentials(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    // Deserialize the user object
                    User user = fromJSON(response);
                    // Populate the user headline
                    populateUserHeadline(user);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Failed to get profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), "Failed to get profile", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), "Failed to get profile", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), "Failed to get profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getUserInfo(String screenName) {
        client.getUser(screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Deserialize the user object
                try {
                    // Populate the user headline
                    User user = fromJSON(response.getJSONObject(0));
                    populateUserHeadline(user);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), "Failed to get profile", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), "Failed to get profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onTweetSelected(Tweet tweet, int position) {
        if (position != RecyclerView.NO_POSITION) {
            // Create an intent to the TweetDetailsActivity with tweet and position
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
            // Deserialize the tweet and position
            Tweet newTweet = Parcels.unwrap(data.getParcelableExtra(Tweet.class.getSimpleName()));
            int position = data.getIntExtra(TWEET_POSITION, 0);
            // Replace the tweet with the new one and notify the adapter
            userTimelineFragment.tweets.set(position, newTweet);
            userTimelineFragment.tweetAdapter.notifyItemChanged(position);
            userTimelineFragment.rvTweets.scrollToPosition(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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
