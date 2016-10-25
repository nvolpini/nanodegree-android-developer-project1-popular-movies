package app.popularmovies;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.popularmovies.model.SearchParams;
import app.popularmovies.service.TheMoviesDBService;

public class FetchMoviesService extends IntentService {

	private static final Logger log = LoggerFactory.getLogger(FetchMoviesService.class);

	private static final String SEARCH_PARAMS_PARCELABLE_KEY = "searchParams";

	private SearchParams searchParams;

	/**
	 * Default constructor, do not use.
	 * Instantiate via {@link #newIntent(Context, SearchParams)}
	 */
	public FetchMoviesService() {
		super(FetchMoviesService.class.getName());
	}

	public static Intent newIntent(Context context, SearchParams searchParams) {
		Intent intent = new Intent(context,FetchMoviesService.class);
		intent.putExtra(SEARCH_PARAMS_PARCELABLE_KEY, searchParams);
		return intent;
	}

	@Override
	protected void onHandleIntent(Intent intent) {


		searchParams = intent.getParcelableExtra(SEARCH_PARAMS_PARCELABLE_KEY);


		TheMoviesDBService fetch = new TheMoviesDBService();

		fetch.setMoviesLanguageChanged(Utils.hasMoviesLanguageChanged(this));

		try {

			fetch.fetchMovies(this, searchParams);

			Utils.setMoviesLanguageChanged(this,false);

		} catch (Exception e) {

			log.error("error fetching movies.", e);


		}

	}
}
