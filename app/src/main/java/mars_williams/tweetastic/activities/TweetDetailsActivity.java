package mars_williams.tweetastic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import mars_williams.tweetastic.models.Tweet;
import mars_williams.tweetastic.networking.TwitterClient;

import static mars_williams.tweetastic.activities.TimelineActivity.REQUEST_CODE_REPLY;
import static mars_williams.tweetastic.activities.TimelineActivity.TWEET_POSITION;


/**
 * Created by mars_williams on 10/11/17.
 */

public class TweetDetailsActivity extends AppCompatActivity {

    @BindView(R.id.ivProfileImageDetails)
    ImageView ivProfileImage;
    @BindView(R.id.tvUserNameDetails)
    TextView tvUserName;
    @BindView(R.id.tvScreenNameDetails)
    TextView tvScreenName;
    @BindView(R.id.tvTimeStampDetails)
    TextView tvTimeStamp;
    @BindView(R.id.tvTweetTextDetails)
    TextView tvTweetBody;
    @BindView(R.id.tvNumRetweetsDetails)
    TextView tvNumRetweets;
    @BindView(R.id.tvNumFavoritesDetails)
    TextView tvNumFavorites;
    @BindView(R.id.ibFavoriteDetails)
    ImageView ibFavorited;
    @BindView(R.id.ibRetweetDetails)
    ImageView ibRetweeted;
    @BindView(R.id.ivMediaImageDetails)
    ImageView ivMediaImage;

    Tweet tweet;
    TwitterClient client;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);
        ButterKnife.bind(this);
        client = TwitterApplication.getRestClient();

        // Set up the action bar
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.action_bar, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.actionbar_title);
        mTitleTextView.setText(getString(R.string.details));

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

        // Deserialize the tweet and position
        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        position = getIntent().getIntExtra(TWEET_POSITION, 0);

        populateDetails();

        setFavorited();
        setRetweeted();
    }

    public void populateDetails() {
        // Populate the views according to this data
        tvUserName.setText(tweet.getUser().name);
        tvTweetBody.setText(tweet.getBody());
        tvScreenName.setText(getString(R.string.replying_to, tweet.getUser().getScreenName()));
        tvTimeStamp.setText(tweet.getCreatedAt());

        // Load the profile image
        Glide.with(this)
                .load(tweet.getUser().getProfileImageUrl())
                .bitmapTransform(new RoundedCornersTransformation(this, 25, 0))
                .placeholder(R.drawable.ic_profile_image_placeholder)
                .into(ivProfileImage);

        // Load the media image
        Glide.with(this)
                .load(tweet.media.getMediaUrl())
                .bitmapTransform(new RoundedCornersTransformation(this, 25, 0))
                .into(ivMediaImage);
    }

    public void setFavorited() {
        // Set the tweet favorited status
        if (tweet.isFavorited()) {
            ibFavorited.setImageResource(R.drawable.ic_favorite);
        } else {
            ibFavorited.setImageResource(R.drawable.ic_unfavorite);
        }
        // Set the number of favorites
        if (tweet.getFavoriteCount() > 0) {
            tvNumFavorites.setText(String.valueOf(tweet.getFavoriteCount()));
        } else {
            tvNumFavorites.setText("");
        }
    }

    public void setRetweeted() {
        // Set the retweeted status
        if (tweet.isRetweeted()) {
            ibRetweeted.setImageResource(R.drawable.ic_retweet);
        } else {
            ibRetweeted.setImageResource(R.drawable.ic_unretweet);
        }
        // Set the number of retweets
        if (tweet.getRetweetCount() > 0) {
            tvNumRetweets.setText(String.valueOf(tweet.getRetweetCount()));
        } else {
            tvNumRetweets.setText("");
        }
    }

    @OnClick(R.id.ibMessageDetails)
    public void putReply() {
        // Send the new tweet back to reply
        Intent i = new Intent(this, ReplyActivity.class);
        i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
        startActivityForResult(i, REQUEST_CODE_REPLY);
    }

    @OnClick(R.id.ibRetweetDetails)
    public void putRetweet() {
        if (tweet.isRetweeted()) {
            unretweetTweet();
        } else {
            retweetTweet();
        }
    }

    @OnClick(R.id.ibFavoriteDetails)
    public void putFavorite() {
        if (tweet.isFavorited()) {
            unfavoriteTweet();
        } else {
            favoriteTweet();
        }
    }

    public void retweetTweet() {
        client.retweet(tweet.getTweetId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Tweet newTweet = Tweet.fromJSON(response);
                    tweet.setRetweeted(true);
                    if (tweet.getRetweetCount() < newTweet.getRetweetCount()) {
                        tweet.setRetweetCount(newTweet.getRetweetCount());
                    }
                    setRetweeted();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), getString(R.string.unable_to_retweet), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), getString(R.string.unable_to_retweet), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), getString(R.string.unable_to_retweet), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), getString(R.string.unable_to_retweet), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void unretweetTweet() {
        client.unRetweet(tweet.getTweetId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Tweet newTweet = Tweet.fromJSON(response);
                    tweet.setRetweeted(false);
                    if (tweet.getRetweetCount() > newTweet.getRetweetCount()) {
                        tweet.setRetweetCount(newTweet.getRetweetCount());
                    }
                    setRetweeted();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), getString(R.string.unable_to_unretweet), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), getString(R.string.unable_to_unretweet), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), getString(R.string.unable_to_unretweet), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), getString(R.string.unable_to_unretweet), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void favoriteTweet() {
        client.favoriteTweet(tweet.getTweetId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    // Endpoint returns original tweet; update the tweet
                    int oldFavoriteCount = tweet.getFavoriteCount();
                    tweet = Tweet.fromJSON(response);
                    if (!tweet.isFavorited()) {
                        tweet.setFavorited(true);
                    }
                    if (tweet.getFavoriteCount() < oldFavoriteCount + 1) {
                        tweet.setFavoriteCount(oldFavoriteCount + 1);
                    }
                    setFavorited();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), getString(R.string.unable_to_favorite_tweet), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), getString(R.string.unable_to_favorite_tweet), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), getString(R.string.unable_to_favorite_tweet), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), getString(R.string.unable_to_favorite_tweet), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void unfavoriteTweet() {
        client.unfavoriteTweet(tweet.getTweetId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    // Endpoint returns original tweet; update the tweet
                    int oldFavoriteCount = tweet.getFavoriteCount();
                    tweet = Tweet.fromJSON(response);
                    if (tweet.isFavorited()) {
                        tweet.setFavorited(false);
                    }
                    if (tweet.getFavoriteCount() > oldFavoriteCount - 1) {
                        tweet.setFavoriteCount(oldFavoriteCount - 1);
                    }
                    setFavorited();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), getString(R.string.unable_to_unfavorite_tweet), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), getString(R.string.unable_to_unfavorite_tweet), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), getString(R.string.unable_to_unfavorite_tweet), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), getString(R.string.unable_to_unfavorite_tweet), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Return to the calling activity, send the new tweet and position
        Intent i = new Intent();
        i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
        i.putExtra(TWEET_POSITION, position);
        setResult(RESULT_OK, i);
        finish();
    }
}

