package app.popularmovies.util;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.popularmovies.MovieDetailsFragment.OnMovieDetailsInteractionListener;
import app.popularmovies.R;
import app.popularmovies.data.MoviesDbHelper;
import app.popularmovies.model.Video;

/**

 */
public class VideosListCursorAdapter extends CursorRecyclerViewAdapter<VideosListCursorAdapter.ViewHolder> {

	private static final Logger log = LoggerFactory.getLogger(VideosListCursorAdapter.class);

	private final OnMovieDetailsInteractionListener mListener;


	public VideosListCursorAdapter(Context context, Cursor cursor, OnMovieDetailsInteractionListener listener) {
		super(context, cursor);
		this.mListener = listener;

	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.fragment_video_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, Cursor cursor) {

		Video video = MoviesDbHelper.cursorToVideo(cursor);

		holder.mItem = video;
		holder.mContentView.setText(String.format("%s - %s", video.getName(), video.getType()));

		holder.mView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null != mListener) {
					mListener.onVideoInteraction(holder.mItem);
				}
			}
		});

		holder.imageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null != mListener) {
					mListener.onShareVideoInteraction(holder.mItem);
				}
			}
		});
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		public final View mView;
		public final TextView mContentView;
		public final ImageButton imageButton;
		public Video mItem;

		public ViewHolder(View view) {
			super(view);
			mView = view;
			mContentView = (TextView) view.findViewById(R.id.videoName);
			imageButton = (ImageButton) view.findViewById(R.id.shareButton);
		}

		@Override
		public String toString() {
			return super.toString() + " '" + mContentView.getText() + "'";
		}
	}
}
