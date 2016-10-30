package app.popularmovies.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import com.android.databinding.library.baseAdapters.BR;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Date;

import app.popularmovies.Utils;
import app.popularmovies.service.TheMoviesDBService;

/**
 * Created by neimar on 10/09/16.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
public class Movie extends BaseObservable implements Parcelable {

	/**
	 * internal id
	 */
	@JsonIgnore
	private long id;

    @JsonProperty("id")
    private int moviesDbId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("original_title")
    private String originalTitle;

    @JsonProperty("overview")
    private String overview;

    @JsonProperty("release_date")
    private String releaseDateString;


	@JsonIgnore
	private Date releaseDate;

    @JsonProperty("vote_average")
    private double voteAverage;

    @JsonProperty("poster_path")
    private String posterPath;

	@JsonProperty("video")
	private boolean video;

	//@JsonProperty("runtime")
	@JsonIgnore //somente na url de movie details
	private Integer runtime;

	@JsonIgnore
	private Long videosDownloaded;

	@JsonIgnore
	private Long reviewsDownloaded;

	@JsonIgnore
	private long movieDownloaded;

	@JsonIgnore
	private String movieDownloadLanguage;

	@JsonIgnore
	private FavoriteInformation favoriteInformation;

	public Movie() {
    }

    private Movie(Parcel in){
		id = in.readLong();
        moviesDbId = in.readInt();
        title = in.readString();
        originalTitle = in.readString();
        overview = in.readString();
        //releaseDateString = in.readString();
		releaseDate = new Date(in.readLong());
        voteAverage = in.readDouble();
        posterPath = in.readString();
		video = in.readInt() == 1;
		runtime = in.readInt();
		videosDownloaded = in.readLong();
		reviewsDownloaded = in.readLong();
		movieDownloaded = in.readLong();
		movieDownloadLanguage = in.readString();
		favoriteInformation = in.readParcelable(getClass().getClassLoader());
    }

    public Movie(long id, int moviesDbId, String title, String originalTitle, String overview, Date releaseDate, double voteAverage) {
        this.id = id;
		this.moviesDbId = moviesDbId;
        this.title = title;
        this.originalTitle = originalTitle;
        this.overview = overview;
        //this.releaseDateString = releaseDateString;
		this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
    }



    public String getYear() {
        //return releaseDateString.substring(0,4);
		return releaseDate != null ? String.format("%1$tY",releaseDate) : null;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

	@Deprecated
    public String getReleaseDateString() {
        return releaseDateString;
    }

	@Deprecated
    public void setReleaseDateString(String releaseDateString) {
        //this.releaseDateString = releaseDateString;
		this.releaseDate = Utils.toDate(releaseDateString);
    }

    public int getMoviesDbId() {
        return moviesDbId;
    }

    public void setMoviesDbId(int moviesDbId) {
        this.moviesDbId = moviesDbId;
    }


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	public boolean isVideo() {
		return video;
	}

	public void setVideo(boolean video) {
		this.video = video;
	}

	public Integer getRuntime() {
		return runtime;
	}

	public void setRuntime(Integer runtime) {
		this.runtime = runtime;
	}

	public Long getVideosDownloaded() {
		return videosDownloaded;
	}

	public void setVideosDownloaded(Long videosDownloaded) {
		this.videosDownloaded = videosDownloaded;
	}

	public Long getReviewsDownloaded() {
		return reviewsDownloaded;
	}

	public void setReviewsDownloaded(Long reviewsDownloaded) {
		this.reviewsDownloaded = reviewsDownloaded;
	}

	public long getMovieDownloaded() {
		return movieDownloaded;
	}

	public void setMovieDownloaded(long movieDownloaded) {
		this.movieDownloaded = movieDownloaded;
	}

	public String getMovieDownloadLanguage() {
		return movieDownloadLanguage;
	}

	public void setMovieDownloadLanguage(String movieDownloadLanguage) {
		this.movieDownloadLanguage = movieDownloadLanguage;
	}

	@Bindable
	public FavoriteInformation getFavoriteInformation() {
		return favoriteInformation;
	}

	public void setFavoriteInformation(FavoriteInformation favoriteInformation) {
		this.favoriteInformation = favoriteInformation;
		notifyPropertyChanged(BR.favoriteInformation);
		notifyPropertyChanged(BR.favorite);
	}

	@Bindable
	public boolean isFavorite() {
		return favoriteInformation != null;
	}


	public String toString() {
		final StringBuilder sb = new StringBuilder("Movie{");
		sb.append("id=").append(id);
		sb.append(", moviesDbId=").append(moviesDbId);
		sb.append(", title='").append(title).append('\'');
		sb.append(", originalTitle='").append(originalTitle).append('\'');
		//sb.append(", overview='").append(overview).append('\'');
		sb.append(", releaseDateString='").append(Utils.toIsoDate(releaseDate)).append('\'');
		//sb.append(", releaseDate=").append(releaseDate);
		sb.append(", voteAverage=").append(voteAverage);
		//sb.append(", posterPath='").append(posterPath).append('\'');
		sb.append(", favorite=").append(isFavorite());
		sb.append('}');
		return sb.toString();
	}


	@Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
        dest.writeInt(moviesDbId);
        dest.writeString(title);
        dest.writeString(originalTitle);
        dest.writeString(overview);
        //dest.writeString(releaseDateString);
		dest.writeLong(releaseDate.getTime());
        dest.writeDouble(voteAverage);
        dest.writeString(posterPath);
		dest.writeInt(video ? 1 : 0);
		dest.writeInt(runtime);
		dest.writeLong(videosDownloaded);
		dest.writeLong(reviewsDownloaded);
		dest.writeLong(movieDownloaded);
		dest.writeString(movieDownloadLanguage);

		dest.writeParcelable(favoriteInformation,0);

    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }

    };
}
