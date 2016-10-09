package app.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import app.popularmovies.Utils;
import app.popularmovies.data.MovieContract.FavoriteMoviesEntry;
import app.popularmovies.data.MovieContract.MovieEntry;
import app.popularmovies.data.MovieContract.PopularMoviesEntry;
import app.popularmovies.data.MovieContract.TopRatedMoviesEntry;
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
						"." + MovieContract.PopularMoviesEntry.COLUMN_MOVIE_ID);
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
				TopRatedMoviesEntry.COLUMN_POSITION + " INTEGER NOT NULL " +
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


}
