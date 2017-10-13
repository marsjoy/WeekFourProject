package mars_williams.tweetastic.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import mars_williams.tweetastic.R;
import mars_williams.tweetastic.TwitterApplication;
import mars_williams.tweetastic.models.Tweet;
import mars_williams.tweetastic.networking.TwitterClient;

public class TweetPhotoAdapter extends RecyclerView.Adapter<TweetPhotoAdapter.ViewHolder> {

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
    public TweetPhotoAdapter(List<Tweet> tweets, TweetAdapterListener listener) {
        mTweets = tweets;
        mListener = listener;
    }

    // Constructor for onclicklistener
    public TweetPhotoAdapter(Context context, AdapterView.OnItemClickListener listener, List<Tweet> tweets) {
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
        @BindView(R.id.ivMediaImage) public ImageView ivMediaImage;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            client = TwitterApplication.getRestClient();
            itemView.setOnClickListener(this);
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
