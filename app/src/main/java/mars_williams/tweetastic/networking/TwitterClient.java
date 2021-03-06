package mars_williams.tweetastic.networking;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import mars_williams.tweetastic.R;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/scribejava/scribejava/tree/master/scribejava-apis/src/main/java/com/github/scribejava/apis
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
    public static final BaseApi REST_API_INSTANCE = TwitterApi.instance(); // Changed
    public static final String REST_URL = "https://api.twitter.com/1.1"; // Changed
    public static final String REST_CONSUMER_KEY = "6KHGTVoonI4degGmaQMQi967r";
    public static final String REST_CONSUMER_SECRET = "kE88YLHhl9rmh7VObKSLTy9eIInhSTaJFtIfc0EcxVvnjUMCaK";

    // Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
    public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

    // See https://developer.chrome.com/multidevice/android/intents
    public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

    public TwitterClient(Context context) {
        super(context,
                REST_API_INSTANCE,
                REST_URL,
                REST_CONSUMER_KEY,
                REST_CONSUMER_SECRET,
                String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
                        context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
    }

    public void getHomeTimelinePage(long max_id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", "25");
        params.put("since_id", 1);
        if (max_id > 1) { // for consecutive calls to this endpoint
            params.put("max_id", max_id); // would we want this to update based on the last id we got?
        }
        client.get(apiUrl, params, handler);
    }

    public void getMentionsTimelinePage(long max_id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", "25");
        params.put("since_id", 1);
        if (max_id > 1) { // for consecutive calls to this endpoint
            params.put("max_id", max_id); // would we want this to update based on the last id we got?
        }
        client.get(apiUrl, params, handler);
    }

    public void getUserTimelinePage(long max_id, String screenName, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", "25");
        params.put("since_id", 1);
        params.put("screen_name", screenName);
        if (max_id > 1) { // for consecutive calls to this endpoint
            params.put("max_id", max_id); // would we want this to update based on the last id we got?
        }
        client.get(apiUrl, params, handler);
    }

    public void searchTweets(String searchQuery, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("search/tweets.json");
        RequestParams params = new RequestParams();
        params.put("q", searchQuery);
        client.get(apiUrl, params, handler);
    }

    public void getFollowersPage(String cursor, String screenName, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("followers/list.json");
        RequestParams params = new RequestParams();
        params.put("screen_name", screenName);
        params.put("cursor", cursor);
        params.put("include_user_entities", false);
        params.put("skip_status", true);
        client.get(apiUrl, params, handler);
    }

    public void getFollowingPage(String cursor, String screenName, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("friends/list.json");
        RequestParams params = new RequestParams();
        params.put("screen_name", screenName);
        params.put("cursor", cursor);
        params.put("include_user_entities", false);
        params.put("skip_status", true);
        client.get(apiUrl, params, handler);
    }

    public void getFollowing(String screenName, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("friends/list.json");
        RequestParams params = new RequestParams();
        params.put("screen_name", screenName);
        params.put("include_user_entities", false);
        params.put("skip_status", true);
        client.get(apiUrl, params, handler);
    }

    public void getFollowers(String screenName, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("followers/list.json");
        RequestParams params = new RequestParams();
        params.put("screen_name", screenName);
        params.put("include_user_entities", false);
        params.put("skip_status", true);
        client.get(apiUrl, params, handler);
    }

    public void verifyCredentials(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("account/verify_credentials.json");
        client.get(apiUrl, null, handler);
    }

    public void getUser(String screenName, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("users/lookup.json");
        RequestParams params = new RequestParams();
        params.put("screen_name", screenName);
        client.get(apiUrl, params, handler);
    }

    public void postTweet(String status, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", status);
        getClient().post(apiUrl, params, handler);
    }

    public void retweet(long id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/retweet.json");
        RequestParams params = new RequestParams();
        params.put("id", id);
        client.post(apiUrl, params, handler);
    }

    public void unRetweet(long id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/unretweet.json");
        RequestParams params = new RequestParams();
        params.put("id", id);
        client.post(apiUrl, params, handler);
    }

    public void replyTweet(String message, long uid, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", message);
        params.put("in_reply_to_status_id", uid);
        client.post(apiUrl, params, handler);
    }

    public void favoriteTweet(long id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("favorites/create.json");
        RequestParams params = new RequestParams();
        params.put("id", id);
        client.post(apiUrl, params, handler);
    }

    public void unfavoriteTweet(long id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("favorites/destroy.json");
        RequestParams params = new RequestParams();
        params.put("id", id);
        client.post(apiUrl, params, handler);
    }
}
