package app.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import app.popularmovies.model.Movie;
import app.popularmovies.model.Review;
import app.popularmovies.model.Video;

public class Main2Activity extends AppCompatActivity implements
		MoviesFragment.OnListFragmentInteractionListener
		,MovieDetailsFragment.OnMovieDetailsInteractionListener {

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

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		//log.trace("saving state...");

		super.onSaveInstanceState(outState);

	}

	private void setupViewPager(ViewPager viewPager) {
		Adapter adapter = new Adapter(getSupportFragmentManager());
		adapter.addFragment(MoviesListFragment.newInstance(MoviesListFilter.POPULAR), getString(R.string.popular));
		adapter.addFragment(MoviesListFragment.newInstance(MoviesListFilter.TOP_RATED), getString(R.string.topRated));
		adapter.addFragment(MoviesListFragment.newInstance(MoviesListFilter.FAVORITES), getString(R.string.favorites));
		viewPager.setAdapter(adapter);
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


	@Override
	public void onListFragmentInteraction(Movie movie) {
		log.trace("iteracao, movie: {}",movie.getOriginalTitle());

		Intent intent = MovieDetailsActivity.newIntent(this,movie);

		startActivity(intent);
	}

	@Override
	public void onVideoInteraction(Video video) {
		log.trace("iteracao, video: {}",video.getName());

		Intent i = Utils.newVideoIntent(this,video);

		if (i != null) {
			startActivity(i);

		} else {

			Toast.makeText(this,getString(R.string.cannot_handle_video,video.getSite()),Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onReviewInteraction(Review review) {
		log.trace("iteracao, review: {}",review.getId());

		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(review.getUrl()));
		startActivity(i);
	}
}
