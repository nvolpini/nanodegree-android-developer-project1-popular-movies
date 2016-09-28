package app.popularmovies.data;

import java.util.List;

import app.popularmovies.model.Movie;

/**
 * Repository to manage movies in the local database
 *
 * Created by neimar on 28/09/16.
 */

public interface IMoviesRepository {

    /**
     * creates a new movie.
     *
     * - sets the generated id in the instance, so it can be reused.
     *
     * @param movie
     */
    void create(Movie movie);

    /**
     * Update movie
     * @param movie
     */
    void update(Movie movie);

    /**
     * Delete movie
     * @param movie
     */
    void delete(Movie movie);


    /**
     * Check if a movie already exists in the local database using the moviedb id.
     *
     * @param movieDbId
     * @return
     */
    boolean exists(int movieDbId);

    /**
     * Load movie by id
     * @param id
     * @return
     */
    Movie getById(int id);

    /**
     * Load movie by movieDbId
     * @param movieDbId
     * @return
     */
    Movie getByMovieDbId(int movieDbId);

    List<Movie> getPopularMovies();

    List<Movie> getTopRatedMovies();

    List<Movie> getFavoriteMovies();


    /**
     * Mark a movie as favorite
     * @param movie
     */
    void markFavorite(Movie movie);

    /**
     * remove from favorites
     * @param movie
     */
    void removeFavorite(Movie movie);

    /**
     * Save the list of popular movies
     * @param movies
     */
    void savePopular(List<Movie> movies);

    /**
     * Save the list of top rated movies
     * @param movies
     */
    void saveTopRated(List<Movie> movies);



}
