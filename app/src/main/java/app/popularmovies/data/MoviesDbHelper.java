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
import app.popularmovies.model.Review;
import app.popularmovies.model.Video;

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
			,MovieContract.MovieEntry.TABLE_NAME+"."+ MovieEntry.COLUMN_VIDEO
			,MovieContract.MovieEntry.TABLE_NAME+"."+ MovieEntry.COLUMN_RUNTIME
			,MovieContract.MovieEntry.TABLE_NAME+"."+ MovieEntry.COLUMN_DOWNLOADED
			,MovieContract.MovieEntry.TABLE_NAME+"."+ MovieEntry.COLUMN_VIDEOS_DOWNLOADED
			,MovieContract.MovieEntry.TABLE_NAME+"."+ MovieEntry.COLUMN_REVIEWS_DOWNLOADED
			,MovieContract.MovieEntry.TABLE_NAME+"."+ MovieEntry.COLUMN_DOWNLOAD_LANGUAGE

			, FavoriteMoviesEntry.TABLE_NAME+"."+ FavoriteMoviesEntry._ID+" as fav_id "
			, FavoriteMoviesEntry.TABLE_NAME+"."+ FavoriteMoviesEntry.COLUMN_POSITION
			, FavoriteMoviesEntry.TABLE_NAME+"."+ FavoriteMoviesEntry.COLUMN_DATE_ADD
			, FavoriteMoviesEntry.TABLE_NAME+"."+ FavoriteMoviesEntry.COLUMN_VOTES

	};


	public static final SQLiteQueryBuilder MOVIES_QUERY_BUILDER;

	static{
		MOVIES_QUERY_BUILDER = new SQLiteQueryBuilder();

		MOVIES_QUERY_BUILDER.setTables(
				MovieContract.MovieEntry.TABLE_NAME +
						" LEFT JOIN "+FavoriteMoviesEntry.TABLE_NAME +
						" ON "+FavoriteMoviesEntry.TABLE_NAME+
						"."+FavoriteMoviesEntry.COLUMN_MOVIE_ID +
						" = "+MovieEntry.TABLE_NAME+
						"."+MovieEntry._ID
		);
	}

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
				MovieEntry.COLUMN_VIDEO + " INTEGER NOT NULL, " +
				MovieEntry.COLUMN_RUNTIME + " INTEGER, " +
				MovieEntry.COLUMN_DOWNLOADED + " INTEGER NOT NULL, " +
				MovieEntry.COLUMN_VIDEOS_DOWNLOADED + " INTEGER, " +
				MovieEntry.COLUMN_REVIEWS_DOWNLOADED + " INTEGER, " +
				MovieEntry.COLUMN_DOWNLOAD_LANGUAGE + " TEXT NOT NULL, " +
				" UNIQUE (" + MovieEntry.COLUMN_MOVIESDB_ID + ") " +
				" ON CONFLICT REPLACE" +
                " );";


		final String SQL_CREATE_MOVIE_VIDEOS_TABLE = "CREATE TABLE " + MovieContract.VideoEntry.TABLE_NAME + " (" +
				MovieContract.VideoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				MovieContract.VideoEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL " +
				" REFERENCES "+MovieEntry.TABLE_NAME+" (" +MovieEntry._ID+") ON DELETE CASCADE, " +
				MovieContract.VideoEntry.COLUMN_MOVIESDB_ID + " TEXT NOT NULL, " +
				MovieContract.VideoEntry.COLUMN_NAME + " TEXT NOT NULL, " +
				MovieContract.VideoEntry.COLUMN_LANGUAGE + " TEXT NOT NULL, " +
				MovieContract.VideoEntry.COLUMN_REGION + " TEXT NOT NULL, " +
				MovieContract.VideoEntry.COLUMN_SITE + " TEXT NOT NULL, " +
				MovieContract.VideoEntry.COLUMN_KEY + " TEXT NOT NULL, " +
				MovieContract.VideoEntry.COLUMN_SIZE + " INTEGER NOT NULL, " +
				MovieContract.VideoEntry.COLUMN_TYPE + " TEXT NOT NULL, " +

				" UNIQUE (" + MovieContract.VideoEntry.COLUMN_MOVIESDB_ID + ") " +
				" ON CONFLICT REPLACE" +
				" );";

		final String SQL_CREATE_MOVIE_REVIEWS_TABLE = "CREATE TABLE " + MovieContract.ReviewEntry.TABLE_NAME + " (" +
				MovieContract.ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL " +
				" REFERENCES "+MovieEntry.TABLE_NAME+" (" +MovieEntry._ID+") ON DELETE CASCADE, " +
				MovieContract.ReviewEntry.COLUMN_MOVIESDB_ID + " TEXT NOT NULL, " +
				MovieContract.ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
				MovieContract.ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +
				MovieContract.ReviewEntry.COLUMN_URL + " TEXT NOT NULL, " +

				" UNIQUE (" + MovieContract.ReviewEntry.COLUMN_MOVIESDB_ID + ") " +
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

		sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_VIDEOS_TABLE);

		sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_REVIEWS_TABLE);

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

		values.put(MovieEntry.COLUMN_VIDEO, movie.isVideo() ? 1 : 0);
		values.put(MovieEntry.COLUMN_RUNTIME, movie.getRuntime());
		values.put(MovieEntry.COLUMN_DOWNLOADED, movie.getMovieDownloaded());
		values.put(MovieEntry.COLUMN_VIDEOS_DOWNLOADED, movie.getVideosDownloaded());
		values.put(MovieEntry.COLUMN_REVIEWS_DOWNLOADED, movie.getReviewsDownloaded());
		values.put(MovieEntry.COLUMN_DOWNLOAD_LANGUAGE, movie.getMovieDownloadLanguage());



		return values;

	}


	/**
	 * TODO REVER LANGUAGE CHANGE
	 * @param mContext
	 * @param movie
	 * @param moviesLanguageChanged
	 * @return
	 */
	public static long addMovie(Context mContext, Movie movie, boolean moviesLanguageChanged) {

		long movieId;
		String currentLang;

		// First, check if the movie already exists
		Cursor cursor = mContext.getContentResolver().query(
				MovieContract.MovieEntry.CONTENT_URI,
				new String[]{MovieEntry.TABLE_NAME+"."+MovieContract.MovieEntry._ID
						, MovieEntry.TABLE_NAME+"."+ MovieEntry.COLUMN_DOWNLOAD_LANGUAGE},
				MovieEntry.TABLE_NAME+"."+MovieContract.MovieEntry.COLUMN_MOVIESDB_ID + " = ?",
				new String[]{Integer.toString(movie.getMoviesDbId())},
				null);

		if (cursor.moveToFirst()) {
			movieId = cursor.getLong(cursor.getColumnIndex(MovieContract.MovieEntry._ID));
			currentLang = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_DOWNLOAD_LANGUAGE));
			movie.setId(movieId);

			if(moviesLanguageChanged || !currentLang.equals(movie.getMovieDownloadLanguage())) {

				log.debug("updating movie data due to language change");

				ContentValues movieValues = new ContentValues();

				movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
				movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
				movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());

				movieValues.put(MovieEntry.COLUMN_DOWNLOADED, new Date().getTime());
				movieValues.put(MovieEntry.COLUMN_DOWNLOAD_LANGUAGE, movie.getMovieDownloadLanguage());


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


	/**
	 * @param movie
	 * @param activated
	 * @return
	 */
	public static FavoriteInformation setFavorite(Context context, Movie movie, boolean activated) {

		log.trace("set favorite, movieId: {}, activated: {}",movie.getId(), activated);

		FavoriteInformation f = new FavoriteInformation();

		// First, check if the movie already exists
		Cursor cursor = context.getContentResolver().query(
				MovieContract.FavoriteMoviesEntry.CONTENT_URI,
				new String[]{
						MovieContract.FavoriteMoviesEntry.TABLE_NAME+ "."+
								MovieContract.FavoriteMoviesEntry._ID},
				MovieContract.FavoriteMoviesEntry.TABLE_NAME+ "."+
						MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID + " = ?",
				new String[]{Long.toString(movie.getId())},
				null);

		if (cursor.moveToFirst()) {
			int movieIdIndex = cursor.getColumnIndex(MovieContract.FavoriteMoviesEntry._ID);
			f.setId(cursor.getLong(movieIdIndex));
			log.trace("movie is already a favorite, movieId: {}, favId: {}",movie.getId(), f.getId());

			if (!activated) {
				log.debug("removing favorite: {}", f.getId());

				context.getContentResolver().delete(MovieContract.FavoriteMoviesEntry.CONTENT_URI
						,MovieContract.FavoriteMoviesEntry.TABLE_NAME+ "."+
								MovieContract.FavoriteMoviesEntry._ID+"=?",new String[]{Long.toString(f.getId())});

				movie.setFavoriteInformation(null);
			}

		} else {

			f.setDateAdded(new Date());
			f.setVotes(0);
			f.setPosition(1);
			movie.setFavoriteInformation(f);

			ContentValues movieValues = new ContentValues();
			movieValues.put(MovieContract.FavoriteMoviesEntry.COLUMN_DATE_ADD,f.getDateAdded().getTime());
			movieValues.put(MovieContract.FavoriteMoviesEntry.COLUMN_VOTES,f.getVotes());
			movieValues.put(MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID, movie.getId());
			movieValues.put(MovieContract.FavoriteMoviesEntry.COLUMN_POSITION,f.getPosition());

			Uri insertedUri = context.getContentResolver().insert(
					MovieContract.FavoriteMoviesEntry.CONTENT_URI,
					movieValues
			);

			// The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
			f.setId(ContentUris.parseId(insertedUri));
			log.trace("favorite movie added: {}",movie);
		}

		cursor.close();

		return f;
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

		m.setVideo(cursor.getInt(8) == 1);
		m.setRuntime(cursor.getInt(9));
		m.setMovieDownloaded(cursor.getLong(10));
		m.setVideosDownloaded(cursor.getLong(11));
		m.setReviewsDownloaded(cursor.getLong(12));
		m.setMovieDownloadLanguage(cursor.getString(13));

		FavoriteInformation f = new FavoriteInformation();

		int fidi = cursor.getColumnIndex("fav_id");

		//log.trace("favorite id col index: {}",fidi);

		if (fidi >= 0 && !cursor.isNull(fidi)) {

			f.setId(cursor.getLong(fidi));
			f.setPosition(cursor.getInt(15));
			f.setDateAdded(new Date(cursor.getLong(16)));
			f.setVotes(cursor.getInt(17));

			//log.trace("Movie is a favorite, fid: {}, movieId: {}",f.getId(), m.getId());

			m.setFavoriteInformation(f);

		}

		//log.trace("Movie from cursor: {}",m);

		return m;

	}

	public static ContentValues getContentValues(Video video) {
		Utils.assertNotNull(video,"video cannot be null");

		ContentValues values = new ContentValues();
		values.put(MovieContract.VideoEntry.COLUMN_MOVIE_ID, video.getMovieId());

		values.put(MovieContract.VideoEntry.COLUMN_MOVIESDB_ID, video.getMoviesDbId());
		values.put(MovieContract.VideoEntry.COLUMN_NAME, video.getName());
		values.put(MovieContract.VideoEntry.COLUMN_LANGUAGE, video.getLanguage());
		values.put(MovieContract.VideoEntry.COLUMN_REGION, video.getRegion());
		values.put(MovieContract.VideoEntry.COLUMN_SITE, video.getSite());
		values.put(MovieContract.VideoEntry.COLUMN_KEY, video.getKey());
		values.put(MovieContract.VideoEntry.COLUMN_SIZE, video.getSize());
		values.put(MovieContract.VideoEntry.COLUMN_TYPE, video.getType());

		return values;

	}

	public static Video cursorToVideo(Cursor cursor) {

		Video video = new Video();

		video.setId(cursor.getLong(0));
		video.setMovieId(cursor.getLong(1));
		video.setMoviesDbId(cursor.getString(2));
		video.setName(cursor.getString(3));
		video.setLanguage(cursor.getString(4));
		video.setRegion(cursor.getString(5));
		video.setSite(cursor.getString(6));
		video.setKey(cursor.getString(7));
		video.setSize(cursor.getInt(8));
		video.setType(cursor.getString(9));

		return video;

	}


	public static ContentValues getContentValues(Review review) {
		Utils.assertNotNull(review,"review cannot be null");

		ContentValues values = new ContentValues();
		values.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, review.getMovieId());

		values.put(MovieContract.ReviewEntry.COLUMN_MOVIESDB_ID, review.getMoviesDbId());
		values.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, review.getAuthor());
		values.put(MovieContract.ReviewEntry.COLUMN_CONTENT, review.getContent());
		values.put(MovieContract.ReviewEntry.COLUMN_URL, review.getUrl());

		return values;

	}

	public static Review cursorToReview(Cursor cursor) {

		Review video = new Review();

		video.setId(cursor.getLong(0));
		video.setMovieId(cursor.getLong(1));
		video.setMoviesDbId(cursor.getString(2));
		video.setAuthor(cursor.getString(3));
		video.setContent(cursor.getString(4));
		video.setUrl(cursor.getString(5));

		return video;

	}


}
