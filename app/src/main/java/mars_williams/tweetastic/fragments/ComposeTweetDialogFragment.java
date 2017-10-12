package mars_williams.tweetastic.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import mars_williams.tweetastic.R;
import mars_williams.tweetastic.TwitterApplication;
import mars_williams.tweetastic.helpers.Utils;
import mars_williams.tweetastic.models.Tweet;
import mars_williams.tweetastic.models.User;
import mars_williams.tweetastic.networking.TwitterClient;

public class ComposeTweetDialogFragment extends DialogFragment {
    final private int maxLength = 140;
    @BindView(R.id.etNewTweet)
    EditText etNewTweet;
    @BindView(R.id.tvCharacterCount)
    TextView tvCharacterCount;
    @BindView(R.id.ivComposeProfileImage)
    ImageView ivComposeProfileImage;
    TwitterClient client;
    OnTweetComposedListener tweetComposed;
    private Unbinder unbinder;

    // Empty constructor required for DialogFragment
    public ComposeTweetDialogFragment() {
    }

    public static ComposeTweetDialogFragment newInstance() {
        ComposeTweetDialogFragment composeTweetDialogFragment = new ComposeTweetDialogFragment();
        return composeTweetDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compose, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        tweetComposed = (OnTweetComposedListener) context;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        client = TwitterApplication.getRestClient();

        // Show soft keyboard automatically and request focus to field
        etNewTweet.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        etNewTweet.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(maxLength)
        });

        User.getCurrentUser(new User.UserCallbackInterface() {
            @Override
            public void onUserAvailable(User currentUser) {
                Glide.with(getContext())
                        .load(currentUser.getProfileImageUrl())
                        .placeholder(R.drawable.ic_profile_image_placeholder)
                        .bitmapTransform(new RoundedCornersTransformation(ivComposeProfileImage.getContext(), 25, 0))
                        .into(ivComposeProfileImage);
            }
        });

        Utils.initCharacterCount(getContext(), etNewTweet, tvCharacterCount);
    }

    @OnClick(R.id.btnCloseCompose)
    public void closeCompose(View v) {
        // this should take you back to the timeline without posting the data
        Resources.Theme theme = getResources().newTheme();
        theme.applyStyle(R.style.AlertDialogCustom, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), theme))
                    .setIcon(R.drawable.pill_filled)
                    .setMessage(R.string.close_confirmation)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
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

    @OnClick(R.id.btSubmitNewTweet)
    public void postTweet(View v) {
        String newTweetText = etNewTweet.getText().toString();
        client.postTweet(newTweetText, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    // Get the tweet
                    Tweet tweet = Tweet.fromJSON(response);
                    tweetComposed.onTweetComposed(tweet);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dismiss();
                Toast.makeText(getContext(), "Submitted tweet", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (errorResponse != null) {
                    Log.d("DEBUG", errorResponse.toString());
                } else {
                    Log.d("DEBUG", "null error");
                }
            }
        });
    }

    public interface OnTweetComposedListener {
        void onTweetComposed(Tweet tweet);
    }
}
