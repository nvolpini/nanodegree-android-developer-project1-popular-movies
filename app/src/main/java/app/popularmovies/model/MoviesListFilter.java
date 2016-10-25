package app.popularmovies.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import app.popularmovies.data.MovieContract;

/**
 * Created by neimar on 24/10/16.
 */
public class MoviesListFilter implements Parcelable {

	public static final MoviesListFilter POPULAR = new MoviesListFilter(MoviesCollection.POPULAR);
	public static final MoviesListFilter TOP_RATED = new MoviesListFilter(MoviesCollection.TOP_RATED);
	public static final MoviesListFilter FAVORITES = new MoviesListFilter(MoviesCollection.FAVORITES);

	private enum MoviesCollection {
		POPULAR, TOP_RATED, FAVORITES, SEARCH;
	}

	private MoviesCollection collection;

	private MoviesListFilter(MoviesCollection collection) {
		this.collection = collection;
	}

	private MoviesListFilter(Parcel in) {

		this.collection = MoviesCollection.valueOf(in.readString());

	}


	public Uri getUri() {

		switch (collection) {

			case POPULAR:
				return MovieContract.PopularMoviesEntry.CONTENT_URI;

			case TOP_RATED:
				return MovieContract.TopRatedMoviesEntry.CONTENT_URI;

			default:
				return MovieContract.FavoriteMoviesEntry.CONTENT_URI;
		}

	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("MoviesListFilter{");
		sb.append("collection=").append(collection);
		sb.append('}');
		return sb.toString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(collection.name());
	}


	public static final Parcelable.Creator<MoviesListFilter> CREATOR = new Parcelable.Creator<MoviesListFilter>() {
		@Override
		public MoviesListFilter createFromParcel(Parcel parcel) {
			return new MoviesListFilter(parcel);
		}

		@Override
		public MoviesListFilter[] newArray(int i) {
			return new MoviesListFilter[i];
		}

	};
}
