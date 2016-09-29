package app.popularmovies.data;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.test.AndroidTestCase;

/**
 * Created by neimar on 29/09/16.
 */

public class TestMoviesProvider extends AndroidTestCase {
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

}
