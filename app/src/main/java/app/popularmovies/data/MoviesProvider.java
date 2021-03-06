package app.popularmovies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by neimar on 28/09/16.
 */

public class MoviesProvider extends ContentProvider {

	private static final Logger log = LoggerFactory.getLogger(MoviesProvider.class);

	// The URI Matcher used by this content provider.
	private static final UriMatcher sUriMatcher = buildUriMatcher();

	private MoviesDbHelper mOpenHelper;

	static final int MOVIES = 100;
	static final int MOVIE_ID = 101;
	static final int MOVIE_VIDEOS = 110;
	static final int MOVIE_REVIEWS = 120;
	static final int POPULAR_MOVIES = 200;
	static final int TOP_RATED_MOVIES = 201;
	static final int FAVORITE_MOVIES = 202;


	static UriMatcher buildUriMatcher() {

		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = MovieContract.CONTENT_AUTHORITY;

		matcher.addURI(authority, MovieContract.PATH_MOVIES, MOVIES);
		matcher.addURI(authority, MovieContract.PATH_MOVIES+"/#", MOVIE_ID);
		matcher.addURI(authority, MovieContract.PATH_MOVIES+"/"+MovieContract.PATH_VIDEOS+"/#", MOVIE_VIDEOS);
		matcher.addURI(authority, MovieContract.PATH_MOVIES+"/"+MovieContract.PATH_REVIEWS+"/#", MOVIE_REVIEWS);
		matcher.addURI(authority, MovieContract.PATH_MOVIES+"/"+MovieContract.PATH_POPULAR, POPULAR_MOVIES);
		matcher.addURI(authority, MovieContract.PATH_MOVIES+"/"+MovieContract.PATH_TOP_RATED, TOP_RATED_MOVIES);
		matcher.addURI(authority, MovieContract.PATH_MOVIES+"/"+MovieContract.PATH_FAVORITES, FAVORITE_MOVIES);

		return matcher;
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new MoviesDbHelper(getContext());
		return true;
	}


	@Nullable
	@Override
	public String getType(Uri uri) {

		final int match = sUriMatcher.match(uri);

		log.trace("matching uri {} to {}", uri, match);

		switch (match) {
			case MOVIE_ID:
				return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
			case MOVIES:
				return MovieContract.MovieEntry.CONTENT_TYPE;
			case POPULAR_MOVIES:
				return MovieContract.PopularMoviesEntry.CONTENT_TYPE;
			case TOP_RATED_MOVIES:
				return MovieContract.TopRatedMoviesEntry.CONTENT_TYPE;
			case FAVORITE_MOVIES:
				return MovieContract.FavoriteMoviesEntry.CONTENT_TYPE;
			case MOVIE_VIDEOS:
				return MovieContract.VideoEntry.CONTENT_TYPE;
			case MOVIE_REVIEWS:
				return MovieContract.ReviewEntry.CONTENT_TYPE;
			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}


	private Cursor getMovies(
			Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

		return MoviesDbHelper.MOVIES_QUERY_BUILDER.query(mOpenHelper.getReadableDatabase(),
				projection,
				selection,
				selectionArgs,
				null,
				null,
				sortOrder
		);
	}
	private Cursor getPopular(
			Uri uri, String[] projection, String sortOrder) {

		return MoviesDbHelper.POPULAR_MOVIES_QUERY_BUILDER.query(mOpenHelper.getReadableDatabase(),
				projection,
				null,
				null,
				null,
				null,
				sortOrder == null ? sortOrder = MovieContract.PopularMoviesEntry.TABLE_NAME+"."+MovieContract.PopularMoviesEntry.COLUMN_POSITION : sortOrder
		);
	}

	private Cursor getTopRated(
			Uri uri, String[] projection, String sortOrder) {

		return MoviesDbHelper.TOP_RATED_QUERY_BUILDER.query(mOpenHelper.getReadableDatabase(),
				projection,
				null,
				null,
				null,
				null,
				sortOrder == null ? sortOrder = MovieContract.TopRatedMoviesEntry.TABLE_NAME+"."+MovieContract.TopRatedMoviesEntry.COLUMN_POSITION : sortOrder
		);
	}


	private Cursor getFavorites(
			Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

		return MoviesDbHelper.FAVORITES_QUERY_BUILDER.query(mOpenHelper.getReadableDatabase(),
				projection,
				selection,
				selectionArgs,
				null,
				null,
				sortOrder == null ? sortOrder = MovieContract.FavoriteMoviesEntry.TABLE_NAME+"."+MovieContract.FavoriteMoviesEntry.COLUMN_POSITION : sortOrder
		);
	}



	@Nullable
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

		log.trace("querying uri: {}, projection: {}, selection: {}, args: {}",uri,projection,selection,selectionArgs);



		// Here's the switch statement that, given a URI, will determine what kind of request it is,
		// and query the database accordingly.
		Cursor retCursor;
		switch (sUriMatcher.match(uri)) {

			case POPULAR_MOVIES: {
				projection = projection != null ? projection : MoviesDbHelper.DEFAULT_MOVIES_PROJECTION;
				retCursor = getPopular(uri,projection
						,sortOrder != null ? sortOrder : MovieContract.PopularMoviesEntry.TABLE_NAME+"."+MovieContract.PopularMoviesEntry.COLUMN_POSITION);

				log.trace("total: {}",retCursor.getCount());

				break;
			}
			case TOP_RATED_MOVIES: {
				projection = projection != null ? projection : MoviesDbHelper.DEFAULT_MOVIES_PROJECTION;
				retCursor = getTopRated(uri,projection
						,sortOrder != null ? sortOrder : MovieContract.TopRatedMoviesEntry.TABLE_NAME+"."+MovieContract.TopRatedMoviesEntry.COLUMN_POSITION);
				break;
			}
			case FAVORITE_MOVIES: {
				projection = projection != null ? projection : MoviesDbHelper.DEFAULT_MOVIES_PROJECTION;
				retCursor = getFavorites(uri,projection,selection,selectionArgs
						,sortOrder != null ? sortOrder : MovieContract.FavoriteMoviesEntry.TABLE_NAME+"."+MovieContract.FavoriteMoviesEntry.COLUMN_POSITION);
				break;
			}
			case MOVIE_ID:
			{
				projection = projection != null ? projection : MoviesDbHelper.DEFAULT_MOVIES_PROJECTION;
				selection = MovieContract.MovieEntry.TABLE_NAME+"."+ MovieContract.MovieEntry._ID+"=?";
				selectionArgs = new String[]{Long.toString(MovieContract.MovieEntry.getMovieIdFromUri(uri))};

				log.trace("selection: {}, args: {}", selection, selectionArgs);

				retCursor = getMovies(uri, projection,selection, selectionArgs, sortOrder);
				break;
			}

			case MOVIES:
			{
				projection = projection != null ? projection : MoviesDbHelper.DEFAULT_MOVIES_PROJECTION;
				retCursor = getMovies(uri,projection,selection,selectionArgs
						,sortOrder != null ? sortOrder : MovieContract.MovieEntry.TABLE_NAME+"."+MovieContract.MovieEntry.COLUMN_TITLE);
				break;
			}

			case MOVIE_VIDEOS:
			{
				selection = MovieContract.VideoEntry.COLUMN_MOVIE_ID+"=?";
				selectionArgs = new String[]{Long.toString(MovieContract.VideoEntry.getMovieIdFromUri(uri))};

				retCursor = mOpenHelper.getReadableDatabase().query(
						MovieContract.VideoEntry.TABLE_NAME,
						projection,
						selection,
						selectionArgs,
						null,
						null,
						sortOrder
				);
				break;
			}

			case MOVIE_REVIEWS:
			{
				selection = MovieContract.ReviewEntry.COLUMN_MOVIE_ID+"=?";
				selectionArgs = new String[]{Long.toString(MovieContract.ReviewEntry.getMovieIdFromUri(uri))};

				retCursor = mOpenHelper.getReadableDatabase().query(
						MovieContract.ReviewEntry.TABLE_NAME,
						projection,
						selection,
						selectionArgs,
						null,
						null,
						sortOrder
				);
				break;
			}
			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		retCursor.setNotificationUri(getContext().getContentResolver(), uri);
		return retCursor;
	}


	@Nullable
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		Uri returnUri;

		switch (match) {
			case MOVIE_ID: {
				//normalizeDate(values);
				long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
				if (_id > 0)
					returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
				else
					throw new android.database.SQLException("Failed to insert row into " + uri);
				break;
			}
			case MOVIES: {
				//normalizeDate(values);
				long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
				if (_id > 0)
					returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
				else
					throw new android.database.SQLException("Failed to insert row into " + uri);
				break;
			}

			case POPULAR_MOVIES: {
				long _id = db.insert(MovieContract.PopularMoviesEntry.TABLE_NAME, null, values);
				if (_id > 0)
					returnUri = MovieContract.PopularMoviesEntry.buildMovieUri(_id);
				else
					throw new android.database.SQLException("Failed to insert row into " + uri);
				break;
			}

			case TOP_RATED_MOVIES: {
				long _id = db.insert(MovieContract.TopRatedMoviesEntry.TABLE_NAME, null, values);
				if (_id > 0)
					returnUri = MovieContract.TopRatedMoviesEntry.buildMovieUri(_id);
				else
					throw new android.database.SQLException("Failed to insert row into " + uri);
				break;
			}

			case FAVORITE_MOVIES: {
				long _id = db.insert(MovieContract.FavoriteMoviesEntry.TABLE_NAME, null, values);
				if (_id > 0)
					returnUri = MovieContract.FavoriteMoviesEntry.buildMovieUri(_id);
				else
					throw new android.database.SQLException("Failed to insert row into " + uri);
				break;
			}

			case MOVIE_VIDEOS: {
				long _id = db.insert(MovieContract.VideoEntry.TABLE_NAME, null, values);
				if (_id > 0)
					returnUri = MovieContract.VideoEntry.buildVideosUri(_id);
				else
					throw new android.database.SQLException("Failed to insert row into " + uri);
				break;
			}

			case MOVIE_REVIEWS: {
				long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
				if (_id > 0)
					returnUri = MovieContract.ReviewEntry.buildReviewsUri(_id);
				else
					throw new android.database.SQLException("Failed to insert row into " + uri);
				break;
			}
			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return returnUri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		int rowsDeleted;
		// this makes delete all rows return the number of rows deleted
		if ( null == selection ) selection = "1";
		switch (match) {
			case MOVIE_ID:
				rowsDeleted = db.delete(
						MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
				break;
			case MOVIES:
				rowsDeleted = db.delete(
						MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
				break;
			case POPULAR_MOVIES:
				rowsDeleted = db.delete(
						MovieContract.PopularMoviesEntry.TABLE_NAME, selection, selectionArgs);
				break;
			case TOP_RATED_MOVIES:
				rowsDeleted = db.delete(
						MovieContract.TopRatedMoviesEntry.TABLE_NAME, selection, selectionArgs);
				break;
			case FAVORITE_MOVIES:
				rowsDeleted = db.delete(
						MovieContract.FavoriteMoviesEntry.TABLE_NAME, selection, selectionArgs);
				break;
			case MOVIE_VIDEOS:
				rowsDeleted = db.delete(
						MovieContract.VideoEntry.TABLE_NAME, selection, selectionArgs);
				break;
			case MOVIE_REVIEWS:
				rowsDeleted = db.delete(
						MovieContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
				break;

			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		// Because a null deletes all rows
		if (rowsDeleted != 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		int rowsUpdated;

		switch (match) {
			case MOVIE_ID:
				//normalizeDate(values);
				rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection,
						selectionArgs);
				break;
			case MOVIES:
				//normalizeDate(values);
				rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection,
						selectionArgs);
				break;
			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		if (rowsUpdated != 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return rowsUpdated;
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		int returnCount = 0;
		switch (match) {
			case MOVIES:
				db.beginTransaction();

				try {
					for (ContentValues value : values) {
						//normalizeDate(value);
						long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
						if (_id != -1) {
							returnCount++;
						}
					}
					db.setTransactionSuccessful();
				} finally {
					db.endTransaction();
				}
				getContext().getContentResolver().notifyChange(uri, null);
				return returnCount;
			case POPULAR_MOVIES:
				db.beginTransaction();
				//clear first
				db.delete(MovieContract.PopularMoviesEntry.TABLE_NAME,"1",null);
				try {
					for (ContentValues value : values) {
						//normalizeDate(value);
						long _id = db.insert(MovieContract.PopularMoviesEntry.TABLE_NAME, null, value);
						if (_id != -1) {
							returnCount++;
						}
					}
					db.setTransactionSuccessful();
				} finally {
					db.endTransaction();
				}
				getContext().getContentResolver().notifyChange(uri, null);
				return returnCount;

			case TOP_RATED_MOVIES:
				db.beginTransaction();
				db.delete(MovieContract.TopRatedMoviesEntry.TABLE_NAME,"1",null);
				try {
					for (ContentValues value : values) {
						//normalizeDate(value);
						long _id = db.insert(MovieContract.TopRatedMoviesEntry.TABLE_NAME, null, value);
						if (_id != -1) {
							returnCount++;
						}
					}
					db.setTransactionSuccessful();
				} finally {
					db.endTransaction();
				}
				getContext().getContentResolver().notifyChange(uri, null);
				return returnCount;

			case MOVIE_VIDEOS:
				db.beginTransaction();
				db.delete(MovieContract.VideoEntry.TABLE_NAME
						, MovieContract.VideoEntry.TABLE_NAME+"."+MovieContract.VideoEntry.COLUMN_MOVIE_ID+"=?"
						, new String[]{Long.toString(MovieContract.VideoEntry.getMovieIdFromUri(uri))});
				try {
					for (ContentValues value : values) {
						//normalizeDate(value);
						long _id = db.insert(MovieContract.VideoEntry.TABLE_NAME, null, value);
						if (_id != -1) {
							returnCount++;
						}

						//update download date
						ContentValues updateValues = new ContentValues();
						updateValues.put(MovieContract.MovieEntry.COLUMN_VIDEOS_DOWNLOADED, new Date().getTime());
						db.update(MovieContract.MovieEntry.TABLE_NAME,updateValues,MovieContract.MovieEntry._ID+"=?", new String[]{value.getAsString(MovieContract.VideoEntry.COLUMN_MOVIE_ID)});
					}
					db.setTransactionSuccessful();
				} finally {
					db.endTransaction();
				}
				getContext().getContentResolver().notifyChange(uri, null);
				return returnCount;

			case MOVIE_REVIEWS:
				db.beginTransaction();
				db.delete(MovieContract.ReviewEntry.TABLE_NAME
						, MovieContract.ReviewEntry.TABLE_NAME+"."+MovieContract.ReviewEntry.COLUMN_MOVIE_ID+"=?"
						, new String[]{Long.toString(MovieContract.ReviewEntry.getMovieIdFromUri(uri))});
				try {
					for (ContentValues value : values) {
						//normalizeDate(value);
						long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, value);
						if (_id != -1) {
							returnCount++;
						}

						//update download date
						ContentValues updateValues = new ContentValues();
						updateValues.put(MovieContract.MovieEntry.COLUMN_REVIEWS_DOWNLOADED, new Date().getTime());
						db.update(MovieContract.MovieEntry.TABLE_NAME,updateValues,MovieContract.MovieEntry._ID+"=?", new String[]{value.getAsString(MovieContract.ReviewEntry.COLUMN_MOVIE_ID)});

					}
					db.setTransactionSuccessful();
				} finally {
					db.endTransaction();
				}
				getContext().getContentResolver().notifyChange(uri, null);
				return returnCount;

			default:
				return super.bulkInsert(uri, values);
		}
	}

	// You do not need to call this method. This is a method specifically to assist the testing
	// framework in running smoothly. You can read more at:
	// http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
	@Override
	@TargetApi(11)
	public void shutdown() {
		mOpenHelper.close();
		super.shutdown();
	}
}
