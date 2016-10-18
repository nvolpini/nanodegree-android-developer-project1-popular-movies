package app.popularmovies.service;

import app.popularmovies.model.SearchParams;

/**
 * Created by neimar on 26/09/16.
 */
public abstract class AbstractSearchImpl implements IMovieSearch {

	protected final SearchParams params;

	public AbstractSearchImpl(SearchParams params) {
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
	public IMovieSearch sortByFavorites() {
		this.sortBy(SORT_BY_FAVORITES);
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

	protected void validate() {
		String sortBy = params.getSortBy();

		if (sortBy == null ||
				(!SORT_BY_POPULARITY.equals(sortBy) && !SORT_BY_RATING.equals(sortBy) )) {
			sortBy = SORT_BY_POPULARITY;
			params.setSortBy(sortBy);
		}

		if (params.getMoviesToDownload()==0) { //TODO VALIDAR RESTO DE 20 == 0
			params.setMoviesToDownload(20);
		}
	}
}
