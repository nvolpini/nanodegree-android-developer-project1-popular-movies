package app.popularmovies.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import app.popularmovies.Utils;
import app.popularmovies.data.MoviesDbHelper;
import app.popularmovies.data.MoviesRepository;
import app.popularmovies.model.Movie;
import app.popularmovies.model.SearchParams;

/**
 * Created by neimar on 19/09/16.
 */
public class MoviesService {

    private static final Logger log = LoggerFactory.getLogger(MoviesService.class);

    public static final String SORT_BY_POPULARITY = "popularity.desc";

    public static final String SORT_BY_RATING = "vote_average.desc";

    public static final String DEFAULT_LANGUAGE = "en";

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

	/**
	 * @deprecated  usar parcelable
	 * @param movie
	 * @param bundle
	 */
    public void saveMovie(Movie movie, Bundle bundle) {

        if (movie == null || bundle == null) {
            throw new IllegalArgumentException("Movie and bundle cannot be null");
        }

        bundle.putInt(getMovieDataKey("id"),movie.getMoviesDbId());
        bundle.putString(getMovieDataKey("title"),movie.getTitle());
        bundle.putString(getMovieDataKey("originalTitle"),movie.getOriginalTitle());
        bundle.putString(getMovieDataKey("releaseDate"),movie.getReleaseDateString());
        bundle.putString(getMovieDataKey("overview"),movie.getOverview());
        bundle.putDouble(getMovieDataKey("voteAverage"),movie.getVoteAverage());
        bundle.putString(getMovieDataKey("posterPath"),movie.getPosterPath());


    }

	/**
	 * @deprecated  usar parcelable
	 * @param bundle
	 * @return
	 */
    public Movie restoreMovie(Bundle bundle) {

        if (bundle == null) {
            throw new IllegalArgumentException("Bundle cannot be null");
        }

        Movie movie = new Movie();

        movie.setMoviesDbId(bundle.getInt(getMovieDataKey("id")));
        movie.setTitle(bundle.getString(getMovieDataKey("title")));
        movie.setOriginalTitle(bundle.getString(getMovieDataKey("originalTitle")));
        movie.setReleaseDateString(bundle.getString(getMovieDataKey("releaseDate")));
        movie.setOverview(bundle.getString(getMovieDataKey("overview")));
        movie.setVoteAverage(bundle.getDouble(getMovieDataKey("voteAverage")));
        movie.setPosterPath(bundle.getString(getMovieDataKey("posterPath")));



        return movie;

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
                .setLanguage(DEFAULT_LANGUAGE)
                .setSortBy(SORT_BY_POPULARITY);
    }

    public List<? extends Movie> getSampleData() {

        ArrayList<Movie> m = new ArrayList<>();

        m.add(new Movie(1,"Movie 1", "Movie 1 ot", "Movie, movie, movie, movie movie text long text movie text long movie text again", Utils.toDate("2016-01-05"),8));
        m.add(new Movie(2,"Movie 2 long title for this movie"
                , "Movie 2 ot", "Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text againMovie 2 ot\", \"Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text againMovie 2 ot\", \"Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text againMovie 2 ot\", \"Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text againMovie 2 ot\", \"Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text again Movie 2 ot\", \"Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text againMovie 2 ot\", \"Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text againMovie 2 ot\", \"Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text againMovie 2 ot\", \"Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text againMovie 2 ot\", \"Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text again Movie 2 ot\", \"Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text againMovie 2 ot\", \"Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text againMovie 2 ot\", \"Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text againMovie 2 ot\", \"Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text againMovie 2 ot\", \"Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text again"
                , Utils.toDate("2016-01-05"),8));
        m.add(new Movie(3,"Movie 3", "Movie 3 ot", "Movie, movie, movie, movie movie text long text movie text long movie text again", Utils.toDate("2016-01-05"),8));
        m.add(new Movie(4,"Movie 4", "Movie 4 ot", "Movie, movie, movie, movie movie text long text movie text long movie text again", Utils.toDate("2016-01-05"),8));

        return m;


    }

	/**
	 * Fetch and save locally
	 * @param searchParams
	 */
	public void downloadAndSaveMovies(Context context, SearchParams searchParams) throws MoviesDataException {

		MoviesDbHelper dbHelper = new MoviesDbHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		List<Movie> movies = null;

		try {
			IMovieSearch search = MoviesService.get().newSearch(searchParams);

			movies = search.list();

		} catch (MoviesDataException e) {

			log.error("error fetching movies.",e);
			throw e;

		}
		Vector<ContentValues> regs = new Vector<ContentValues>(movies.size());

		for (Movie m: movies) {

			//ContentValues mv = new ContentValues();
			//regs.add(mv);

			MoviesRepository.get(context).create(m);

		}


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
