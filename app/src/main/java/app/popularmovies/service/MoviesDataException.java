package app.popularmovies.service;

/**
 * Created by neimar on 26/09/16.
 */

public class MoviesDataException extends RuntimeException {
	public MoviesDataException() {
	}

	public MoviesDataException(String detailMessage) {
		super(detailMessage);
	}

	public MoviesDataException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public MoviesDataException(Throwable throwable) {
		super(throwable);
	}
}
