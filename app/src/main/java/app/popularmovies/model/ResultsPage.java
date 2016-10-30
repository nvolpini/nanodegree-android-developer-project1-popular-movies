package app.popularmovies.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Iterator;
import java.util.List;

/**
 * Created by neimar on 18/10/16.
 */

public class ResultsPage<T> extends AbstractJsonMapping implements Iterable<T> {

	@JsonProperty("results")
	private List<T> results;

	@JsonProperty("page")
	private int page;

	@JsonProperty("total_pages")
	private int totalPages;

	@JsonProperty("total_results")
	private int totalResults;

	@Override
	public Iterator<T> iterator() {
		return results.iterator();
	}


	public List<T> getResults() {
		return results;
	}


	public int getPage() {
		return page;
	}


	public int getTotalPages() {
		return totalPages;
	}


	public int getTotalResults() {
		return totalResults;
	}


	public void setPage(int page) {
		this.page = page;
	}


	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}


	public void setTotalResults(int totalResults) {
		this.totalResults = totalResults;
	}

}
