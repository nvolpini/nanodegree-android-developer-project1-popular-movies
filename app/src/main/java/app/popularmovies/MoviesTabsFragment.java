package app.popularmovies;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import app.popularmovies.model.MoviesListFilter;

/**
 * Use the {@link MoviesTabsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoviesTabsFragment extends Fragment {

	private static final Logger log = LoggerFactory.getLogger(MoviesTabsFragment.class);

	private static final String SELECTED_TAB_KEY = "selected_tab";
	private static final String TWO_PANE_LAYOUT_KEY = "two_pane";

	private ViewPager viewPager;

	private boolean twoPaneLayout = false;

	private int selectedTab = 0;

	public MoviesTabsFragment() {
		// Required empty public constructor
	}

	public static MoviesTabsFragment newInstance() {
		MoviesTabsFragment fragment = new MoviesTabsFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
		 	selectedTab = savedInstanceState.getInt(SELECTED_TAB_KEY);
			twoPaneLayout = savedInstanceState.getInt(TWO_PANE_LAYOUT_KEY) == 1;
		}

		log.trace("onCreate, twoPane: {}, selectedTab: {}", twoPaneLayout, selectedTab);

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt(SELECTED_TAB_KEY, viewPager.getCurrentItem());

		outState.putInt(TWO_PANE_LAYOUT_KEY, twoPaneLayout ? 1 : 0);

		log.trace("onSave, tab: {}, twoPane: {}",viewPager.getCurrentItem(), twoPaneLayout);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		// Inflate the layout for this fragment
		View root = inflater.inflate(R.layout.fragment_movies_tabs, container, false);

		viewPager = (ViewPager) root.findViewById(R.id.viewPager);

		setupViewPager(viewPager);

		TabLayout tabs = (TabLayout) root.findViewById(R.id.tabs);
		tabs.setupWithViewPager(viewPager);

		viewPager.setCurrentItem(selectedTab);

		return root;
	}

	private void setupViewPager(ViewPager viewPager) {

		log.trace("setup pager, twoPane: {}", twoPaneLayout);

		int cols = twoPaneLayout ? 3 : 2;

		viewPager.setOffscreenPageLimit(2);

		MyAdapter adapter = new MyAdapter(getChildFragmentManager());

		adapter.addFragment(MoviesListFragment.newInstance(MoviesListFilter.POPULAR, cols), getString(R.string.popular));
		adapter.addFragment(MoviesListFragment.newInstance(MoviesListFilter.TOP_RATED, cols), getString(R.string.topRated));
		adapter.addFragment(MoviesListFragment.newInstance(MoviesListFilter.FAVORITES, cols), getString(R.string.favorites));

		viewPager.setAdapter(adapter);



	}

	static class MyAdapter extends FragmentPagerAdapter {
		private final List<Fragment> mFragments = new ArrayList<>();
		private final List<String> mFragmentTitles = new ArrayList<>();

		public MyAdapter(FragmentManager fm) {
			super(fm);
		}

		public void addFragment(Fragment fragment, String title) {
			mFragments.add(fragment);
			mFragmentTitles.add(title);
		}

		@Override
		public Fragment getItem(int position) {
			return mFragments.get(position);
		}

		@Override
		public int getCount() {
			return mFragments.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mFragmentTitles.get(position);
		}
	}

	public void setTwoPaneLayout(boolean twoPaneLayout) {
		this.twoPaneLayout = twoPaneLayout;
	}
}
