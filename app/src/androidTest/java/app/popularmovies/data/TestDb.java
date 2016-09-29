/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.popularmovies.data;

import android.test.AndroidTestCase;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;

public class TestDb extends AndroidTestCase {

    private static final Logger log = LoggerFactory.getLogger(TestDb.class);


    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(MoviesDbHelper.DATABASE_NAME);
    }


    public void setUp() {
        deleteTheDatabase();
    }


    public void testCreateDb() throws Throwable {

        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.PopularMoviesEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.TopRatedMoviesEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.FavoriteMoviesEntry.TABLE_NAME);

        mContext.deleteDatabase(MoviesDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MoviesDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without the movie table",
                tableNameHashSet.isEmpty());

		checkTableMovies(db);
		checkTablePopularMovies(db);
		checkTableTopRatedMovies(db);
		checkTableFavoriteMovies(db);

        db.close();
    }


	public void testInsertTables() {
		MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		long movieId = doInsertTable(db, MovieContract.MovieEntry.TABLE_NAME,TestUtilities.createMovieValues());

		doInsertTable(db, MovieContract.PopularMoviesEntry.TABLE_NAME,TestUtilities.createPopularMoviesValues(movieId,1));

		doInsertTable(db, MovieContract.TopRatedMoviesEntry.TABLE_NAME,TestUtilities.createTopRatedMoviesValues(movieId,1));

		doInsertTable(db, MovieContract.FavoriteMoviesEntry.TABLE_NAME,TestUtilities.createFavoriteMoviesValues(movieId,1));

		dbHelper.close();

	}



	private void checkTableMovies(SQLiteDatabase db) {
		final HashSet<String> columns = new HashSet<String>();
		columns.add(MovieContract.MovieEntry._ID);
		columns.add(MovieContract.MovieEntry.COLUMN_MOVIESDB_ID);
		columns.add(MovieContract.MovieEntry.COLUMN_TITLE);
		columns.add(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
		columns.add(MovieContract.MovieEntry.COLUMN_OVERVIEW);
		columns.add(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
		columns.add(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
		columns.add(MovieContract.MovieEntry.COLUMN_POSTER_PATH);

		checkTable(db,MovieContract.MovieEntry.TABLE_NAME,columns);
	}

	private void checkTablePopularMovies(SQLiteDatabase db) {
		final HashSet<String> columns = new HashSet<String>();
		columns.add(MovieContract.PopularMoviesEntry._ID);
		columns.add(MovieContract.PopularMoviesEntry.COLUMN_MOVIE_ID);
		columns.add(MovieContract.PopularMoviesEntry.COLUMN_POSITION);

		checkTable(db,MovieContract.PopularMoviesEntry.TABLE_NAME,columns);
	}


	private void checkTableTopRatedMovies(SQLiteDatabase db) {
		final HashSet<String> columns = new HashSet<String>();
		columns.add(MovieContract.TopRatedMoviesEntry._ID);
		columns.add(MovieContract.TopRatedMoviesEntry.COLUMN_MOVIE_ID);
		columns.add(MovieContract.TopRatedMoviesEntry.COLUMN_POSITION);

		checkTable(db,MovieContract.TopRatedMoviesEntry.TABLE_NAME,columns);
	}

	private void checkTableFavoriteMovies(SQLiteDatabase db) {
		final HashSet<String> columns = new HashSet<String>();
		columns.add(MovieContract.FavoriteMoviesEntry._ID);
		columns.add(MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID);
		columns.add(MovieContract.FavoriteMoviesEntry.COLUMN_POSITION);
		columns.add(MovieContract.FavoriteMoviesEntry.COLUMN_VOTES);
		columns.add(MovieContract.FavoriteMoviesEntry.COLUMN_DATE_ADD);

		checkTable(db,MovieContract.FavoriteMoviesEntry.TABLE_NAME,columns);
	}


	private void checkTable(SQLiteDatabase db, String tableName, HashSet<String> expectedTableColumns) {
		Cursor c = db.rawQuery("PRAGMA table_info(" + tableName + ")",
				null);

		assertTrue("Error: This means that we were unable to query the database for table information.",
				c.moveToFirst());

		int columnNameIndex = c.getColumnIndex("name");
		do {
			String columnName = c.getString(columnNameIndex);
			expectedTableColumns.remove(columnName);
		} while(c.moveToNext());

		// if this fails, it means that your database doesn't contain all of the required movie
		// entry columns
		assertTrue("Error: The database doesn't contain all of the required columns for table "+tableName,
				expectedTableColumns.isEmpty());
	}


	private long doInsertTable(SQLiteDatabase db, String tableName, ContentValues values) {



		long id = db.insert(tableName, null, values);
		assertTrue(id != -1);

		Cursor cursor = db.query(
				tableName,  // Table to Query
				null, // leaving "columns" null just returns all the columns.
				null, // cols for "where" clause
				null, // values for "where" clause
				null, // columns to group by
				null, // columns to filter by row groups
				null  // sort order
		);

		assertTrue( "Error: No Records returned for table: "+tableName, cursor.moveToFirst() );

		TestUtilities.validateCurrentRecord("testInsertReadDb failed to validate table: "+tableName,
				cursor, values);

		assertFalse( "Error: More than one record returned for table: "+tableName,
				cursor.moveToNext() );

		cursor.close();


		return id;
	}







}
