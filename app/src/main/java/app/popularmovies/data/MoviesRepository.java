package app.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import app.popularmovies.model.Movie;
import app.popularmovies.service.MoviesDataException;

import static android.R.attr.id;

/**
 * Created by neimar on 04/10/16.
 */

/**
 * Work with db directly.
 * Proof of concept -  most operations will be running via ContentProvider
 * @deprecated
 */
public class MoviesRepository implements IMoviesRepository {

	private static final Logger log = LoggerFactory.getLogger(MoviesRepository.class);

	private static MoviesRepository sInstance = null;
	private final Context mContext;

	/**
	 * @deprecated
	 * @param context
	 * @return
	 */
	public static synchronized MoviesRepository get(Context context) {

		if (sInstance == null) {
			sInstance = new MoviesRepository(context.getApplicationContext());
		}
		return sInstance;
	}

	private final MoviesDbHelper dbHelper;

	private MoviesRepository(Context context) {
		dbHelper = new MoviesDbHelper(context);
		mContext = context;
	}

	@Override
	public void create(Movie movie) throws MoviesDataException {

		ContentValues values = MoviesDbHelper.getContentValues(movie);

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

		ContentValues values = MoviesDbHelper.getContentValues(movie);

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
		log.trace("checking if movie exists, movieDbId: {}",movieDbId);

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

			cursor.moveToFirst();

			return MoviesDbHelper.cursorToMovie(cursor);

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
		SQLiteDatabase db = null;

		log.debug("saving popular movies...");

		try {

			db = dbHelper.getWritableDatabase();

			db.beginTransaction();
			try {

				int rows = db.delete(MovieContract.PopularMoviesEntry.TABLE_NAME,"1",null);

				log.debug("deleted {} rows from '{}' table",rows,MovieContract.PopularMoviesEntry.TABLE_NAME);

				int pos = 1;
				for (Movie m : movies) {

					ContentValues values = new ContentValues();
					values.put(MovieContract.PopularMoviesEntry.COLUMN_MOVIE_ID, m.getId());
					values.put(MovieContract.PopularMoviesEntry.COLUMN_POSITION, pos++);

					long id = db.insert(MovieContract.PopularMoviesEntry.TABLE_NAME, null, values);

					if (id < 0) {
						throw new  MoviesDataException(String.format("Error inserting popular movie: %s",m.getId()));
					}

				}
				



				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}

			mContext.getContentResolver().notifyChange(MovieContract.PopularMoviesEntry.CONTENT_URI, null);

		} catch (SQLiteException e) {

			log.error("Error opening database",e);

			throw new  MoviesDataException("Error opening database",e);

		} finally {
			if (db != null)
				db.close();
		}

	}

	@Override
	public void saveTopRated(List<Movie> movies) {
		SQLiteDatabase db = null;

		log.debug("saving top rated movies...");

		try {

			db = dbHelper.getWritableDatabase();

			db.beginTransaction();
			try {

				int rows = db.delete(MovieContract.TopRatedMoviesEntry.TABLE_NAME,"1",null);

				log.debug("deleted {} rows from '{}' table",rows,MovieContract.TopRatedMoviesEntry.TABLE_NAME);

				int pos = 1;
				for (Movie m : movies) {

					ContentValues values = new ContentValues();
					values.put(MovieContract.TopRatedMoviesEntry.COLUMN_MOVIE_ID, m.getId());
					values.put(MovieContract.TopRatedMoviesEntry.COLUMN_POSITION, pos++);

					long id = db.insert(MovieContract.TopRatedMoviesEntry.TABLE_NAME, null, values);

					if (id < 0) {
						throw new  MoviesDataException(String.format("Error inserting top_rated movie: %s",m.getId()));
					}

				}



				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}

			mContext.getContentResolver().notifyChange(MovieContract.TopRatedMoviesEntry.CONTENT_URI, null);

		} catch (SQLiteException e) {

			log.error("Error opening database",e);

			throw new  MoviesDataException("Error opening database",e);

		} finally {
			if (db != null)
				db.close();
		}
	}
}
