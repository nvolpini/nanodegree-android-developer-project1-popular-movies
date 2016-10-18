package app.popularmovies.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import app.popularmovies.Utils;
import app.popularmovies.data.MovieContract.FavoriteMoviesEntry;
import app.popularmovies.data.MovieContract.MovieEntry;
import app.popularmovies.data.MovieContract.PopularMoviesEntry;
import app.popularmovies.data.MovieContract.TopRatedMoviesEntry;
import app.popularmovies.model.FavoriteInformation;
import app.popularmovies.model.Movie;

/**
 * Created by neimar on 25/09/16.
 */

public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final Logger log = LoggerFactory.getLogger(MoviesDbHelper.class);

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "popularmovies.db";

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

	public static final String[] DEFAULT_MOVIES_PROJECTION = {
			MovieContract.MovieEntry.TABLE_NAME+"."+MovieContract.MovieEntry._ID
			,MovieContract.MovieEntry.TABLE_NAME+"."+MovieContract.MovieEntry.COLUMN_MOVIESDB_ID
			,MovieContract.MovieEntry.TABLE_NAME+"."+MovieContract.MovieEntry.COLUMN_TITLE
			,MovieContract.MovieEntry.TABLE_NAME+"."+MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE
			,MovieContract.MovieEntry.TABLE_NAME+"."+MovieContract.MovieEntry.COLUMN_OVERVIEW
			,MovieContract.MovieEntry.TABLE_NAME+"."+MovieContract.MovieEntry.COLUMN_RELEASE_DATE
			,MovieContract.MovieEntry.TABLE_NAME+"."+MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE
			,MovieContract.MovieEntry.TABLE_NAME+"."+MovieContract.MovieEntry.COLUMN_POSTER_PATH

			, FavoriteMoviesEntry.TABLE_NAME+"."+ FavoriteMoviesEntry._ID+" as fav_id "
			, FavoriteMoviesEntry.TABLE_NAME+"."+ FavoriteMoviesEntry.COLUMN_POSITION
			, FavoriteMoviesEntry.TABLE_NAME+"."+ FavoriteMoviesEntry.COLUMN_DATE_ADD
			, FavoriteMoviesEntry.TABLE_NAME+"."+ FavoriteMoviesEntry.COLUMN_VOTES

	};

	public static final SQLiteQueryBuilder POPULAR_MOVIES_QUERY_BUILDER;

	static{
		POPULAR_MOVIES_QUERY_BUILDER = new SQLiteQueryBuilder();

		//This is an inner join which looks like
		//top_rated INNER JOIN movies ON movies.id = top_rated.movie_id
		POPULAR_MOVIES_QUERY_BUILDER.setTables(
				MovieContract.PopularMoviesEntry.TABLE_NAME + " INNER JOIN " +
						MovieContract.MovieEntry.TABLE_NAME +
						" ON " + MovieContract.MovieEntry.TABLE_NAME +
						"." + MovieContract.MovieEntry._ID +
						" = " + MovieContract.PopularMoviesEntry.TABLE_NAME +
						"." + MovieContract.PopularMoviesEntry.COLUMN_MOVIE_ID +
						" LEFT JOIN "+FavoriteMoviesEntry.TABLE_NAME +
						" ON "+FavoriteMoviesEntry.TABLE_NAME+
						"."+FavoriteMoviesEntry.COLUMN_MOVIE_ID +
						" = "+MovieEntry.TABLE_NAME+
						"."+MovieEntry._ID
					);
	}

	public static final SQLiteQueryBuilder TOP_RATED_QUERY_BUILDER;

	static{
		TOP_RATED_QUERY_BUILDER = new SQLiteQueryBuilder();

		//This is an inner join which looks like
		//top_rated INNER JOIN movies ON movies.id = top_rated.movie_id
		TOP_RATED_QUERY_BUILDER.setTables(
				MovieContract.TopRatedMoviesEntry.TABLE_NAME + " INNER JOIN " +
						MovieContract.MovieEntry.TABLE_NAME +
						" ON " + MovieContract.MovieEntry.TABLE_NAME +
						"." + MovieContract.MovieEntry._ID +
						" = " + MovieContract.TopRatedMoviesEntry.TABLE_NAME +
						"." + MovieContract.TopRatedMoviesEntry.COLUMN_MOVIE_ID+
						" LEFT JOIN "+FavoriteMoviesEntry.TABLE_NAME +
						" ON "+FavoriteMoviesEntry.TABLE_NAME+
						"."+FavoriteMoviesEntry.COLUMN_MOVIE_ID +
						" = "+MovieEntry.TABLE_NAME+
						"."+MovieEntry._ID
					);


	}


	public static final SQLiteQueryBuilder FAVORITES_QUERY_BUILDER;

	static{
		FAVORITES_QUERY_BUILDER = new SQLiteQueryBuilder();

		//This is an inner join which looks like
		//top_rated INNER JOIN movies ON movies.id = top_rated.movie_id
		FAVORITES_QUERY_BUILDER.setTables(
				MovieContract.FavoriteMoviesEntry.TABLE_NAME + " INNER JOIN " +
						MovieContract.MovieEntry.TABLE_NAME +
						" ON " + MovieContract.MovieEntry.TABLE_NAME +
						"." + MovieContract.MovieEntry._ID +
						" = " + MovieContract.FavoriteMoviesEntry.TABLE_NAME +
						"." + MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID

					);
	}


	public static Movie cursorToMovie(Cursor cursor) {

		Movie m = new Movie();

		m.setId(cursor.getLong(0));
		m.setMoviesDbId(cursor.getInt(1));
		m.setTitle(cursor.getString(2));
		m.setOriginalTitle(cursor.getString(3));
		m.setOverview(cursor.getString(4));

		m.setReleaseDate(new Date(cursor.getLong(5))); //TODO

		m.setVoteAverage(cursor.getDouble(6));
		m.setPosterPath(cursor.getString(7));

		FavoriteInformation f = new FavoriteInformation();

		int fidi = cursor.getColumnIndex("fav_id");

		//log.trace("favorite id col index: {}",fidi);

		if (fidi >= 0 && !cursor.isNull(fidi)) {

			f.setId(cursor.getLong(fidi));
			f.setPosition(cursor.getInt(9));
			f.setDateAdded(new Date(cursor.getLong(10)));
			f.setVotes(cursor.getInt(11));

			//log.trace("Movie is a favorite, fid: {}, movieId: {}",f.getId(), m.getId());

			m.setFavoriteInformation(f);

		}

		//log.trace("Movie from cursor: {}",m);

		return m;

	}

	@Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieEntry.COLUMN_MOVIESDB_ID + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
				" UNIQUE (" + MovieEntry.COLUMN_MOVIESDB_ID + ") " +
				" ON CONFLICT REPLACE" +
                " );";

        final String SQL_CREATE_POPULAR_MOVIES_TABLE = "CREATE TABLE " + PopularMoviesEntry.TABLE_NAME + " (" +
				PopularMoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				PopularMoviesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL " +
				" REFERENCES "+MovieEntry.TABLE_NAME+" (" +MovieEntry._ID+"), " +
				PopularMoviesEntry.COLUMN_POSITION + " INTEGER NOT NULL, " +
				" UNIQUE (" + PopularMoviesEntry.COLUMN_MOVIE_ID + ") " +
				" ON CONFLICT REPLACE" +
                " );";



		final String SQL_CREATE_TOP_RATED_MOVIES_TABLE = "CREATE TABLE " + TopRatedMoviesEntry.TABLE_NAME + " (" +
				TopRatedMoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				TopRatedMoviesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL " +
				" REFERENCES "+MovieEntry.TABLE_NAME+" (" +MovieEntry._ID+"), " +
				TopRatedMoviesEntry.COLUMN_POSITION + " INTEGER NOT NULL, " +
				" UNIQUE (" + TopRatedMoviesEntry.COLUMN_MOVIE_ID + ") " +
				" ON CONFLICT REPLACE" +
				" );";



		final String SQL_CREATE_FAVORITE_MOVIES_TABLE = "CREATE TABLE " + FavoriteMoviesEntry.TABLE_NAME + " (" +
				FavoriteMoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				FavoriteMoviesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL " +
				" REFERENCES "+MovieEntry.TABLE_NAME+" (" +MovieEntry._ID+"), " +
				FavoriteMoviesEntry.COLUMN_POSITION + " INTEGER NOT NULL, " +
				FavoriteMoviesEntry.COLUMN_DATE_ADD + " INTEGER NOT NULL, " +
				FavoriteMoviesEntry.COLUMN_VOTES + " INTEGER NOT NULL DEFAULT 0 " +
				" );";


		sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);

		sqLiteDatabase.execSQL(SQL_CREATE_POPULAR_MOVIES_TABLE);

		sqLiteDatabase.execSQL(SQL_CREATE_TOP_RATED_MOVIES_TABLE);

		sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_MOVIES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        //onCreate(sqLiteDatabase);
    }

	public static ContentValues getContentValues(Movie movie) {
		Utils.assertNotNull(movie,"movie cannot be null");

		ContentValues values = new ContentValues();
		values.put(MovieEntry.COLUMN_MOVIESDB_ID, movie.getMoviesDbId());
		values.put(MovieEntry.COLUMN_TITLE, movie.getTitle());
		values.put(MovieEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
		values.put(MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate().getTime());
		values.put(MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
		values.put(MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
		values.put(MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());

		return values;

	}


	public static long addMovie(Context mContext, Movie movie, boolean moviesLanguageChanged) {

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

			if(moviesLanguageChanged) {

				log.debug("updating movie data due to language change");

				ContentValues movieValues = new ContentValues();

				movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
				movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
				movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());

				int updatedRows = mContext.getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI, movieValues
						, MovieContract.MovieEntry._ID+"=?", new String[]{Long.toString(movie.getId())});

			}

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
