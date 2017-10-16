package mars_williams.tweetastic.helpers;

/**
 * Created by mars_williams on 10/16/17.
 */

import android.content.Context;
import android.content.Intent;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import mars_williams.tweetastic.R;
import mars_williams.tweetastic.activities.SearchActivity;

public class HashtagFormatter extends ClickableSpan {
    Context mContext;
    TextPaint textPaint;

    public HashtagFormatter(Context context) {
        super();
        mContext = context;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        textPaint = ds;
        if (ds != null) {
            ds.setColor(ds.linkColor);
            ds.setColor(mContext.getColor(R.color.twitter_tan));
        }
    }

    @Override
    public void onClick(View widget) {
        TextView tv = (TextView) widget;
        Spanned s = (Spanned) tv.getText();
        int start = s.getSpanStart(this);
        int end = s.getSpanEnd(this);
        String hashtag = s.subSequence(start + 1, end).toString();

        // start search activity here
        Intent i = new Intent(mContext, SearchActivity.class);
        i.putExtra(mContext.getString(R.string.search_query), hashtag);
        (mContext).startActivity(i);
    }
}
