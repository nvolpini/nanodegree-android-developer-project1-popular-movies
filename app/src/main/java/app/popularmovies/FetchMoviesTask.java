package app.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Vector;

import app.popularmovies.data.MovieContract;
import app.popularmovies.data.MoviesDbHelper;
import app.popularmovies.model.Movie;
import app.popularmovies.model.SearchParams;
import app.popularmovies.service.IMovieSearch;
import app.popularmovies.service.MoviesService;


/**
 * Created by neimar on 02/10/16.
 */

public class FetchMoviesTask extends AsyncTask<SearchParams,Void,Void> {

	private static final Logger log = LoggerFactory.getLogger(FetchMoviesTask.class);

	private final Context mContext;
	private SearchParams searchParams;

	public FetchMoviesTask(Context context) {
		mContext = context;
	}
	
	@Override
	protected Void doInBackground(SearchParams... params) {

		if (params.length == 0) {
			return null;
		}

		searchParams = params[0];

		log.debug("fetching remote movies: {}", searchParams);

		List<Movie> moviesList;
		try {

			IMovieSearch s = MoviesService.get().newSearch(searchParams);

			moviesList = s.list();


		} catch (Exception e) {

			log.error("error fetching movies.", e);

			return null;

		}

		Vector<ContentValues> regs = new Vector<ContentValues>(moviesList.size());

		for (Movie movie : moviesList) {

			long movieId = MoviesDbHelper.addMovie(mContext,movie, Utils.hasMoviesLanguageChanged(mContext));

			ContentValues values = new ContentValues();

			if (searchParams.isSortByPopularity()) {

				values.put(MovieContract.PopularMoviesEntry.COLUMN_MOVIE_ID,movieId);
				values.put(MovieContract.PopularMoviesEntry.COLUMN_POSITION,regs.size()+1);

			} else if (searchParams.isSortByRating()) {

				values.put(MovieContract.TopRatedMoviesEntry.COLUMN_MOVIE_ID,movieId);
				values.put(MovieContract.TopRatedMoviesEntry.COLUMN_POSITION,regs.size()+1);

			}

			regs.add(values);

		}//for

		int inserted = 0;
		// add to database
		if ( regs.size() > 0 ) {
			ContentValues[] cvArray = new ContentValues[regs.size()];
			regs.toArray(cvArray);
			inserted = mContext.getContentResolver().bulkInsert(
					searchParams.isSortByPopularity()
							? MovieContract.PopularMoviesEntry.CONTENT_URI
							: MovieContract.TopRatedMoviesEntry.CONTENT_URI
					, cvArray);
		}

		log.debug("Inserted {} movies",inserted);


		//Utils.setMoviesLanguageChanged(context,true);

		return null;
	}


}