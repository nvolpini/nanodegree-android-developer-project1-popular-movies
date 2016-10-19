package app.popularmovies.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.List;

/**
 * Created by neimar on 18/10/16.
 */

@JsonRootName("video")

public class Video extends AbstractJsonMapping {


	@JsonProperty("id")
	private String id;

	@JsonProperty("name")
	private String name;

	@JsonProperty("iso_639_1")
	private String language;

	@JsonProperty("iso_3166_1")
	private String region;

	@JsonProperty("site")
	private String site;

	@JsonProperty("key")
	private String key;

	@JsonProperty("size")
	private Integer size;

	@JsonProperty("type")
	private String type;

	public Video() {
	}

	public static class Results {

		@JsonProperty("id")
		private int id;

		@JsonProperty("results")
		private List<Video> videos;

		public int getId() {
			return id;
		}


		public void setId(int id) {


			this.id = id;
		}

		public List<Video> getVideos() {
			return videos;
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
