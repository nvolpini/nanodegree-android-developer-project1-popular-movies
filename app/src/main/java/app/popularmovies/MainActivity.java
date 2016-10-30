package app.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.popularmovies.model.Movie;
import app.popularmovies.model.Review;
import app.popularmovies.model.Video;

public class MainActivity extends AppCompatActivity implements
		MoviesListFragment.OnListFragmentInteractionListener
		, MovieDetailsFragment.OnMovieDetailsInteractionListener {

	private static final Logger log = LoggerFactory.getLogger(MainActivity.class);

	private static final String DETAILFRAGMENT_TAG = "DFTAG";
	private static final String TABS_FRAGMENT_TAG = "TFTAG";

	private boolean twoPaneLayout;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		log.trace("onCreate() savedState null: {}", (savedInstanceState == null));


		//reset all prefs
		//PreferenceManager.getDefaultSharedPreferences(this).edit().clear().apply();

		//initialize prefs
		PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
		PreferenceManager.setDefaultValues(this, R.xml.pref_data_sync, false);


		if (findViewById(R.id.movie_detail_container) != null) {
			twoPaneLayout = true;


			if (savedInstanceState == null) {


				MoviesTabsFragment tabsFragment = MoviesTabsFragment.newInstance(3);

				getSupportFragmentManager().beginTransaction()
						.replace(R.id.tabs_container
								, tabsFragment
								, TABS_FRAGMENT_TAG)
						.commit();

			}

		} else {
			twoPaneLayout = false;
			getSupportActionBar().setElevation(0f);
		}


	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		//outState.putInt(ALREADY_DOWNLOADED_KEY,alreadyDownloaded ? 1: 0);

		//log.trace("onsave");
	}

	@Override
	protected void onResume() {
		super.onResume();
		log.trace("onResume()");

	}


	@Override
	protected void onPause() {
		super.onPause();
		log.trace("onPause()");


	}


	@Override
	protected void onStart() {
		super.onStart();
		log.trace("onStart()");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		log.trace("onDestroy()");
	}


	@Override
	protected void onRestart() {
		log.trace("onRestart()");

		super.onRestart();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);

		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

		if (id == R.id.action_settings) {
			startActivity(new Intent(this, SettingsActivity.class));
			return true;

		} else if (id == R.id.action_download) {
			Utils.downloadMovies(this);
			return true;
		}

		return super.onOptionsItemSelected(item);

	}

	public boolean isTwoPaneLayout() {
		return twoPaneLayout;
	}


	@Override
	public void onListFragmentInteraction(Movie movie) {

		log.trace("iteracao, movie: {}", movie.getOriginalTitle());

		if (twoPaneLayout) {

			MovieDetailsFragment df = MovieDetailsFragment.newInstance(movie.getId());

			findViewById(R.id.movie_detail_container).setVisibility(View.VISIBLE);

			getSupportFragmentManager().beginTransaction()
					.replace(R.id.movie_detail_container, df, DETAILFRAGMENT_TAG)
					.commit();

		} else {

			Intent intent = MovieDetailsActivity.newIntent(this, movie);

			startActivity(intent);
		}

	}


	@Override
	public void onVideoInteraction(Video video) {
		log.trace("iteracao, video: {}", video.getName());

		Intent i = Utils.newVideoIntent(this, video);

		if (i != null) {
			startActivity(i);

		} else {

			Toast.makeText(this, getString(R.string.cannot_handle_video, video.getSite()), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onReviewInteraction(Review review) {
		log.trace("iteracao, review: {}", review.getId());

		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(review.getUrl()));
		startActivity(i);
	}

	@Override
	public void onShareVideoInteraction(Video video) {
		log.trace("iteracao, share video: {}", video.getId());

		Intent i = Utils.newShareVideoIntent(this, video);

		if (i != null) {
			startActivity(i);

		}

	}

}
