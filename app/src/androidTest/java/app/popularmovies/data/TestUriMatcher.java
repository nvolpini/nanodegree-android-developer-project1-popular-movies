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

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

public class TestUriMatcher extends AndroidTestCase {

    private static final long TEST_MOVIE_ID = 10L;

    // content://app.popularmovies/movies"
    private static final Uri TEST_MOVIES_DIR = MovieContract.MovieEntry.CONTENT_URI;

	// content://app.popularmovies/movies/#"
	private static final Uri TEST_MOVIE_ITEM = MovieContract.MovieEntry.buildMovieUri(TEST_MOVIE_ID);

	// content://app.popularmovies/movies/popular"
	private static final Uri TEST_POPULAR_MOVIES_DIR = MovieContract.PopularMoviesEntry.CONTENT_URI;

	// content://app.popularmovies/movies/top_rated"
	private static final Uri TEST_TOP_RATED_MOVIES_DIR = MovieContract.TopRatedMoviesEntry.CONTENT_URI;

	// content://app.popularmovies/movies/favorites"
	private static final Uri TEST_FAVORITE_MOVIES_DIR = MovieContract.FavoriteMoviesEntry.CONTENT_URI;

	public void testUriMatcher() {
        UriMatcher testMatcher = MoviesProvider.buildUriMatcher();

        assertEquals("Error: The MOVIES URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIES_DIR), MoviesProvider.MOVIES);

		assertEquals("Error: The MOVIE ITEM URI was matched incorrectly.",
				testMatcher.match(TEST_MOVIE_ITEM), MoviesProvider.MOVIE_ID);

		assertEquals("Error: The POPULAR MOVIES URI was matched incorrectly.",
				testMatcher.match(TEST_POPULAR_MOVIES_DIR), MoviesProvider.POPULAR_MOVIES);

		assertEquals("Error: The TOP RATED MOVIES URI was matched incorrectly.",
				testMatcher.match(TEST_TOP_RATED_MOVIES_DIR), MoviesProvider.TOP_RATED_MOVIES);

		assertEquals("Error: The FAVORITE MOVIES URI was matched incorrectly.",
				testMatcher.match(TEST_FAVORITE_MOVIES_DIR), MoviesProvider.FAVORITE_MOVIES);

	}
}
