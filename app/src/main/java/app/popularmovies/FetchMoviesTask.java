package app.popularmovies;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
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

	public FetchMoviesTask(Context context) {
		mContext = context;
	}
	
	@Override
	protected Void doInBackground(SearchParams... params) {

		if (params.length == 0) {
			return null;
		}

		SearchParams searchParams = params[0];

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

			long movieId = addMovie(movie);

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


		return null;
	}

	private long addMovie(Movie movie) {

		long movieId;

		// First, check if the movie already exists
		Cursor cursor = mContext.getContentResolver().query(
				MovieContract.MovieEntry.CONTENT_URI,
				new String[]{MovieContract.MovieEntry._ID},
				MovieContract.MovieEntry.COLUMN_MOVIESDB_ID + " = ?",
				new String[]{Integer.toString(movie.getMoviesDbId())},
				null);

		if (cursor.moveToFirst()) {
			int movieIdIndex = cursor.getColumnIndex(MovieContract.MovieEntry._ID);
			movieId = cursor.getLong(movieIdIndex);
			movie.setId(movieId);
			log.trace("movie already exists: {}",movie);
		} else {

			ContentValues movieValues = MoviesDbHelper.getContentValues(movie);

			Uri insertedUri = mContext.getContentResolver().insert(
					MovieContract.MovieEntry.CONTENT_URI,
					movieValues
			);

			// The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
			movieId = ContentUris.parseId(insertedUri);
			movie.setId(movieId);
			log.trace("Movie created: {}",movie);
		}

		cursor.close();

		return movieId;
	}

}