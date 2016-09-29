package app.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.popularmovies.data.MovieContract.FavoriteMoviesEntry;
import app.popularmovies.data.MovieContract.MovieEntry;
import app.popularmovies.data.MovieContract.PopularMoviesEntry;
import app.popularmovies.data.MovieContract.TopRatedMoviesEntry;

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

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieEntry.COLUMN_MOVIESDB_ID + " INTEGER UNIQUE NOT NULL, " +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL " +
                " );";

        final String SQL_CREATE_POPULAR_MOVIES_TABLE = "CREATE TABLE " + PopularMoviesEntry.TABLE_NAME + " (" +
				PopularMoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				PopularMoviesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL REFERENCES "+MovieEntry.TABLE_NAME+", " +
				PopularMoviesEntry.COLUMN_POSITION + " INTEGER NOT NULL " +
                " );";



		final String SQL_CREATE_TOP_RATED_MOVIES_TABLE = "CREATE TABLE " + TopRatedMoviesEntry.TABLE_NAME + " (" +
				TopRatedMoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				TopRatedMoviesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL REFERENCES "+MovieEntry.TABLE_NAME+", " +
				TopRatedMoviesEntry.COLUMN_POSITION + " INTEGER NOT NULL " +
				" );";



		final String SQL_CREATE_FAVORITE_MOVIES_TABLE = "CREATE TABLE " + FavoriteMoviesEntry.TABLE_NAME + " (" +
				FavoriteMoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				FavoriteMoviesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL REFERENCES "+MovieEntry.TABLE_NAME+", " +
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
}
