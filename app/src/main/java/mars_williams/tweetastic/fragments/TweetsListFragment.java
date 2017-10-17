package mars_williams.tweetastic.fragments;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import mars_williams.tweetastic.R;
import mars_williams.tweetastic.adapters.TweetAdapter;
import mars_williams.tweetastic.listeners.EndlessRecyclerViewScrollListener;
import mars_williams.tweetastic.models.Tweet;
import mars_williams.tweetastic.recievers.InternetCheckReceiver;

public class TweetsListFragment extends Fragment implements TweetAdapter.TweetAdapterListener {

    @BindView(R.id.rvTweets)
    public RecyclerView rvTweets;
    public TweetAdapter tweetAdapter;
    public ArrayList<Tweet> tweets;
    public SwipeRefreshLayout swipeContainer;
    InternetCheckReceiver broadcastReceiver;
    private Unbinder unbinder;
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        broadcastReceiver = new InternetCheckReceiver(rvTweets);
        this.getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onStop() {
        this.getActivity().unregisterReceiver(broadcastReceiver);
        super.onStop();
    }

    // Inflation happens inside onCreateView
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweets_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        // Init the arraylist (data source)
        tweets = new ArrayList<>();
        // Construct the adapter from this datasource
        tweetAdapter = new TweetAdapter(tweets, (TweetAdapter.TweetAdapterListener) this);
        // RecyclerView setup (layout manager, use adapter)
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        rvTweets.setLayoutManager(linearLayoutManager);
        // Set the adapter
        rvTweets.setAdapter(tweetAdapter);

        // Initialize the infinite pagination
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Tweet lastTweet = tweets.get(tweets.size() - 1);
                fetchNextPage(lastTweet.getTweetId() - 1);
            }
        };
        rvTweets.addOnScrollListener(scrollListener);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh the list here
                fetchTimelineAsync(0);
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(
                R.color.twitter_blue,
                R.color.twitter_light_blue,
                R.color.twitter_dark_gray,
                R.color.twitter_light_gray);

        return view;
    }

    public void addItems(JSONArray response) {
        List<Tweet> tweetsCollection = Tweet.fromJSONArray(response);
        tweetAdapter.addAll(tweetsCollection);
    }

    public void fetchTimelineAsync(int page) {
    }

    // Append the next page of data into the adapter
    public void fetchNextPage(long max_id) {
    }

    @Override
    public void onItemSelected(View view, int position) {
        Tweet tweet = tweets.get(position);
        ((TweetSelectedListener) getActivity()).onTweetSelected(tweet, position);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void showProgressBar() {
        ((LoadingProgressDialog) getActivity()).showProgressBar();
    }

    public void hideProgressBar() {
        ((LoadingProgressDialog) getActivity()).hideProgressBar();
    }

    public interface LoadingProgressDialog {
        // Show/hide progress dialog
        void showProgressBar();

        void hideProgressBar();
    }

    public interface TweetSelectedListener {
        // Handle tweet selection
        void onTweetSelected(Tweet tweet, int position);
    }

}
