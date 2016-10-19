package app.popularmovies.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Created by neimar on 18/10/16.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
public class Review extends AbstractJsonMapping {

	@JsonProperty
	private String id;

	@JsonProperty
	private String author;

	@JsonProperty
	private String content;

	@JsonProperty
	private String url;


	public Review() {
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
		sb.append("id='").append(id).append('\'');
		sb.append(", author='").append(author).append('\'');
		sb.append('}');
		return sb.toString();
	}

}
