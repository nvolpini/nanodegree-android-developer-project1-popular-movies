package app.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by neimar on 25/09/16.
 */

public class MovieContract {

	private static final Logger log = LoggerFactory.getLogger(MovieContract.class);

    public static final String CONTENT_AUTHORITY = "app.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movies";

	public static final String PATH_POPULAR = "popular";

	public static final String PATH_TOP_RATED = "top_rated";

	public static final String PATH_FAVORITES = "favorites";

	public static final String PATH_VIDEOS = "videos";

	public static final String PATH_REVIEWS = "reviews";


	// To make it easy to query for the exact date, we normalize all dates that go into
	// the database to the start of the the Julian day at UTC.
	public static long normalizeDate(long startDate) {
		// normalize the start date to the beginning of the (UTC) day
		Time time = new Time();
		time.set(startDate);
		int julianDay = Time.getJulianDay(startDate, time.gmtoff);
		return time.setJulianDay(julianDay);
	}

	/**
	 * Contract for the movies table.
	 * Stores information about movies
	 */
    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

		public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        // Table name
        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_MOVIESDB_ID = "moviesdb_id";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_ORIGINAL_TITLE = "otiginal_title";

        public static final String COLUMN_OVERVIEW = "overview";

        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        public static final String COLUMN_POSTER_PATH = "poster_path";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

	}

	/**
	 * Default columns for tables that holds a list of movies.
	 */
	public interface ListOfMoviesEntry extends BaseColumns {

		/**
		 * reference to the movies_table
		 */
		public static final String COLUMN_MOVIE_ID = "movie_id";

		/**
		 * position on the list
		 */
		public static final String COLUMN_POSITION = "position";

	}

	/**
	 * Contract for the popular_movies table
	 * Holds the list of popular movies downloaded
	 */
	public static final class PopularMoviesEntry implements ListOfMoviesEntry {

		public static final Uri CONTENT_URI =
				BASE_CONTENT_URI.buildUpon()
						.appendPath(PATH_MOVIES)
						.appendPath(PATH_POPULAR)
						.build();

		public static final String CONTENT_TYPE =
				ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POPULAR;

		public static final String CONTENT_ITEM_TYPE =
				ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POPULAR;

		// Table name
		public static final String TABLE_NAME = "popular_movies";

		public static Uri buildMovieUri(long id) {
			return ContentUris.withAppendedId(CONTENT_URI, id);
		}

	}

	/**
	 * Contract for the top_rated_movies table
	 * Holds the list of top rated movies downloaded
	 */
	public static final class TopRatedMoviesEntry implements ListOfMoviesEntry {

		public static final Uri CONTENT_URI =
				BASE_CONTENT_URI.buildUpon()
						.appendPath(PATH_MOVIES)
						.appendPath(PATH_TOP_RATED)
						.build();

		public static final String CONTENT_TYPE =
				ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_RATED;

		public static final String CONTENT_ITEM_TYPE =
				ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_RATED;

		// Table name
		public static final String TABLE_NAME = "top_rated_movies";

		public static Uri buildMovieUri(long id) {
			return ContentUris.withAppendedId(CONTENT_URI, id);
		}
	}

	/**
	 * Contract for the favorite_movies table
	 * Holds the list of user's favorite movies
	 */
	public static final class FavoriteMoviesEntry implements ListOfMoviesEntry {

		public static final Uri CONTENT_URI =
				BASE_CONTENT_URI.buildUpon()
						.appendPath(PATH_MOVIES)
						.appendPath(PATH_FAVORITES)
						.build();

		public static final String CONTENT_TYPE =
				ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;

		public static final String CONTENT_ITEM_TYPE =
				ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;

		// Table name
		public static final String TABLE_NAME = "favorite_movies";

		public static final String COLUMN_VOTES = "votes";

		public static final String COLUMN_DATE_ADD = "date_add";

		public static Uri buildMovieUri(long id) {
			return ContentUris.withAppendedId(CONTENT_URI, id);
		}

	}


	public static final class VideoEntry implements BaseColumns {

		public static final Uri CONTENT_URI =
				BASE_CONTENT_URI.buildUpon()
						.appendPath(PATH_MOVIES)
						.appendPath(PATH_VIDEOS)
						.build();

		public static final String CONTENT_TYPE =
				ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEOS;

		public static final String CONTENT_ITEM_TYPE =
				ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEOS;

		// Table name
		public static final String TABLE_NAME = "movie_videos";

		public static final String COLUMN_MOVIE_ID = "movie_id";

		public static final String COLUMN_MOVIESDB_ID = "moviesdb_id";

		public static final String COLUMN_NAME = "name";

		public static final String COLUMN_LANGUAGE = "language";

		public static final String COLUMN_REGION = "region";

		public static final String COLUMN_SITE = "site";

		public static final String COLUMN_KEY = "key";

		public static final String COLUMN_SIZE = "size";

		public static final String COLUMN_TYPE = "type";


		public static Uri buildVideosUri(long id) {
			return ContentUris.withAppendedId(CONTENT_URI, id);
		}

		public static long getMovieIdFromUri(Uri uri) {
			log.trace("uri: {}, id: {}", uri, uri.getPathSegments().get(2));
			return Long.parseLong(uri.getPathSegments().get(2));
		}

	}


	public static final class ReviewEntry implements BaseColumns {

		public static final Uri CONTENT_URI =
				BASE_CONTENT_URI.buildUpon()
						.appendPath(PATH_MOVIES)
						.appendPath(PATH_REVIEWS).build();

		public static final String CONTENT_TYPE =
				ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;

		public static final String CONTENT_ITEM_TYPE =
				ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;

		// Table name
		public static final String TABLE_NAME = "movie_reviews";

		public static final String COLUMN_MOVIE_ID = "movie_id";

		public static final String COLUMN_MOVIESDB_ID = "moviesdb_id";

		public static final String COLUMN_AUTHOR = "author";

		public static final String COLUMN_CONTENT = "content";

		public static final String COLUMN_URL = "url";


		public static Uri buildReviewsUri(long id) {
			return ContentUris.withAppendedId(CONTENT_URI, id);
		}

		public static long getMovieIdFromUri(Uri uri) {
			return Long.parseLong(uri.getPathSegments().get(2));
		}
	}
}
