package app.popularmovies.data;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

import app.popularmovies.utils.PollingCheck;

/**
 * Created by neimar on 25/09/16.
 */

public class TestUtilities extends AndroidTestCase {

    static final Integer TEST_MOVIEDB_ID = 99705;

    static final long TEST_DATE = 1419033600L;  // December 20th, 2014

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

  
    static ContentValues createMovieValues() {
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_MOVIESDB_ID, TEST_MOVIEDB_ID);
        values.put(MovieContract.MovieEntry.COLUMN_TITLE, "Filme Teste");
        values.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "Test Movie");
        values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, TEST_DATE);
        values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "Just a fictional movie");
        values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, 7.5);
        values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, "path");

        return values;
    }

    static ContentValues createMovieValues(int movieDbId) {
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_MOVIESDB_ID, movieDbId);
        values.put(MovieContract.MovieEntry.COLUMN_TITLE, "Test Movie "+movieDbId);
        values.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "Test Movie");
        values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, TEST_DATE);
        values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "Just a fictional movie");
        values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, 7.5);
        values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, "path");

        return values;
    }

    static ContentValues createPopularMoviesValues(long movieId, int position) {
        ContentValues values = new ContentValues();
        values.put(MovieContract.PopularMoviesEntry.COLUMN_MOVIE_ID, movieId);
        values.put(MovieContract.PopularMoviesEntry.COLUMN_POSITION, position);

        return values;
    }


    static ContentValues createTopRatedMoviesValues(long movieId, int position) {
        ContentValues values = new ContentValues();
        values.put(MovieContract.TopRatedMoviesEntry.COLUMN_MOVIE_ID, movieId);
        values.put(MovieContract.TopRatedMoviesEntry.COLUMN_POSITION, position);

        return values;
    }


    static ContentValues createFavoriteMoviesValues(long movieId, int position) {
        ContentValues values = new ContentValues();
        values.put(MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID, movieId);
        values.put(MovieContract.FavoriteMoviesEntry.COLUMN_POSITION, position);
        values.put(MovieContract.FavoriteMoviesEntry.COLUMN_VOTES, 5);
        values.put(MovieContract.FavoriteMoviesEntry.COLUMN_DATE_ADD, TEST_DATE);

        return values;
    }

    public static ContentValues createMovieVideoValues(long movieId, int i) {
        ContentValues values = new ContentValues();
        values.put(MovieContract.VideoEntry.COLUMN_MOVIE_ID, movieId);
        values.put(MovieContract.VideoEntry.COLUMN_MOVIESDB_ID, "abcd"+i);
        values.put(MovieContract.VideoEntry.COLUMN_NAME, "video"+i);
        values.put(MovieContract.VideoEntry.COLUMN_LANGUAGE, "pt");
        values.put(MovieContract.VideoEntry.COLUMN_REGION, "BR");
        values.put(MovieContract.VideoEntry.COLUMN_SITE, "youtube");
		values.put(MovieContract.VideoEntry.COLUMN_KEY, "abcd");
		values.put(MovieContract.VideoEntry.COLUMN_SIZE, 720);
		values.put(MovieContract.VideoEntry.COLUMN_TYPE, "Trailer");

        return values;
	}

	public static ContentValues createMovieReviewValues(long movieId, int i) {
		ContentValues values = new ContentValues();
		values.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);
		values.put(MovieContract.ReviewEntry.COLUMN_MOVIESDB_ID, "abcd"+i);
		values.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, "myself");
		values.put(MovieContract.ReviewEntry.COLUMN_CONTENT, "some review....");
		values.put(MovieContract.ReviewEntry.COLUMN_URL, "http://www.themoviesdb.com/review/");
		return values;
	}

	/*
		Students: The functions we provide inside of TestProvider use this utility class to test
		the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
		CTS tests.

		Note that this only tests that the onChange function is called; it does not test that the
		correct Uri is returned.
	 */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}