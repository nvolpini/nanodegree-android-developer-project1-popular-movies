package app.popularmovies.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

}
