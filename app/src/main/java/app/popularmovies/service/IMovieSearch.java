package app.popularmovies.service;

import java.util.List;

import app.popularmovies.model.Movie;

/**
 * Created by neimar on 19/09/16.
 */
public interface IMovieSearch {

    IMovieSearch sortByPopularity();

    IMovieSearch sortByRating();

    IMovieSearch sortBy(String sortBy);

    IMovieSearch withLanguage(String language);

	/**
	 *
	 * @return list of movies. Empty list if none found
	 * @throws MoviesDataException in case of errors
	 */
    List<Movie> list() throws MoviesDataException;

	/**
	 *
	 * @param movieId movie id to get
	 * @return movie or null if not found
	 * @throws MoviesDataException in case of errors
	 */
    Movie getMovieById(int movieId) throws MoviesDataException;

}
