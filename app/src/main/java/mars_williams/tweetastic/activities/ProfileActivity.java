package mars_williams.tweetastic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
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


public class ProfileActivity extends AppCompatActivity implements TweetsListFragment.TweetSelectedListener {

    @BindView(R.id.tvUserName)
    TextView tvName;
    @BindView(R.id.tvTagline)
    TextView tvTagline;
    @BindView(R.id.tvFollowers)
    TextView tvFollowers;
    @BindView(R.id.tvFollowing)
    TextView tvFollowing;
    @BindView(R.id.tvScreenName)
    TextView tvScreenName;
    @BindView(R.id.ivProfileImage)
    ImageView ivProfileImage;

    TwitterClient client;
    Tweet tweet;
    UserTimelineFragment userTimelineFragment;
    TextView mTitleTextView;
    String screenName;
    User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        client = TwitterApplication.getRestClient();

        // Get intent extras
        screenName = getIntent().getStringExtra("screen_name");
        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        mUser = getUser();
        populateUserHeadline(mUser);
        setupActionBar(mUser);

        // Create the user fragment
        userTimelineFragment = UserTimelineFragment.newInstance(mUser.getScreenName());
        // Display the user timeline fragment inside the container (dynamically)
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Make transaction
        ft.replace(R.id.flContainer, userTimelineFragment);
        // Commit transaction
        ft.commit();
    }

    public User getUser() {
        if (tweet != null) {
            return tweet.getUser();
            // If the tweet is invalid, get this user's info
        } else if (screenName != null) {
            return getUserInfo(screenName);
        } else {
            return getCurrentUserInfo();
        }
    }

    public void setupActionBar(User mUser) {
        // Set up the action bar
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.action_bar, null);

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setTitle(mUser.getName());
    }

    public void populateUserHeadline(User mUser) {
        // Populate profile info views
        tvName.setText(mUser.getName());
        tvScreenName.setText(mUser.getScreenName());
        tvTagline.setText(mUser.getTagLine());
        tvFollowers.setText(getString(R.string.followers_count, mUser.getFollowersCount()));
        tvFollowing.setText(getString(R.string.following_count, mUser.getFollowingCount()));

        // Load profile image with Glide
        Glide.with(this)
                .load(mUser.getProfileImageUrl())
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

    public User getCurrentUserInfo() {
        client.verifyCredentials(new JsonHttpResponseHandler() {
            User mUser;
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    // Deserialize the user object
                    mUser = fromJSON(response);
                    // Populate the user headline
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
        return mUser;
    }

    public User getUserInfo(String screenName) {
        client.getUser(screenName, new JsonHttpResponseHandler() {
            User mUser;
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Deserialize the user object
                try {
                    User user = fromJSON(response.getJSONObject(0));
                    // Populate the user headline
                    mUser = user;
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
        return mUser;
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
}
