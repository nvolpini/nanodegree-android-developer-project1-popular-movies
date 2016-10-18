package app.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import app.popularmovies.service.IMovieSearch;

/**
 * Created by neimar on 19/09/16.
 */
public class SearchParams implements Parcelable {

	private String language;

	private String sortBy;

	private int moviesToDownload;

	public SearchParams() {


	}

	private SearchParams(Parcel in) {
		language = in.readString();
		sortBy = in.readString();
		moviesToDownload = in.readInt();
	}

	public boolean isSortByPopularity() {
		return IMovieSearch.SORT_BY_POPULARITY.equals(sortBy);
	}


	public boolean isSortByRating() {
		return IMovieSearch.SORT_BY_RATING.equals(sortBy);
	}

	public boolean isSortByFavorites() {
		return IMovieSearch.SORT_BY_FAVORITES.equals(sortBy);
	}

	public String getSortBy() {
		return sortBy;
	}

	public SearchParams setSortBy(String sortBy) {
		this.sortBy = sortBy;
		return this;
	}

	public String getLanguage() {
		return language;
	}

	public SearchParams setLanguage(String language) {
		this.language = language;
		return this;
	}

	public int getMoviesToDownload() {
		return moviesToDownload;
	}

	public void setMoviesToDownload(int moviesToDownload) {
		this.moviesToDownload = moviesToDownload;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("SearchParams{");
		sb.append("language='").append(language).append('\'');
		sb.append(", sortBy='").append(sortBy).append('\'');
		sb.append(", moviesToDownload='").append(moviesToDownload).append('\'');
		sb.append('}');
		return sb.toString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(language);
		dest.writeString(sortBy);
		dest.writeInt(moviesToDownload);
	}


	public static final Parcelable.Creator<SearchParams> CREATOR = new Parcelable.Creator<SearchParams>() {
		@Override
		public SearchParams createFromParcel(Parcel parcel) {
			return new SearchParams(parcel);
		}

		@Override
		public SearchParams[] newArray(int i) {
			return new SearchParams[i];
		}

	};
}
