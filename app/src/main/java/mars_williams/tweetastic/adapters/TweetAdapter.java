package mars_williams.tweetastic.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import mars_williams.tweetastic.R;
import mars_williams.tweetastic.TwitterApplication;
import mars_williams.tweetastic.activities.ProfileActivity;
import mars_williams.tweetastic.activities.ReplyActivity;
import mars_williams.tweetastic.models.Tweet;
import mars_williams.tweetastic.networking.TwitterClient;

import static mars_williams.tweetastic.activities.TimelineActivity.REQUEST_CODE_REPLY;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    List<Tweet> mTweets;
    Context context;
    TwitterClient client;
    LayoutInflater inflater;
    private TweetAdapterListener mListener;

    // Desfine an interface required by the ViewHolder
    public interface TweetAdapterListener {
        public void onItemSelected(View view, int position);
    }

    // Pass in the Tweets array in the constructor
    public TweetAdapter(List<Tweet> tweets, TweetAdapterListener listener) {
        mTweets = tweets;
        mListener = listener;
    }

    // Constructor for onclicklistener
    public TweetAdapter(Context context, AdapterView.OnItemClickListener listener, List<Tweet> tweets) {
        inflater = LayoutInflater.from(context);
        this.mTweets = tweets;
    }

    // For each row, inflate layout and cache references into ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        if(inflater == null) {
            inflater = LayoutInflater.from(context);
        }
        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    // Bind the values based on the position of the element
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the data according to position
        Tweet tweet = mTweets.get(position);

        // Populate the views according to this data
        holder.tvUsername.setText(tweet.getUser().name);
        holder.tvBody.setText(tweet.getBody());
        holder.tvScreenName.setText(holder.tvScreenName.getContext()
                .getString(R.string.formatted_user_screen_name, tweet.getUser().getScreenName()));
        holder.tvCreatedAt.setText(tweet.getRelativeCreatedAt());
        holder.tvBody.setText(tweet.getBody());
        holder.tvReplyCount.setText("");
        holder.tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));

        // Set the favorited status
        if(tweet.isFavorited()) {
            holder.ibFavorite.setImageResource(R.drawable.ic_favorite);
        } else {
            holder.ibFavorite.setImageResource(R.drawable.ic_unfavorite);
        }

        // If for some reason the favorite count is not working, set it to 1
        if(tweet.isFavorited() && tweet.getFavoriteCount() <= 0) {
            tweet.setFavoriteCount(1);
        }

        // Set the favorite count
        if(tweet.getFavoriteCount() > 0) {
            holder.tvFavoriteCount.setText(String.valueOf(tweet.getFavoriteCount()));
        } else {
            holder.tvFavoriteCount.setText("");
        }

        if(tweet.isRetweeted()) {
            holder.ibRetweet.setImageResource(R.drawable.ic_retweet);
        } else {
            holder.ibRetweet.setImageResource(R.drawable.ic_unretweet);
        }

        if(tweet.getRetweetCount() > 0) {
            holder.tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
        } else {
            holder.tvRetweetCount.setText("");
        }

        // Load the profile image
        Glide.with(context)
                .load(tweet.getUser().profileImageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 25, 0))
                .into(holder.ivProfileImage);

        Glide.with(context)
                .load(tweet.media.getMediaUrl())
                .bitmapTransform(new RoundedCornersTransformation(context, 25, 0))
                .into(holder.ivMediaImage);
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }

    // Create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.ivProfileImage) public ImageButton ivProfileImage;
        @BindView(R.id.tvUserName) public TextView tvUsername;
        @BindView(R.id.tvCreatedAt) public TextView tvCreatedAt;
        @BindView(R.id.tvBody) public TextView tvBody;
        @BindView(R.id.tvScreenName) public TextView tvScreenName;
        @BindView(R.id.tvReplyCount) public TextView tvReplyCount;
        @BindView(R.id.tvRetweetCount) public TextView tvRetweetCount;
        @BindView(R.id.tvFavoriteCount) public TextView tvFavoriteCount;
        @BindView(R.id.ivMediaImage) public ImageView ivMediaImage;
        @BindView(R.id.ibRetweet) public ImageButton ibRetweet;
        @BindView(R.id.ibFavorite) public ImageButton ibFavorite;
        @BindView(R.id.vDivider) public View vDivider;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            client = TwitterApplication.getRestClient();
            itemView.setOnClickListener(this);
        }

        public void retweetTweet(final Tweet tweet, final int position) {
            client.retweet(tweet.getTweetId(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        Tweet newTweet = Tweet.fromJSON(response);
                        if (!newTweet.isRetweeted()) {
                            newTweet.setRetweeted(true);
                        }
                        if (newTweet.getRetweetCount() < tweet.getRetweetCount() + 1) {
                            newTweet.setRetweetCount(tweet.getRetweetCount() + 1);
                        }
                        mTweets.set(position, newTweet);
                        setRetweeted(newTweet);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, context.getString(R.string.unable_to_retweet), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    throwable.printStackTrace();
                    Toast.makeText(context, context.getString(R.string.unable_to_retweet), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    throwable.printStackTrace();
                    Toast.makeText(context, context.getString(R.string.unable_to_retweet), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    throwable.printStackTrace();
                    Toast.makeText(context, context.getString(R.string.unable_to_retweet), Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void unretweetTweet(final Tweet tweet, final int position) {
            client.unRetweet(tweet.getTweetId(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        Tweet newTweet = Tweet.fromJSON(response);
                        if (newTweet.isRetweeted()) {
                            newTweet.setRetweeted(false);
                        }
                        if (newTweet.getRetweetCount() > tweet.getRetweetCount() - 1) {
                            newTweet.setRetweetCount(tweet.getRetweetCount() - 1);
                        }
                        mTweets.set(position, newTweet);
                        setRetweeted(newTweet);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, context.getString(R.string.unable_to_unretweet), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    throwable.printStackTrace();
                    Toast.makeText(context, context.getString(R.string.unable_to_unretweet), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    throwable.printStackTrace();
                    Toast.makeText(context, context.getString(R.string.unable_to_unretweet), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    throwable.printStackTrace();
                    Toast.makeText(context, context.getString(R.string.unable_to_unretweet), Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void favoriteTweet(final Tweet tweet, final int position) {
            client.favoriteTweet(tweet.getTweetId(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        Tweet newTweet = Tweet.fromJSON(response);
                        mTweets.set(position, newTweet);
                        setFavorited(newTweet);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Toast.makeText(context, context.getString(R.string.unable_to_unfavorite_tweet), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Toast.makeText(context, context.getString(R.string.unable_to_unfavorite_tweet), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Toast.makeText(context, context.getString(R.string.unable_to_unfavorite_tweet), Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void unfavoriteTweet(final Tweet tweet, final int position) {
            client.unfavoriteTweet(tweet.getTweetId(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        Tweet newTweet = Tweet.fromJSON(response);
                        mTweets.set(position, newTweet);
                        setFavorited(newTweet);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Toast.makeText(context, context.getString(R.string.unable_to_favorite_tweet), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Toast.makeText(context, context.getString(R.string.unable_to_favorite_tweet), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Toast.makeText(context, context.getString(R.string.unable_to_favorite_tweet), Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void setFavorited(Tweet tweet) {
            // Set the tweet favorited status
            if(tweet.isFavorited()) {
                ibFavorite.setImageResource(R.drawable.ic_favorite);
            } else {
                ibFavorite.setImageResource(R.drawable.ic_unfavorite);
            }
            // Set the number of favorited tweets
            if(tweet.getFavoriteCount() > 0) {
                tvFavoriteCount.setText(String.valueOf(tweet.getFavoriteCount()));
            } else {
                tvFavoriteCount.setText("");
            }
        }

        public void setRetweeted(Tweet tweet) {
            // Set the tweet retweeted status
            if(tweet.isRetweeted()) {
                ibRetweet.setImageResource(R.drawable.ic_retweet);
            } else {
                ibRetweet.setImageResource(R.drawable.ic_unretweet);
            }
            // Set the number of retweeted tweets
            if(tweet.getRetweetCount() > 0) {
                tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
            } else {
                tvRetweetCount.setText("");
            }
        }

        @OnClick(R.id.ibMessage)
        public void goToReply() {
            final int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                final Tweet tweet = mTweets.get(position);
                Intent i = new Intent(context, ReplyActivity.class);
                i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                ((AppCompatActivity)context).startActivityForResult(i, REQUEST_CODE_REPLY);
            }
        }

        @OnClick(R.id.ibRetweet)
        public void goToRetweet() {
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                Tweet tweet = mTweets.get(position);
                if (tweet.isRetweeted()) {
                    unretweetTweet(tweet, position);
                } else {
                    retweetTweet(tweet, position);
                }
            }
        }

        @OnClick(R.id.ibFavorite)
        public void goToFavorite() {
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                Tweet tweet = mTweets.get(position);
                if(tweet.isFavorited()) {
                    unfavoriteTweet(tweet, position);
                } else {
                    favoriteTweet(tweet, position);
                }
            }
        }

        @OnClick(R.id.ivProfileImage)
        public void goToProfile() {
            final int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                Tweet tweet = mTweets.get(position);
                Intent i = new Intent(context, ProfileActivity.class);
                i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                (context).startActivity(i);
            }
        }

        @Override
        public void onClick(View v) {
            if(mListener != null) {
                // Get the position of the row element
                int position = getAdapterPosition();
                // Fire the listener callback
                mListener.onItemSelected(v, position);
            }
        }
    }
}
