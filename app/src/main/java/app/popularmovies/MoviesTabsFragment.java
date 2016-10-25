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

	private static final String PAGER_SELECTED_POSITION_KEY = "selected_position";

	private ViewPager viewPager;

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

		log.trace("onCreate, pos: {}", savedInstanceState != null ? savedInstanceState.getInt(PAGER_SELECTED_POSITION_KEY) : null);

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt(PAGER_SELECTED_POSITION_KEY, viewPager.getCurrentItem());

		log.trace("onSave, pos: {}",viewPager.getCurrentItem());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		log.trace("onCreateView, pos: {}", savedInstanceState != null ? savedInstanceState.getInt(PAGER_SELECTED_POSITION_KEY) : null);


		// Inflate the layout for this fragment
		View root = inflater.inflate(R.layout.fragment_movies_tabs, container, false);

		viewPager = (ViewPager) root.findViewById(R.id.viewPager);

		setupViewPager(viewPager);

		/*
		PagerTabStrip pagerTabStrip = (PagerTabStrip) root.findViewById(R.id.pagerTabStrip);
		//pagerTabStrip.setDrawFullUnderline(true);
		//pagerTabStrip.setGravity(Gravity.LEFT);
		pagerTabStrip.setTextColor(Color.WHITE);
		pagerTabStrip.setTabIndicatorColor(Color.RED);
		*/

		TabLayout tabs = (TabLayout) root.findViewById(R.id.tabs);
		tabs.setupWithViewPager(viewPager);

		if (savedInstanceState != null) {
			viewPager.setCurrentItem(savedInstanceState.getInt(PAGER_SELECTED_POSITION_KEY));
		}

		return root;
	}

	private void setupViewPager(ViewPager viewPager) {

		viewPager.setOffscreenPageLimit(2);

		MyAdapter adapter = new MyAdapter(getChildFragmentManager());

		adapter.addFragment(MoviesListFragment.newInstance(MoviesListFilter.POPULAR), getString(R.string.popular));
		adapter.addFragment(MoviesListFragment.newInstance(MoviesListFilter.TOP_RATED), getString(R.string.topRated));
		adapter.addFragment(MoviesListFragment.newInstance(MoviesListFilter.FAVORITES), getString(R.string.favorites));

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

}
