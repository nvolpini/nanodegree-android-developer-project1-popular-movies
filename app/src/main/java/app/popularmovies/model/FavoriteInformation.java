package app.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by neimar on 07/10/16.
 */
public class FavoriteInformation implements Parcelable {


	private long id;

	private int position;

	private Date dateAdded;

	private int votes;

	public FavoriteInformation() {

	}

	private FavoriteInformation(Parcel in) {

		this.id = in.readLong();
		this.position = in.readInt();
		this.dateAdded = new Date(in.readLong());
		this.votes = in.readInt();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public Date getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}

	public int getVotes() {
		return votes;
	}

	public void setVotes(int votes) {
		this.votes = votes;
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeLong(id);
		dest.writeInt(position);
		dest.writeLong(dateAdded.getTime());
		dest.writeInt(votes);

	}

	public static final Parcelable.Creator<FavoriteInformation> CREATOR = new Parcelable.Creator<FavoriteInformation>() {
		@Override
		public FavoriteInformation createFromParcel(Parcel parcel) {
			return new FavoriteInformation(parcel);
		}

		@Override
		public FavoriteInformation[] newArray(int i) {
			return new FavoriteInformation[i];
		}

	};

}

