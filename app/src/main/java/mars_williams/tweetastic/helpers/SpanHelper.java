package mars_williams.tweetastic.helpers;

import android.content.Context;
import android.text.SpannableString;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mars_williams on 10/16/17.
 */

public class SpanHelper {

    public static ArrayList<int[]> getHashtagSpans(String body) {
        ArrayList<int[]> spans = matchSpans(body, "[#]+[A-Za-z0-9-_]+\\b");
        return spans;
    }

    public static ArrayList<int[]> getCalloutSpans(String body) {
        ArrayList<int[]> spans = matchSpans(body, "[@]+[A-Za-z0-9-_]+\\b");
        return spans;
    }

    public static ArrayList<int[]> matchSpans(String body, String regexString) {
        ArrayList<int[]> spans = new ArrayList<int[]>();

        Pattern pattern = Pattern.compile(regexString);
        Matcher matcher = pattern.matcher(body);

        // Check all occurrences
        while (matcher.find()) {
            int[] currentSpan = new int[2];
            currentSpan[0] = matcher.start();
            currentSpan[1] = matcher.end();
            spans.add(currentSpan);
        }
        return spans;
    }

    public static void setSpanHashtag(SpannableString styledBody, ArrayList<int[]> hashtagSpans, Context context) {
        for (int i = 0; i < hashtagSpans.size(); i++) {
            SpanRange spanRange = setSpan(i, hashtagSpans);

            styledBody.setSpan(new HashtagFormatter(context),
                    (int) spanRange.start,
                    (int) spanRange.end, 0);
        }
    }

    public static void setSpanCallout(SpannableString styledBody, ArrayList<int[]> calloutSpans, Context context) {
        for (int i = 0; i < calloutSpans.size(); i++) {
            SpanRange spanRange = setSpan(i, calloutSpans);

            styledBody.setSpan(new CalloutFormatter(context),
                    (int) spanRange.start,
                    (int) spanRange.end, 0);
        }
    }

    public static SpanRange setSpan(int index, ArrayList<int[]> spans) {
        int[] span = spans.get(index);
        int start = span[0];
        int end = span[1];

        return new SpanRange(start, end);
    }

    public static class SpanRange<Integer> {
        public final Integer start;
        public final Integer end;

        public SpanRange(Integer start, Integer end) {
            this.start = start;
            this.end = end;
        }
    }
}