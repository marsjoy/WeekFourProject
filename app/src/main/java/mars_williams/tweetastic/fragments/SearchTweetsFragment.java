package mars_williams.tweetastic.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import mars_williams.tweetastic.R;
import mars_williams.tweetastic.TwitterApplication;
import mars_williams.tweetastic.networking.TwitterClient;

/**
 * Created by mars_williams on 10/16/17.
 */

public class SearchTweetsFragment extends TweetsListFragment {

    TwitterClient client;

    public static SearchTweetsFragment getInstance(String searchQuery) {
        SearchTweetsFragment searchTweetsFragment = new SearchTweetsFragment();
        Bundle args = new Bundle();
        args.putString("search_query", searchQuery);
        searchTweetsFragment.setArguments(args);
        return searchTweetsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        populateSearchResults();
    }

    private void populateSearchResults() {
        showProgressBar();
        String searchQuery = getArguments().getString("search_query");
        client.searchTweets(searchQuery, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(getString(R.string.twitter_client), response.toString());
                try {
                    addItems(response.getJSONArray("statuses"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hideProgressBar();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e(getString(R.string.twitter_client), response.toString());
                addItems(response);
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getContext(), getString(R.string.unable_to_fetch_tweets), Toast.LENGTH_SHORT).show();
                Log.e(getString(R.string.twitter_client), responseString);
                throwable.printStackTrace();
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getContext(), getString(R.string.unable_to_fetch_tweets), Toast.LENGTH_SHORT).show();
                Log.e(getString(R.string.twitter_client), errorResponse.toString());
                throwable.printStackTrace();
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(getContext(), getString(R.string.unable_to_fetch_tweets), Toast.LENGTH_SHORT).show();
                Log.e(getString(R.string.twitter_client), errorResponse.toString());
                throwable.printStackTrace();
                hideProgressBar();
            }
        });
    }
}

