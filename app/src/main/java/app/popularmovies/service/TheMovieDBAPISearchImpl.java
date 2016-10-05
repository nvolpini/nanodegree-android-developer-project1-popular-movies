package app.popularmovies.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import app.popularmovies.BuildConfig;
import app.popularmovies.Utils;
import app.popularmovies.model.Movie;
import app.popularmovies.model.SearchParams;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

import static app.popularmovies.service.MoviesService.SORT_BY_POPULARITY;

/**
 * Implementation based on the library TheMovieDB API (https://github.com/holgerbrandl/themoviedbapi)
 *
 * Created by neimar on 26/09/16.
 */

public class TheMovieDBAPISearchImpl extends AbstractSearchImpl {

	private static final Logger log = LoggerFactory.getLogger(TheMovieDBAPISearchImpl.class);

	public TheMovieDBAPISearchImpl(SearchParams params) {
		super(params);
	}

	@Override
	public List<Movie> list() throws MoviesDataException {

		List<Movie> movies = new ArrayList<>();

		validate();

		try {

			TmdbApi moviesApi = new TmdbApi(BuildConfig.MOVIESDB_API_KEY);


			/**
			 * DO NOT use discover service
			 */
			/*
            TmdbDiscover search = moviesApi.getDiscover();

            Discover discover = new Discover();
            discover.language(params.getLanguage());
            discover.year(2016);
            discover.page(1);
            discover.sortBy(params.getSortBy());
            MovieResultsPage res = search.getDiscover(discover);
            */

			MovieResultsPage res;

			if (params.getSortBy().equals(SORT_BY_POPULARITY)) {

				res = moviesApi.getMovies().getPopularMovies(params.getLanguage(), 1);

			} else {

				res = moviesApi.getMovies().getTopRatedMovies(params.getLanguage(), 1);

			}


			log.debug("Fetching movies. Lang: {}, sort by: {} ", params.getLanguage()
					, params.getSortBy());


			for (MovieDb md : res.getResults()) {

				movies.add(convert(md));

			}

			return movies;

		} catch (Exception e) {
			log.error("error querying movies.",e);
			throw new MoviesDataException("Error querying movies",e);
		}

	}

	/**
	 *
	 *
	 * @param movieId
	 * @return movie or null if not found or error
	 */
	@Override
	public Movie getMovieById(int movieId) throws MoviesDataException {

		log.debug("Getting movie: {}",movieId);

		try {

			TmdbApi moviesApi = new TmdbApi(BuildConfig.MOVIESDB_API_KEY);

			MovieDb md = moviesApi.getMovies().getMovie(movieId, params.getLanguage());

			log.debug("movieId: {} found: {}",movieId,md.getTitle());

			return convert(md);

		} catch (Exception e) {
			log.error("error querying movie by id: {}",movieId,e);
			throw new MoviesDataException("Error querying movie by id: "+movieId,e);
		}

	}


	private Movie convert(MovieDb md) {
		Movie m = new Movie();
		m.setMoviesDbId(md.getId());
		m.setTitle(md.getTitle());
		//m.setReleaseDateString(md.getReleaseDate());
		m.setReleaseDate(Utils.toDate(md.getReleaseDate()));
		m.setPosterPath(md.getPosterPath());
		m.setOverview(md.getOverview());
		m.setOriginalTitle(md.getOriginalTitle());
		m.setVoteAverage(md.getVoteAverage());
		return m;
	}

}