package mars_williams.tweetastic.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import mars_williams.tweetastic.R;
import mars_williams.tweetastic.TwitterApplication;
import mars_williams.tweetastic.models.Tweet;
import mars_williams.tweetastic.networking.TwitterClient;

/**
 * Created by mars_williams on 10/12/17.
 */

public class ReplyActivity extends AppCompatActivity {

    @BindView(R.id.tvReplyingTo)
    TextView tvReplyingTo;
    @BindView(R.id.tvCharacterCount) TextView tvCharacterCount;
    @BindView(R.id.etNewTweet)
    EditText etNewTweet;
    @BindView(R.id.btnCloseReply)
    Button btnCloseReply;

    private TwitterClient client;
    private Tweet tweet;
    final private int maxLength = 140;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        ButterKnife.bind(this);
        client = TwitterApplication.getRestClient();

        // Unwrap the tweet from the intent
        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        // Set up the action bar
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.action_bar, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.actionbar_title);
        mTitleTextView.setText(getString(R.string.reply));

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

        // Populate the @ replying to
        tvReplyingTo.setText(getString(R.string.replying_to, tweet.getUser().getScreenName()));

        // Set the character count on the body edittext
        initCharacterCount();
    }

    public void initCharacterCount() {
        etNewTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCharacterCount.setText(String.valueOf(maxLength - s.length()));
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    @OnClick(R.id.btnCloseReply)
    public void closeReply(View v) {
        // this should take you back to the timeline without posting the data
        Resources.Theme theme = getResources().newTheme();
        theme.applyStyle(R.style.AlertDialogCustom, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, theme))
                    .setIcon(R.drawable.pill_filled)
                    .setMessage(R.string.close_confirmation)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            dialog.show();
            Button nButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            nButton.setTextColor(getResources().getColor(R.color.twitter_red, null));
            Button pButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            pButton.setTextColor(getResources().getColor(R.color.twitter_red_light, null));
        }
    }

    @OnClick(R.id.btSubmitNewTweetReply)
    public void submitReply() {
        // Construct the reply
        String newReplyText = getString(R.string.reply_text, tweet.getUser().getScreenName(), etNewTweet.getText().toString());
        // Send the request and parameters to the endpoint
        client.replyTweet(newReplyText, etNewTweet.getId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    // Deserialize the tweet and send it and its location back to TimeLine activity
                    tweet = Tweet.fromJSON(response);
                    Intent i = new Intent(ReplyActivity.this, TimelineActivity.class);
                    i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                    setResult(RESULT_OK, i);
                    startActivity(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ReplyActivity.this, getString(R.string.failed_to_submit_reply), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(ReplyActivity.this, getString(R.string.failed_to_submit_reply), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(ReplyActivity.this, getString(R.string.failed_to_submit_reply), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(ReplyActivity.this, getString(R.string.failed_to_submit_reply), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

