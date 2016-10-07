package app.popularmovies.service;

import android.content.Context;
import android.database.Cursor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import app.popularmovies.data.MovieContract;
import app.popularmovies.data.MoviesDbHelper;
import app.popularmovies.data.MoviesRepository;
import app.popularmovies.model.Movie;
import app.popularmovies.model.SearchParams;

/**
 * Created by neimar on 19/09/16.
 */
public class MoviesService {

    private static final Logger log = LoggerFactory.getLogger(MoviesService.class);

	private static MoviesService INSTANCE = null;

    public static synchronized MoviesService get() {

        if (INSTANCE == null) {
            INSTANCE = new MoviesService();
        }

        return INSTANCE;
    }

    private MoviesService() {
    }


	/**
	 * TODO buscar uma imagem maior dependendo do tamanho da tela do dispositivo.
	 *
	 * @param movie
	 * @return
	 */
    public String getMoviePosterUrl(Movie movie) {

        return movie == null ? null : String.format("http://image.tmdb.org/t/p/w500/%s"
                ,movie.getPosterPath());

    }

    private String getMovieDataKey(String name) {
        return String.format("app.popularmovies.movie.%s",name);
    }



    public IMovieSearch newSearch() {

		return newSearch(newSearchParams());

    }

    public IMovieSearch newSearch(SearchParams params) {

		//to swap implementations, just change here

		//return new TheMovieDBAPISearchImpl(params);

		return new RetrofitSearchImpl(params);
    }

    /**
     *
     * @return Default params
     */
    public SearchParams newSearchParams() {
        return new SearchParams()
                .setLanguage(IMovieSearch.DEFAULT_LANGUAGE)
                .setSortBy(IMovieSearch.SORT_BY_POPULARITY);
    }

	/**
	 * Fetch and save locally
	 * @param searchParams
	 */
	public void downloadAndSaveMovies(Context context, SearchParams searchParams) throws MoviesDataException {

		MoviesDbHelper dbHelper = new MoviesDbHelper(context);
		//SQLiteDatabase db = dbHelper.getWritableDatabase();

		MoviesRepository repo = MoviesRepository.get(context);

		List<Movie> movies = null;

		try {
			IMovieSearch search = MoviesService.get().newSearch(searchParams);

			movies = search.list();

		} catch (MoviesDataException e) {

			log.error("error fetching movies.",e);
			throw e;

		}
		//Vector<ContentValues> regs = new Vector<ContentValues>(movies.size());

		for (Movie m: movies) {

			//ContentValues mv = new ContentValues();
			//regs.add(mv);

			if (!repo.exists(m.getMoviesDbId())) {
				repo.create(m);
			}



		}

		if(searchParams.isSortByPopularity()) {

			repo.savePopular(movies);

		} else if(searchParams.isSortByRating()) {

			repo.saveTopRated(movies);

		}


		Cursor c = context.getContentResolver().query(MovieContract.PopularMoviesEntry.CONTENT_URI, null, null, null, null);

		c.moveToFirst();
		do {

			log.debug("movie: {}", MoviesDbHelper.cursorToMovie(c));

		} while( c.moveToNext() );

/*
		int inserted = 0;
		// add to database
		if ( regs.size() > 0 ) {
			ContentValues[] cvArray = new ContentValues[regs.size()];
			regs.toArray(cvArray);
			//inserted = context.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
		}

		log.debug("Inserted {} movies",inserted);*/
	}

}
