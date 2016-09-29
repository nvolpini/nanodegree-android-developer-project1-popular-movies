package app.popularmovies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by neimar on 28/09/16.
 */

public class MoviesProvider extends ContentProvider {

	// The URI Matcher used by this content provider.
	private static final UriMatcher sUriMatcher = buildUriMatcher();

	private MoviesDbHelper mOpenHelper;

	static final int MOVIES = 100;
	static final int MOVIE_ID = 101;
	static final int POPULAR_MOVIES = 200;
	static final int TOP_RATED_MOVIES = 201;
	static final int FAVORITE_MOVIES = 202;


	static UriMatcher buildUriMatcher() {

		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = MovieContract.CONTENT_AUTHORITY;

		matcher.addURI(authority, MovieContract.PATH_MOVIES, MOVIES);
		matcher.addURI(authority, MovieContract.PATH_MOVIES+"/#", MOVIE_ID);
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
			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}


	@Nullable
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		// Here's the switch statement that, given a URI, will determine what kind of request it is,
		// and query the database accordingly.
		Cursor retCursor;
		switch (sUriMatcher.match(uri)) {

			case POPULAR_MOVIES: {
				retCursor = mOpenHelper.getReadableDatabase().query(
						MovieContract.PopularMoviesEntry.TABLE_NAME,
						projection,
						selection,
						selectionArgs,
						null,
						null,
						sortOrder
				);
				break;
			}
			case TOP_RATED_MOVIES: {
				retCursor = mOpenHelper.getReadableDatabase().query(
						MovieContract.TopRatedMoviesEntry.TABLE_NAME,
						projection,
						selection,
						selectionArgs,
						null,
						null,
						sortOrder
				);
				break;
			}
			case FAVORITE_MOVIES: {
				retCursor = mOpenHelper.getReadableDatabase().query(
						MovieContract.FavoriteMoviesEntry.TABLE_NAME,
						projection,
						selection,
						selectionArgs,
						null,
						null,
						sortOrder
				);
				break;
			}
			case MOVIE_ID:
			{
				retCursor = mOpenHelper.getReadableDatabase().query(
						MovieContract.MovieEntry.TABLE_NAME,
						projection,
						selection,
						selectionArgs,
						null,
						null,
						sortOrder
				);
				break;
			}

			case MOVIES:
			{
				retCursor = mOpenHelper.getReadableDatabase().query(
						MovieContract.MovieEntry.TABLE_NAME,
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


	private void normalizeDate(ContentValues values) {
		// normalize the date value
		if (values.containsKey(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)) {
			long dateValue = values.getAsLong(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
			values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, MovieContract.normalizeDate(dateValue));
		}
		if (values.containsKey(MovieContract.FavoriteMoviesEntry.COLUMN_DATE_ADD)) {
			long dateValue = values.getAsLong(MovieContract.FavoriteMoviesEntry.COLUMN_DATE_ADD);
			values.put(MovieContract.FavoriteMoviesEntry.COLUMN_DATE_ADD, MovieContract.normalizeDate(dateValue));
		}

	}

	@Nullable
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		Uri returnUri;

		switch (match) {
			case MOVIE_ID: {
				normalizeDate(values);
				long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
				if (_id > 0)
					returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
				else
					throw new android.database.SQLException("Failed to insert row into " + uri);
				break;
			}

			//TODO INSERT OTHERS

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

			//TODO OTHERS

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
				normalizeDate(values);
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
		switch (match) {
			case POPULAR_MOVIES:
				db.beginTransaction();
				int returnCount = 0;
				try {
					for (ContentValues value : values) {
						normalizeDate(value);
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
