package app.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

import app.popularmovies.Utils;
import app.popularmovies.model.Movie;
import app.popularmovies.service.MoviesDataException;

import static android.R.attr.id;

/**
 * Created by neimar on 04/10/16.
 */

/**
 * Work with db directly.
 * Proof of concept -  most operations will be running via ContentProvider
 */
public class MoviesRepository implements IMoviesRepository {

	private static final Logger log = LoggerFactory.getLogger(MoviesRepository.class);

	private static MoviesRepository sInstance = null;

	public static synchronized MoviesRepository get(Context context) {

		if (sInstance == null) {
			sInstance = new MoviesRepository(context.getApplicationContext());
		}
		return sInstance;
	}

	private final MoviesDbHelper dbHelper;

	private MoviesRepository(Context context) {
		dbHelper = new MoviesDbHelper(context);
	}

	private ContentValues getContentValues(Movie movie) {
		Utils.assertNotNull(movie,"movie cannot be null");

		ContentValues values = new ContentValues();
		values.put(MovieContract.MovieEntry.COLUMN_MOVIESDB_ID, movie.getMoviesDbId());
		values.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
		values.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
		values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate().getTime());
		values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
		values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
		values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());

		return values;

	}

	@Override
	public void create(Movie movie) throws MoviesDataException {

		ContentValues values = getContentValues(movie);

		SQLiteDatabase db = null;

		log.debug("Creating movie: {}",movie);

		try {

			db = dbHelper.getWritableDatabase();

			long id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);

			if (id < 0) {
				throw new  MoviesDataException(String.format("Error inserting movie: %s",movie.getTitle()));
			}

			movie.setId(id);

		} catch (SQLiteException e) {

			log.error("Error opening database",e);

			throw new  MoviesDataException("Error opening database",e);

		} finally {
			if (db != null)
				db.close();
		}

	}

	@Override
	public void update(Movie movie) {

		ContentValues values = getContentValues(movie);

		SQLiteDatabase db = null;

		log.debug("Updating movie: {}",movie);

		try {

			db = dbHelper.getWritableDatabase();

			int rows = db.update(MovieContract.MovieEntry.TABLE_NAME, values, "id=?", new String[]{Long.toString(movie.getId())});

			if (rows != 1) {
				throw new  MoviesDataException(String.format("Error updating movie: %s, rows affected: %s",movie, rows));
			}


		} catch (SQLiteException e) {

			log.error("Error opening database",e);

			throw new  MoviesDataException("Error opening database",e);

		} finally {
			if (db != null)
				db.close();
		}
	}

	@Override
	public void delete(Movie movie) {

		SQLiteDatabase db = null;

		log.debug("Deleting movie: {}",movie);

		try {

			db = dbHelper.getWritableDatabase();

			int rows = db.delete(MovieContract.MovieEntry.TABLE_NAME, "id=?", new String[]{Long.toString(movie.getId())});

			if (rows != 1) {
				throw new  MoviesDataException(String.format("Error deleting movie: %s, rows affected: %s",movie, rows));
			}

		} catch (SQLiteException e) {

			log.error("Error opening database",e);

			throw new  MoviesDataException("Error opening database",e);

		} finally {
			if (db != null)
				db.close();
		}
	}

	@Override
	public boolean exists(int movieDbId) {

		SQLiteDatabase db = null;
		Cursor cursor = null;
		log.debug("checking if movie exists, movieDbId: {}",movieDbId);

		try {

			db = dbHelper.getReadableDatabase();

			cursor = db.rawQuery(String.format("select count(*) from %s where %s = ?"
						, MovieContract.MovieEntry.TABLE_NAME
						, MovieContract.MovieEntry.COLUMN_MOVIESDB_ID
						)
					, new String[]{Integer.toString(movieDbId)});

			cursor.moveToFirst();

			return cursor.getLong(0) > 0;

		} catch (SQLiteException e) {

			log.error("Error opening database",e);

			throw new  MoviesDataException("Error opening database",e);

		} finally {

			if (cursor != null)
				cursor.close();

			if (db != null)
				db.close();
		}


	}

	public Movie getByColumnId(String columnName, long id) {

		SQLiteDatabase db = null;
		Cursor cursor = null;
		log.debug("getting movie by '{}': {}",columnName, id);

		try {

			db = dbHelper.getReadableDatabase();

			cursor = db.rawQuery(String.format("select * from %s where %s = ?"
					, MovieContract.MovieEntry.TABLE_NAME
					, columnName
					)
					, new String[]{Long.toString(id)}
					);

			return cursorToMovie(cursor);

		} catch (SQLiteException e) {

			log.error("Error opening database",e);

			throw new  MoviesDataException("Error opening database",e);

		} finally {

			if (cursor != null)
				cursor.close();

			if (db != null)
				db.close();
		}


	}

	private Movie cursorToMovie(Cursor cursor) {

		Movie m = new Movie();

		cursor.moveToFirst();

		m.setId(cursor.getLong(0));
		m.setMoviesDbId(cursor.getInt(1));
		m.setTitle(cursor.getString(2));
		m.setOriginalTitle(cursor.getString(3));
		m.setOverview(cursor.getString(4));

		m.setReleaseDate(new Date(cursor.getLong(5))); //TODO

		m.setVoteAverage(cursor.getDouble(6));
		m.setPosterPath(cursor.getString(7));

		return m;

	}

	@Override
	public Movie getById(int id) {
		return getByColumnId(MovieContract.MovieEntry._ID,id);
	}

	@Override
	public Movie getByMovieDbId(int movieDbId) {
		return getByColumnId(MovieContract.MovieEntry.COLUMN_MOVIESDB_ID,id);
	}

	@Override
	public List<Movie> getPopularMovies() {
		return null;
	}

	@Override
	public List<Movie> getTopRatedMovies() {
		return null;
	}

	@Override
	public List<Movie> getFavoriteMovies() {
		return null;
	}

	@Override
	public void markFavorite(Movie movie) {

	}

	@Override
	public void removeFavorite(Movie movie) {

	}

	@Override
	public void savePopular(List<Movie> movies) {

	}

	@Override
	public void saveTopRated(List<Movie> movies) {

	}
}
