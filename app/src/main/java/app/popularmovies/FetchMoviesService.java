package app.popularmovies;

import android.app.IntentService;
import android.content.Intent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.popularmovies.model.SearchParams;
import app.popularmovies.service.RetrofitSearchImpl;

public class FetchMoviesService extends IntentService {

	private static final Logger log = LoggerFactory.getLogger(FetchMoviesService.class);

	public static final String SEARCH_PARAMS_PARCELABLE_KEY = "searchParams";

	private SearchParams searchParams;

	public FetchMoviesService() {
		super(FetchMoviesService.class.getName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {


		searchParams = intent.getParcelableExtra(SEARCH_PARAMS_PARCELABLE_KEY);


		RetrofitSearchImpl fetch = new RetrofitSearchImpl(searchParams);

		fetch.setMoviesLanguageChanged(Utils.hasMoviesLanguageChanged(this));

		try {

			fetch.fetchMovies(this);

			Utils.setMoviesLanguageChanged(this,false);

		} catch (Exception e) {

			log.error("error fetching movies.", e);


		}

	}
}
