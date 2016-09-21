package app.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by neimar on 10/09/16.
 */
public class Movie implements Parcelable {

    private int id;

    private String title;

    private String originalTitle;

    private String overview;

    private String releaseDate;

    private double voteAverage;

    private String posterPath;


    public Movie() {
    }

    private Movie(Parcel in){
        id = in.readInt();
        title = in.readString();
        originalTitle = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        voteAverage = in.readDouble();
        posterPath = in.readString();
    }

    public Movie(int id, String title, String originalTitle, String overview, String releaseDate, double voteAverage) {
        this.id = id;
        this.title = title;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
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

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Movie{");
        sb.append("id=").append(id);
        sb.append(", originalTitle='").append(originalTitle).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(originalTitle);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeDouble(voteAverage);
        dest.writeString(posterPath);

    }

    public final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
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
