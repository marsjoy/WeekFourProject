package mars_williams.tweetastic.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import mars_williams.tweetastic.R;
import mars_williams.tweetastic.TwitterApplication;
import mars_williams.tweetastic.models.User;
import mars_williams.tweetastic.networking.TwitterClient;


/**
 * Created by mars_williams on 10/16/17.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    List<User> mUsers;
    Context context;
    TwitterClient client;
    LayoutInflater inflater;
    ViewHolder viewHolder;
    private UserAdapterListener mListener;

    // Pass in the Users array in the constructor
    public UserAdapter(List<User> users, UserAdapterListener listener) {
        mUsers = users;
        mListener = listener;
    }

    // Constructor for onclicklistener
    public UserAdapter(Context context, AdapterView.OnItemClickListener listener, List<User> users) {
        inflater = LayoutInflater.from(context);
        this.mUsers = users;
    }

    // For each row, inflate layout and cache references into ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        if (inflater == null) {
            inflater = LayoutInflater.from(context);
        }
        View userView = inflater.inflate(R.layout.item_user, parent, false);
        viewHolder = new ViewHolder(userView);
        return viewHolder;
    }

    // Bind the values based on the position of the element
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the data according to position
        User user = mUsers.get(position);

        // Populate the views according to this data
        holder.tvUserName.setText(user.getName());
        holder.tvScreenName.setText(holder.tvScreenName.getContext()
                .getString(R.string.formatted_user_screen_name, user.getScreenName()));
        holder.tvTagline.setText(user.getTagLine());

        // Load the profile image
        Glide.with(context)
                .load(user.getProfileImageUrl())
                .bitmapTransform(new RoundedCornersTransformation(context, 25, 0))
                .placeholder(R.drawable.ic_profile_image_placeholder)
                .into(holder.ivProfileImage);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        mUsers.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<User> list) {
        mUsers.addAll(list);
        notifyDataSetChanged();
    }

    // Desfine an interface required by the ViewHolder
    public interface UserAdapterListener {
        public void onItemSelected(View view, int position);
    }

    // Create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.rlUserHeader)
        RelativeLayout rlUserHeader;
        @BindView(R.id.ivProfileImage)
        ImageView ivProfileImage;
        @BindView(R.id.tvUserName)
        TextView tvUserName;
        @BindView(R.id.tvScreenName)
        TextView tvScreenName;
        @BindView(R.id.tvTagline)
        TextView tvTagline;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            client = TwitterApplication.getRestClient();
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                // Get the position of the row element
                int position = getAdapterPosition();
                // Fire the listener callback
                mListener.onItemSelected(v, position);
            }
        }
    }
}

