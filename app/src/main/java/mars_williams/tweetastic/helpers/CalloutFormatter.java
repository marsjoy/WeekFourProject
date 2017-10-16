package mars_williams.tweetastic.helpers;

/**
 * Created by mars_williams on 10/16/17.
 */

import android.app.Activity;
import android.content.Context;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import mars_williams.tweetastic.R;

public class CalloutFormatter extends ClickableSpan {
    Context mContext;
    Activity mActivity;

    public CalloutFormatter(Context context, Activity activity) {
        super();
        mContext = context;
        mActivity = activity;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        if (ds != null) {
            ds.setColor(mContext.getColor(R.color.twitter_tan));
        }
    }

    @Override
    public void onClick(View widget) {
        TextView tv = (TextView) widget;
        Spanned s = (Spanned) tv.getText();
        int start = s.getSpanStart(this);
        int end = s.getSpanEnd(this);
        String callout = s.subSequence(start + 1, end).toString();

        // start profile activity here
//        Intent i = new Intent(context, activity.getClass());
//        i.putExtra(activity.getLocalClassName(), Parcels.wrap(callout));
//        (context).startActivity(i);
    }
}

