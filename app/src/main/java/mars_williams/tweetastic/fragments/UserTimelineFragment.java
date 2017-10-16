package mars_williams.tweetastic.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import mars_williams.tweetastic.R;
import mars_williams.tweetastic.TwitterApplication;
import mars_williams.tweetastic.networking.TwitterClient;

/**
 * Created by mars_williams on 10/12/17.
 */

public class UserTimelineFragment extends TweetsListFragment {

    TwitterClient client;
    String screenName;

    public static UserTimelineFragment newInstance(String screenName) {
        UserTimelineFragment userTimelineFragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screenName);
        userTimelineFragment.setArguments(args);
        return userTimelineFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        populateTimeline();
    }

    private void populateTimeline() {
        screenName = getArguments().getString("screen_name");
        client.getUserTimelinePage(0, screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(getString(R.string.twitter_client), response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d(getString(R.string.twitter_client), response.toString());
                addItems(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getContext(), getString(R.string.unable_to_fetch_tweets), Toast.LENGTH_SHORT).show();
                Log.d(getString(R.string.twitter_client), responseString);
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getContext(), getString(R.string.unable_to_fetch_tweets), Toast.LENGTH_SHORT).show();
                Log.d(getString(R.string.twitter_client), errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(getContext(), getString(R.string.unable_to_fetch_tweets), Toast.LENGTH_SHORT).show();
                Log.d(getString(R.string.twitter_client), errorResponse.toString());
                throwable.printStackTrace();
            }
        });
    }

    @Override
    public void fetchTimelineAsync(int page) {
        client.getUserTimelinePage(page, screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(getString(R.string.twitter_client), response.toString());
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                tweetAdapter.clear();
                addItems(response);
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getContext(), getString(R.string.unable_to_fetch_tweets), Toast.LENGTH_SHORT).show();
                Log.d(getString(R.string.twitter_client), errorResponse.toString());
                throwable.printStackTrace();
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(getContext(), getString(R.string.unable_to_fetch_tweets), Toast.LENGTH_SHORT).show();
                Log.d(getString(R.string.twitter_client), errorResponse.toString());
                throwable.printStackTrace();
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getContext(), getString(R.string.unable_to_fetch_tweets), Toast.LENGTH_SHORT).show();
                Log.d(getString(R.string.twitter_client), responseString);
                throwable.printStackTrace();
                swipeContainer.setRefreshing(false);
            }
        });
    }

    @Override
    public void fetchNextPage(long max_id) {
        client.getUserTimelinePage(max_id, screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(getString(R.string.twitter_client), response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d(getString(R.string.twitter_client), response.toString());
                addItems(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getContext(), getString(R.string.unable_to_fetch_tweets), Toast.LENGTH_SHORT).show();
                Log.d(getString(R.string.twitter_client), responseString);
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getContext(), getString(R.string.unable_to_fetch_tweets), Toast.LENGTH_SHORT).show();
                Log.d(getString(R.string.twitter_client), errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(getContext(), getString(R.string.unable_to_fetch_tweets), Toast.LENGTH_SHORT).show();
                Log.d(getString(R.string.twitter_client), errorResponse.toString());
                throwable.printStackTrace();
            }
        });
    }
}

