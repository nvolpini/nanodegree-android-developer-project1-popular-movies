package app.popularmovies.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Created by neimar on 18/10/16.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
public class Review extends AbstractJsonMapping {
	/**
	 * internal id
	 */
	@JsonIgnore
	private long id;

	@JsonIgnore
	private long movieId;

	@JsonProperty("id")
	private String moviesDbId;

	@JsonProperty
	private String author;

	@JsonProperty
	private String content;

	@JsonProperty
	private String url;


	public Review() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getMovieId() {
		return movieId;
	}

	public void setMovieId(long movieId) {
		this.movieId = movieId;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getMoviesDbId() {
		return moviesDbId;
	}

	public void setMoviesDbId(String moviesDbId) {
		this.moviesDbId = moviesDbId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Review{");
		sb.append("id=").append(id);
		sb.append(", moviesDbId='").append(moviesDbId).append('\'');
		sb.append(", author='").append(author).append('\'');
		sb.append(", url='").append(url).append('\'');
		sb.append('}');
		return sb.toString();
	}

}
