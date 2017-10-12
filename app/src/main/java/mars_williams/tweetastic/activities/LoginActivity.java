package mars_williams.tweetastic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;

import com.codepath.oauth.OAuthLoginActionBarActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import mars_williams.tweetastic.R;
import mars_williams.tweetastic.networking.TwitterClient;

public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    // Click handler method for the button used to start OAuth flow
    // Uses the client to initiate OAuth authorization
    // This should be tied to a button used to login
    @OnClick(R.id.ibLogin)
    public void loginToRest(View v) {
        getClient().connect();
    }

    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    // OAuth authenticated successfully, launch primary authenticated activity
    // i.e Display application "homepage"
    @Override
    public void onLoginSuccess() {
        Intent i = new Intent(this, TimelineActivity.class);
        startActivity(i);
    }

    // OAuth authentication flow failed, handle the error
    // i.e Display an error dialog or toast
    @Override
    public void onLoginFailure(Exception e) {
        e.printStackTrace();
    }

}
