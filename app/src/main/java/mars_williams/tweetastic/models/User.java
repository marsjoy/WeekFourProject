package mars_williams.tweetastic.models;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.orm.SugarRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import mars_williams.tweetastic.TwitterApplication;
import mars_williams.tweetastic.networking.TwitterClient;

@Parcel(analyze = User.class)
public class User extends SugarRecord<User> {

    // List the attributes
    public String name;
    public long twitterUserId;
    public String screenName;
    public String profileImageUrl;
    public String tagLine;
    public int followersCount;
    public int followingCount;
    public long nextCursor;

    public User() {
    }

    public User(JSONObject object) throws JSONException {
        this.name = object.getString("name");
        this.twitterUserId = object.getLong("id");
        this.screenName = object.getString("screen_name");
        this.profileImageUrl = object.getString("profile_image_url");
        this.tagLine = object.getString("description");
        this.followersCount = object.getInt("followers_count");
        this.followingCount = object.getInt("friends_count");

    }

    public User(String name, long twitterUserId, String screenName, String profileImageUrl, String tagLine, int followersCount, int followingCount) {
        this.name = name;
        this.twitterUserId = twitterUserId;
        this.screenName = screenName;
        this.profileImageUrl = profileImageUrl;
        this.tagLine = tagLine;
        this.followersCount = followersCount;
        this.followingCount = followingCount;
    }


    // Deserialize the JSON
    public static User fromJSON(JSONObject json) throws JSONException {
        User user = new User();

        // Extract and fill values
        user.name = json.getString("name");
        user.twitterUserId = json.getLong("id");
        user.screenName = json.getString("screen_name");
        user.profileImageUrl = json.getString("profile_image_url");
        user.tagLine = json.getString("description");
        user.followersCount = json.getInt("followers_count");
        user.followingCount = json.getInt("friends_count");
        return user;
    }

    // Deserialize the JSON
    public static ArrayList<User> fromJSONUsers(JSONArray json) throws JSONException {
        return fromJSONArray(json.getJSONArray(0));
    }

    // Deserialize the JSON
    public static long getNextCursor(JSONArray json) throws JSONException {
        JSONArray users = json.getJSONArray(0);
        for (int index = 0; index < users.length(); ++index) {
            JSONObject user = users.getJSONObject(index);
            Long nextCursor = user.getLong("next_cursor");
            if (nextCursor != null) {
                return nextCursor;
            }
        }
        return 0;
    }

    public static void getCurrentUser(final UserCallbackInterface handler) {
        TwitterClient client = TwitterApplication.getRestClient();
        client.verifyCredentials(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    handler.onUserAvailable(User.fromJSON(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    public static ArrayList<User> fromJSONArray(JSONArray array) {
        ArrayList<User> users = new ArrayList<>();
        for (int index = 0; index < array.length(); ++index) {
            try {
                User user = User.fromJSON(array.getJSONObject(index));
                if (user != null) {
                    users.add(user);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return users;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTwitterUserId() {
        return twitterUserId;
    }

    public void setTwitterUserId(long twitterUserId) {
        this.twitterUserId = twitterUserId;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getTagLine() {
        return tagLine;
    }

    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public interface UserCallbackInterface {
        void onUserAvailable(User currentUser);
    }
}

