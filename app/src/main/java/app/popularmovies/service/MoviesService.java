package app.popularmovies.service;

import android.os.Bundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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







    public String getMoviePosterUrl(Movie movie) {

        return movie == null ? null : String.format("http://image.tmdb.org/t/p/w185/%s"
                ,movie.getPosterPath());

    }

    private String getMovieDataKey(String name) {
        return String.format("app.popularmovies.movie.%s",name);
    }

    public void saveMovie(Movie movie, Bundle bundle) {

        if (movie == null || bundle == null) {
            throw new IllegalArgumentException("Movie and bundle cannot be null");
        }

        bundle.putInt(getMovieDataKey("id"),movie.getId());
        bundle.putString(getMovieDataKey("title"),movie.getTitle());
        bundle.putString(getMovieDataKey("originalTitle"),movie.getOriginalTitle());
        bundle.putString(getMovieDataKey("releaseDate"),movie.getReleaseDate());
        bundle.putString(getMovieDataKey("overview"),movie.getOverview());
        bundle.putDouble(getMovieDataKey("voteAverage"),movie.getVoteAverage());
        bundle.putString(getMovieDataKey("posterPath"),movie.getPosterPath());


    }

    public Movie restoreMovie(Bundle bundle) {

        if (bundle == null) {
            throw new IllegalArgumentException("Bundle cannot be null");
        }

        Movie movie = new Movie();

        movie.setId(bundle.getInt(getMovieDataKey("id")));
        movie.setTitle(bundle.getString(getMovieDataKey("title")));
        movie.setOriginalTitle(bundle.getString(getMovieDataKey("originalTitle")));
        movie.setReleaseDate(bundle.getString(getMovieDataKey("releaseDate")));
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

        m.add(new Movie(1,"Movie 1", "Movie 1 ot", "Movie, movie, movie, movie movie text long text movie text long movie text again", "2016-01-05",8));
        m.add(new Movie(2,"Movie 2 long title for this movie"
                , "Movie 2 ot", "Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text againMovie 2 ot\", \"Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text againMovie 2 ot\", \"Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text againMovie 2 ot\", \"Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text againMovie 2 ot\", \"Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text again Movie 2 ot\", \"Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text againMovie 2 ot\", \"Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text againMovie 2 ot\", \"Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text againMovie 2 ot\", \"Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text againMovie 2 ot\", \"Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text again Movie 2 ot\", \"Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text againMovie 2 ot\", \"Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text againMovie 2 ot\", \"Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text againMovie 2 ot\", \"Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text againMovie 2 ot\", \"Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text again, Movie, movie, movie, movie movie text long text movie text long movie text again"
                , "2016-01-05",8));
        m.add(new Movie(3,"Movie 3", "Movie 3 ot", "Movie, movie, movie, movie movie text long text movie text long movie text again", "2016-01-05",8));
        m.add(new Movie(4,"Movie 4", "Movie 4 ot", "Movie, movie, movie, movie movie text long text movie text long movie text again", "2016-01-05",8));

        return m;


    }

}
