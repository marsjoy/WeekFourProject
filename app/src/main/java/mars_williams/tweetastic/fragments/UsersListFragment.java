package mars_williams.tweetastic.fragments;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import mars_williams.tweetastic.R;
import mars_williams.tweetastic.adapters.UserAdapter;
import mars_williams.tweetastic.listeners.EndlessRecyclerViewScrollListener;
import mars_williams.tweetastic.models.User;
import mars_williams.tweetastic.recievers.InternetCheckReceiver;

/**
 * Created by mars_williams on 10/16/17.
 */

public class UsersListFragment extends Fragment implements UserAdapter.UserAdapterListener {

    @BindView(R.id.rvUsers)
    public RecyclerView rvUsers;
    public UserAdapter userAdapter;
    public ArrayList<User> users;
    public SwipeRefreshLayout swipeContainer;
    InternetCheckReceiver broadcastReceiver;
    private Unbinder unbinder;
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        broadcastReceiver = new InternetCheckReceiver(rvUsers);
        this.getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onStop() {
        this.getActivity().unregisterReceiver(broadcastReceiver);
        super.onStop();
    }

    // Inflation happens inside onCreateView
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        // Init the arraylist (data source)
        users = new ArrayList<>();
        // Construct the adapter from this datasource
        userAdapter = new UserAdapter(users, (UserAdapter.UserAdapterListener) this);
        // RecyclerView setup (layout manager, use adapter)
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        rvUsers.setLayoutManager(linearLayoutManager);
        // Set the adapter
        rvUsers.setAdapter(userAdapter);

        // Initialize the infinite pagination
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                fetchNextPage(userAdapter.getNextCursor());
            }
        };
        rvUsers.addOnScrollListener(scrollListener);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh the list here
                fetchTimelineAsync("0");
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(
                R.color.twitter_blue,
                R.color.twitter_light_blue,
                R.color.twitter_dark_gray,
                R.color.twitter_light_gray);

        return view;
    }

    public void addItems(JSONArray response) {
        List<User> usersCollection = User.fromJSONArray(response);
        userAdapter.addAll(usersCollection);
    }

    public void fetchTimelineAsync(String cursor) {
    }

    public void fetchNextPage(String cursor) {
    }

    @Override
    public void onItemSelected(View view, int position) {
        User user = users.get(position);
        ((UserSelectedListener) getActivity()).onUserSelected(user, position);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public interface UserSelectedListener {
        // Handle user selection
        void onUserSelected(User user, int position);
    }
}
