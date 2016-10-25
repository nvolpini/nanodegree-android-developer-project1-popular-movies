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
import app.popularmovies.model.SearchParams;
import app.popularmovies.model.Video;

public class MainActivity extends AppCompatActivity implements
		MoviesFragment.OnListFragmentInteractionListener
		, MovieDetailsFragment.OnMovieDetailsInteractionListener {

	private static final Logger log = LoggerFactory.getLogger(MainActivity.class);

	private static final String DETAILFRAGMENT_TAG = "DFTAG";

	private boolean mTwoPane;
	private String mLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		log.trace("onCreate");

		//reset all prefs
		//PreferenceManager.getDefaultSharedPreferences(this).edit().clear().apply();

		//initialize prefs
		PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
		PreferenceManager.setDefaultValues(this, R.xml.pref_data_sync, false);



		if (findViewById(R.id.movie_detail_container) != null) {
			// The detail container view will be present only in the large-screen layouts
			// (res/layout-sw600dp). If this view is present, then the activity should be
			// in two-pane mode.
			mTwoPane = true;
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			if (savedInstanceState == null) {
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.movie_detail_container
								, MovieDetailsFragment.newInstance(null)
								, DETAILFRAGMENT_TAG)
						.commit();

				//do not show yet
				findViewById(R.id.movie_detail_container).setVisibility(View.INVISIBLE);
			}
		} else {
			mTwoPane = false;
			getSupportActionBar().setElevation(0f);
		}

		MoviesFragment moviesFragment = ((MoviesFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_movies));

		//moviesFragment.setSearchParams(searchParams);

		//Bundle args = new Bundle();
		//args.putInt(MoviesFragment.ARG_COLUMN_COUNT, columnCount);
		//args.putParcelable(MoviesFragment.SEARCH_PARAMS_PARCELABLE_KEY, searchParams);
		//moviesFragment.setArguments(args);


	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {

		//log.trace("saving state...");

		//outState.putParcelable(MoviesFragment.SEARCH_PARAMS_PARCELABLE_KEY, searchParams);

		super.onSaveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		//log.trace("restoring state...");

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

		} else if (id == R.id.action_main2) {
			Intent intent = new Intent(this, Main2Activity.class);
			startActivity(intent);
			return true;

		} else if (id == R.id.action_download) {
			downloadMovies();
			return true;
		}

		return super.onOptionsItemSelected(item);

	}

	private void downloadMovies() {

		SearchParams searchParams = Utils.newSearchParams(this);


		log.trace("downloading movies, params: {}", searchParams);

		if (Utils.isOnline(this)) {

			Intent intent = FetchMoviesService.newIntent(this, searchParams);
			startService(intent);

		} else {
			log.trace("no internet access.");
			Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onListFragmentInteraction(Movie movie) {

		log.trace("iteracao, movie: {}", movie.getOriginalTitle());

		if (mTwoPane) {

			MovieDetailsFragment df = MovieDetailsFragment.newInstance(movie);

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

}
