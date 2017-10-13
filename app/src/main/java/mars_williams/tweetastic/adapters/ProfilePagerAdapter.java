package mars_williams.tweetastic.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import mars_williams.tweetastic.R;
import mars_williams.tweetastic.fragments.FavoritesFragment;
import mars_williams.tweetastic.fragments.PhotosFragment;
import mars_williams.tweetastic.fragments.TweetsListFragment;
import mars_williams.tweetastic.fragments.UserTimelineFragment;

/**
 * Created by mars_williams on 10/12/17.
 */

public class ProfilePagerAdapter extends FragmentStatePagerAdapter {

    SparseArray<TweetsListFragment> registeredFragments = new SparseArray<TweetsListFragment>();
    private String tabTitles[];
    private Context context;

    public ProfilePagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        tabTitles = new String[] {
                this.context.getString(R.string.home),
                this.context.getString(R.string.mentions)};
    }

    // Return the total # of fragments
    @Override
    public int getCount() {
        return 3;
    }

    // Return the fragment to use depending on the position
    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            return new UserTimelineFragment();
        } else if (position == 1) {
            return new PhotosFragment();
        } else if (position == 2) {
        return new FavoritesFragment();
        } else {
            return null;
        }
    }

    // Return title based on position
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        TweetsListFragment fragment =  (TweetsListFragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public TweetsListFragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}