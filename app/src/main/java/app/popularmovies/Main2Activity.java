package app.popularmovies;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import app.popularmovies.model.Movie;
import app.popularmovies.model.SearchParams;
import app.popularmovies.service.IMovieSearch;

public class Main2Activity extends AppCompatActivity implements MoviesFragment.OnListFragmentInteractionListener {

	private static final Logger log = LoggerFactory.getLogger(Main2Activity.class);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);


		ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
		if (viewPager != null) {
			setupViewPager(viewPager);
		}



		TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
		tabLayout.setupWithViewPager(viewPager);
	}


	static SearchParams popularParams = new SearchParams();
	static SearchParams topRatedParams = new SearchParams();
	static SearchParams favoriteParams = new SearchParams();

	static {
		popularParams.setSortBy(IMovieSearch.SORT_BY_POPULARITY);
		topRatedParams.setSortBy(IMovieSearch.SORT_BY_RATING);
		favoriteParams.setSortBy(IMovieSearch.SORT_BY_FAVORITES);
	}

	private void setupViewPager(ViewPager viewPager) {
		Adapter adapter = new Adapter(getSupportFragmentManager());
		adapter.addFragment(MoviesFragment.newInstance(2,popularParams), "Popular");
		adapter.addFragment(MoviesFragment.newInstance(2,topRatedParams), "Top Rated");
		adapter.addFragment(MoviesFragment.newInstance(2, favoriteParams), "Favorites");
		viewPager.setAdapter(adapter);
	}

	@Override
	public void onListFragmentInteraction(Movie movie) {
		log.trace("iteracao, movie: {}",movie.getOriginalTitle());
	}


	static class Adapter extends FragmentPagerAdapter {
		private final List<Fragment> mFragments = new ArrayList<>();
		private final List<String> mFragmentTitles = new ArrayList<>();

		public Adapter(FragmentManager fm) {
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
