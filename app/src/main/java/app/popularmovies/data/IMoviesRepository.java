package app.popularmovies.data;

import android.database.Cursor;

import java.util.List;

import app.popularmovies.model.Movie;
import app.popularmovies.service.MoviesDataException;

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
    void create(Movie movie) throws MoviesDataException;

    /**
     * Update movie
     * @param movie
     */
    void update(Movie movie) throws MoviesDataException;

    /**
     * Delete movie
     * @param movie
     */
    void delete(Movie movie) throws MoviesDataException;


    /**
     * Check if a movie already exists in the local database using the moviedb id.
     *
     * @param movieDbId
     * @return
     */
    boolean exists(int movieDbId) throws MoviesDataException;

    Movie cursorToMovie(Cursor cursor);

    /**
     * Load movie by id
     * @param id
     * @return
     */
    Movie getById(int id) throws MoviesDataException;

    /**
     * Load movie by movieDbId
     * @param movieDbId
     * @return
     */
    Movie getByMovieDbId(int movieDbId) throws MoviesDataException;

    List<Movie> getPopularMovies() throws MoviesDataException;

    List<Movie> getTopRatedMovies() throws MoviesDataException;

    List<Movie> getFavoriteMovies() throws MoviesDataException;


    /**
     * Mark a movie as favorite
     * @param movie
     */
    void markFavorite(Movie movie) throws MoviesDataException;

    /**
     * remove from favorites
     * @param movie
     */
    void removeFavorite(Movie movie) throws MoviesDataException;

    /**
     * Save the list of popular movies
     * @param movies
     */
    void savePopular(List<Movie> movies) throws MoviesDataException;

    /**
     * Save the list of top rated movies
     * @param movies
     */
    void saveTopRated(List<Movie> movies) throws MoviesDataException;



}
