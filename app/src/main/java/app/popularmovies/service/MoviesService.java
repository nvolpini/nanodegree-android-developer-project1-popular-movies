package app.popularmovies.service;

import android.os.Bundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import app.popularmovies.BuildConfig;
import app.popularmovies.model.Movie;
import app.popularmovies.model.SearchParams;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbDiscover;
import info.movito.themoviedbapi.model.Discover;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

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





    private Movie convert(MovieDb md) {
        Movie m = new Movie();
        m.setId(md.getId());
        m.setTitle(md.getTitle());
        m.setReleaseDate(md.getReleaseDate());
        m.setPosterPath(md.getPosterPath());
        m.setOverview(md.getOverview());
        m.setOriginalTitle(md.getOriginalTitle());
        m.setVoteAverage(md.getVoteAverage());
        return m;
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
        bundle.putString(getMovieDataKey("releaseDate"),movie.getOriginalTitle());
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
        return new MoviesSearch(newSearchParams());

    }

    public IMovieSearch newSearch(SearchParams params) {
        return new MoviesSearch(params);

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


    class MoviesSearch implements IMovieSearch {

        private final SearchParams params;

        public MoviesSearch(SearchParams params) {
            this.params = params;
        }

        public SearchParams getParams() {
            return params;
        }

        @Override
        public IMovieSearch sortByPopularity() {
            this.sortBy(SORT_BY_POPULARITY);
            return this;
        }

        @Override
        public IMovieSearch sortByRating() {
            this.sortBy(SORT_BY_RATING);
            return this;
        }

        @Override
        public IMovieSearch sortBy(String sortBy) {
            this.params.setSortBy(sortBy);
            return this;
        }

        @Override
        public IMovieSearch withLanguage(String language) {
            this.params.setLanguage(language);
            return this;
        }

        @Override
        public List<Movie> list() {

            List<Movie> movies = new ArrayList<>();

            String sortBy = params.getSortBy();

            if (sortBy == null ||
                    (!SORT_BY_POPULARITY.equals(sortBy) && !SORT_BY_RATING.equals(sortBy) )) {
                log.warn("invalid sort: {}", sortBy);
                sortBy = SORT_BY_POPULARITY;
                params.setSortBy(sortBy);
            }



            TmdbApi moviesApi = new TmdbApi(BuildConfig.MOVIESDB_API_KEY);

            TmdbDiscover search = moviesApi.getDiscover();

            Discover discover = new Discover();
            discover.language(params.getLanguage());
            discover.year(2016);
            //discover.primaryReleaseYear(2016);
            //discover.getParams().put("primary_release_date.gte","2014-01-01");
            discover.page(1);
            //discover.includeAdult(false);

            discover.sortBy(params.getSortBy());

            log.debug("Fetching movies. Lang: {}, sort by: {} ",params.getLanguage()
                    , params.getSortBy());


            MovieResultsPage res = search.getDiscover(discover);

            for (MovieDb md : res.getResults()) {

                movies.add(convert(md));

            }

            return movies;

        }

        /**
         *
         * TODO create app exceptions
         *
         * @param movieId
         * @return movie or null if not found or error
         */
        @Override
        public Movie getMovieById(int movieId) {

            log.debug("Getting movie: {}",movieId);

            try {

                TmdbApi moviesApi = new TmdbApi(BuildConfig.MOVIESDB_API_KEY);

                MovieDb md = moviesApi.getMovies().getMovie(movieId, params.getLanguage());

                log.debug("movieId: {} found: {}",movieId,md.getTitle());

                return convert(md);

            } catch (Exception e) {

            }

            return null;
        }
    }
}
