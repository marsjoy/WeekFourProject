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

public class FollowingFragment extends UsersListFragment {

    TwitterClient client;
    String nextCursor;

    public static FollowingFragment getInstance(String screenName) {
        FollowingFragment followingFragment = new FollowingFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screenName);
        followingFragment.setArguments(args);
        return followingFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        populateFollowing();
    }

    private void populateFollowing() {
        showProgressBar();
        String screenName = getArguments().getString("screen_name");
        client.getFollowers(screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(getString(R.string.twitter_client), response.toString());
                try {
                    addItems(response.getJSONArray("users"));
                    String cursor = response.getString("next_cursor");
                    if (cursor != null) {
                        userAdapter.setNextCursor(cursor);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hideProgressBar();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d(getString(R.string.twitter_client), response.toString());
                try {
                    addItems(response.getJSONArray(0));
                    userAdapter.setNextCursor(response.getString(1));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getContext(), getString(R.string.unable_to_fetch_tweets), Toast.LENGTH_SHORT).show();
                Log.d(getString(R.string.twitter_client), responseString);
                throwable.printStackTrace();
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getContext(), getString(R.string.unable_to_fetch_tweets), Toast.LENGTH_SHORT).show();
                Log.d(getString(R.string.twitter_client), errorResponse.toString());
                throwable.printStackTrace();
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(getContext(), getString(R.string.unable_to_fetch_tweets), Toast.LENGTH_SHORT).show();
                Log.d(getString(R.string.twitter_client), errorResponse.toString());
                throwable.printStackTrace();
                hideProgressBar();
            }
        });
    }

    public void fetchTimelineAsync(String cursor) {
        showProgressBar();
        String screenName = getArguments().getString("screen_name");
        client.getFollowingPage(cursor, screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(getString(R.string.twitter_client), response.toString());
                try {
                    addItems(response.getJSONArray("users"));
                    String cursor = response.getString("next_cursor");
                    if (cursor != null) {
                        userAdapter.setNextCursor(cursor);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                swipeContainer.setRefreshing(false);
                hideProgressBar();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                userAdapter.clear();
                try {
                    addItems(response.getJSONArray(0));
                    userAdapter.setNextCursor(response.getString(1));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                swipeContainer.setRefreshing(false);
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getContext(), getString(R.string.unable_to_fetch_tweets), Toast.LENGTH_SHORT).show();
                Log.d(getString(R.string.twitter_client), errorResponse.toString());
                throwable.printStackTrace();
                swipeContainer.setRefreshing(false);
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(getContext(), getString(R.string.unable_to_fetch_tweets), Toast.LENGTH_SHORT).show();
                Log.d(getString(R.string.twitter_client), errorResponse.toString());
                throwable.printStackTrace();
                swipeContainer.setRefreshing(false);
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getContext(), getString(R.string.unable_to_fetch_tweets), Toast.LENGTH_SHORT).show();
                Log.d(getString(R.string.twitter_client), responseString);
                throwable.printStackTrace();
                swipeContainer.setRefreshing(false);
                hideProgressBar();
            }
        });
    }

    @Override
    public void fetchNextPage(String cursor) {
        showProgressBar();
        String screenName = getArguments().getString("screen_name");
        client.getFollowingPage(cursor, screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(getString(R.string.twitter_client), response.toString());
                try {
                    addItems(response.getJSONArray("users"));
                    String cursor = response.getString("next_cursor");
                    if (cursor != null) {
                        userAdapter.setNextCursor(cursor);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hideProgressBar();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d(getString(R.string.twitter_client), response.toString());
                try {
                    addItems(response.getJSONArray(0));
                    userAdapter.setNextCursor(response.getString(1));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getContext(), getString(R.string.unable_to_fetch_tweets), Toast.LENGTH_SHORT).show();
                Log.d(getString(R.string.twitter_client), responseString);
                throwable.printStackTrace();
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getContext(), getString(R.string.unable_to_fetch_tweets), Toast.LENGTH_SHORT).show();
                Log.d(getString(R.string.twitter_client), errorResponse.toString());
                throwable.printStackTrace();
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(getContext(), getString(R.string.unable_to_fetch_tweets), Toast.LENGTH_SHORT).show();
                Log.d(getString(R.string.twitter_client), errorResponse.toString());
                throwable.printStackTrace();
                hideProgressBar();
            }
        });
    }
}
