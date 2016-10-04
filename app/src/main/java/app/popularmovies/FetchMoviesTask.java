package app.popularmovies;

import android.content.Context;
import android.os.AsyncTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import app.popularmovies.model.Movie;
import app.popularmovies.model.SearchParams;
import app.popularmovies.service.IMovieSearch;
import app.popularmovies.service.MoviesDataException;
import app.popularmovies.service.MoviesService;

/**
 * Created by neimar on 02/10/16.
 */

public class FetchMoviesTask extends AsyncTask<SearchParams,Void,Void> {

	private static final Logger log = LoggerFactory.getLogger(FetchMoviesTask.class);

	private final Context mContext;

	public FetchMoviesTask(Context context) {
		mContext = context;
	}
	
	@Override
	protected Void doInBackground(SearchParams... params) {

		if (params.length == 0) {
			return null;
		}

		SearchParams searchParams = params[0];

		try {
			IMovieSearch search = MoviesService.get().newSearch(searchParams);

			List<Movie> movies = search.list();



		} catch (MoviesDataException e) {

			log.error("error fetching movies.",e);


		}

		return null;
	}

}