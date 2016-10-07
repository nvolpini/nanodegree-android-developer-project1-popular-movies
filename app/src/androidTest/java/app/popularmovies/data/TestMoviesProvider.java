package app.popularmovies.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by neimar on 29/09/16.
 */

public class TestMoviesProvider extends AndroidTestCase {

	private static final Logger log = LoggerFactory.getLogger(TestMoviesProvider.class);

	/*
		  This helper function deletes all records from both database tables using the ContentProvider.
		  It also queries the ContentProvider to make sure that the database has been successfully
		  deleted, so it cannot be used until the Query and Delete functions have been written
		  in the ContentProvider.
		*/
	public void deleteAllRecordsFromProvider() {

		mContext.getContentResolver().delete(
				MovieContract.PopularMoviesEntry.CONTENT_URI,
				null,
				null
		);

		mContext.getContentResolver().delete(
				MovieContract.TopRatedMoviesEntry.CONTENT_URI,
				null,
				null
		);

		mContext.getContentResolver().delete(
				MovieContract.FavoriteMoviesEntry.CONTENT_URI,
				null,
				null
		);

		mContext.getContentResolver().delete(
				MovieContract.MovieEntry.CONTENT_URI,
				null,
				null
		);

		Cursor cursor = mContext.getContentResolver().query(
				MovieContract.PopularMoviesEntry.CONTENT_URI,
				null,
				null,
				null,
				null
		);
		assertEquals("Error: Records not deleted from popular table during delete", 0, cursor.getCount());
		cursor.close();

		cursor = mContext.getContentResolver().query(
				MovieContract.TopRatedMoviesEntry.CONTENT_URI,
				null,
				null,
				null,
				null
		);
		assertEquals("Error: Records not deleted from top_rated table during delete", 0, cursor.getCount());
		cursor.close();

		cursor = mContext.getContentResolver().query(
				MovieContract.FavoriteMoviesEntry.CONTENT_URI,
				null,
				null,
				null,
				null
		);
		assertEquals("Error: Records not deleted from favorite table during delete", 0, cursor.getCount());
		cursor.close();

		cursor = mContext.getContentResolver().query(
				MovieContract.MovieEntry.CONTENT_URI,
				null,
				null,
				null,
				null
		);
		assertEquals("Error: Records not deleted from Movies table during delete", 0, cursor.getCount());
		cursor.close();
	}

	public void deleteAllRecords() {
		deleteAllRecordsFromProvider();
	}

	// Since we want each test to start with a clean slate, run deleteAllRecords
	// in setUp (called by the test runner before each test).
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		deleteAllRecords();
	}

	/*
        This test checks to make sure that the content provider is registered correctly.
     */
	public void testProviderRegistry() {
		PackageManager pm = mContext.getPackageManager();

		// We define the component name based on the package name from the context and the
		// MoviesProvider class.
		ComponentName componentName = new ComponentName(mContext.getPackageName(),
				MoviesProvider.class.getName());
		try {
			// Fetch the provider info using the component name from the PackageManager
			// This throws an exception if the provider isn't registered.
			ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

			// Make sure that the registered authority matches the authority from the Contract.
			assertEquals("Error: MoviesProvider registered with authority: " + providerInfo.authority +
							" instead of authority: " + MovieContract.CONTENT_AUTHORITY,
					providerInfo.authority, MovieContract.CONTENT_AUTHORITY);

		} catch (PackageManager.NameNotFoundException e) {
			// I guess the provider isn't registered correctly.
			assertTrue("Error: MoviesProvider not registered at " + mContext.getPackageName(),
					false);
		}
	}



	public void testGetType() {

		// content://app.popularmovies/movies/
		String type = mContext.getContentResolver().getType(MovieContract.MovieEntry.CONTENT_URI);
		// vnd.android.cursor.dir/app.popularmovies/movies
		assertEquals("Error: the MovieEntry CONTENT_URI should return MovieEntry.CONTENT_TYPE",
				MovieContract.MovieEntry.CONTENT_TYPE, type);

		long movieId = 1L; //
		// content://app.popularmovies/movies/94074/20140612
		type = mContext.getContentResolver().getType(
				MovieContract.MovieEntry.buildMovieUri(movieId));
		// vnd.android.cursor.item/app.popularmovies/movies/1419120000
		assertEquals("Error: the MovieEntry CONTENT_URI with location and date should return MovieEntry.CONTENT_ITEM_TYPE",
				MovieContract.MovieEntry.CONTENT_ITEM_TYPE, type);

		// content://app.popularmovies/movies/popular/
		type = mContext.getContentResolver().getType(MovieContract.PopularMoviesEntry.CONTENT_URI);
		// vnd.android.cursor.dir/app.popularmovies/movies/popular
		assertEquals("Error: the PopularMoviesEntry CONTENT_URI should return PopularMoviesEntry.CONTENT_TYPE",
				MovieContract.PopularMoviesEntry.CONTENT_TYPE, type);

		// content://app.popularmovies/movies/top_rated/
		type = mContext.getContentResolver().getType(MovieContract.TopRatedMoviesEntry.CONTENT_URI);
		// vnd.android.cursor.dir/app.popularmovies/movies/top_rated
		assertEquals("Error: the TopRatedMoviesEntry CONTENT_URI should return TopRatedMoviesEntry.CONTENT_TYPE",
				MovieContract.TopRatedMoviesEntry.CONTENT_TYPE, type);

		// content://app.popularmovies/movies/favorites/
		type = mContext.getContentResolver().getType(MovieContract.FavoriteMoviesEntry.CONTENT_URI);
		// vnd.android.cursor.dir/app.popularmovies/movies/favorites
		assertEquals("Error: the FavoriteMoviesEntry CONTENT_URI should return FavoriteMoviesEntry.CONTENT_TYPE",
				MovieContract.FavoriteMoviesEntry.CONTENT_TYPE, type);


	}


	public void testBasicMoviesQuery() {

		// insert our test records into the database
		MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		ContentValues testValues = TestUtilities.createMovieValues();

		long id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, testValues);
		assertTrue("Unable to Insert MovieEntry into the Database", id != -1);

		db.close();

		// Test the basic content provider query
		Cursor cursor = mContext.getContentResolver().query(
				MovieContract.MovieEntry.CONTENT_URI,
				null,
				null,
				null,
				null
		);

		// Make sure we get the correct cursor out of the database
		TestUtilities.validateCursor("testBasicMoviesQuery", cursor, testValues);
	}


	public void testBasicMovieRetrieve() {

		// insert our test records into the database
		MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		ContentValues testValues = TestUtilities.createMovieValues();

		long id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, testValues);
		assertTrue("Unable to Insert MovieEntry into the Database", id != -1);

		db.close();

		// Test the basic content provider query
		Cursor cursor = mContext.getContentResolver().query(
				MovieContract.MovieEntry.buildMovieUri(id),
				null,
				null,
				null,
				null
		);

		// Make sure we get the correct cursor out of the database
		TestUtilities.validateCursor("testBasicMoviesQuery", cursor, testValues);
	}

	public void testUpdateMovie() {
		// Create a new map of values, where column names are the keys
		ContentValues values = TestUtilities.createMovieValues();

		Uri movieUri = mContext.getContentResolver().
				insert(MovieContract.MovieEntry.CONTENT_URI, values);
		long movieId = ContentUris.parseId(movieUri);

		// Verify we got a row back.
		assertTrue(movieId != -1);
		log.debug("New row id: {}", movieId);

		ContentValues updatedValues = new ContentValues(values);
		updatedValues.put(MovieContract.MovieEntry._ID, movieId);
		updatedValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, 8.8);

		// Create a cursor with observer to make sure that the content provider is notifying
		// the observers as expected
		Cursor locationCursor = mContext.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);

		TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
		locationCursor.registerContentObserver(tco);

		int count = mContext.getContentResolver().update(
				MovieContract.MovieEntry.CONTENT_URI, updatedValues, MovieContract.MovieEntry._ID + "= ?",
				new String[] { Long.toString(movieId)});
		assertEquals(count, 1);

		// Test to make sure our observer is called.  If not, we throw an assertion.
		//
		// Students: If your code is failing here, it means that your content provider
		// isn't calling getContext().getContentResolver().notifyChange(uri, null);
		tco.waitForNotificationOrFail();

		locationCursor.unregisterContentObserver(tco);
		locationCursor.close();

		// A cursor is your primary interface to the query results.
		Cursor cursor = mContext.getContentResolver().query(
				MovieContract.MovieEntry.CONTENT_URI,
				null,   // projection
				MovieContract.MovieEntry._ID + " = " + movieId,
				null,   // Values for the "where" clause
				null    // sort order
		);

		TestUtilities.validateCursor("testUpdateMovie.  Error validating movie entry update.",
				cursor, updatedValues);

		cursor.close();
	}

	public void testPopularMoviesList() {

	// insert our test records into the database
		MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
		SQLiteDatabase db = dbHelper.getWritableDatabase();


		ContentValues movie1 = TestUtilities.createMovieValues(1);
		ContentValues movie2 = TestUtilities.createMovieValues(2);

		long movieId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, movie1);
		db.insert(MovieContract.PopularMoviesEntry.TABLE_NAME, null,TestUtilities.createPopularMoviesValues(movieId,1));


		movieId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, movie2);
		db.insert(MovieContract.PopularMoviesEntry.TABLE_NAME, null,TestUtilities.createPopularMoviesValues(movieId,2));



		// Test the basic content provider query
		Cursor cursor = mContext.getContentResolver().query(
				MovieContract.PopularMoviesEntry.CONTENT_URI,
				null,
				null,
				null,
				null
		);


		assertTrue( "Erro no results", cursor.moveToFirst() );


		TestUtilities.validateCurrentRecord("erro",cursor,movie1);
		cursor.moveToNext();
		TestUtilities.validateCurrentRecord("erro",cursor,movie2);

		assertFalse("more than 2 returned", cursor.moveToNext());

		cursor.close();
		dbHelper.close();
	}
}
