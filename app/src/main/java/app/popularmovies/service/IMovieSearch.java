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

    List<Movie> list();

    Movie getMovieById(int movieId);

}
