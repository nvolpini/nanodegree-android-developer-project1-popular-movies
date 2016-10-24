package app.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.popularmovies.MovieDetailsFragment.OnMovieDetailsInteractionListener;
import app.popularmovies.data.MoviesDbHelper;
import app.popularmovies.model.Video;
import app.popularmovies.util.CursorRecyclerViewAdapter;

/**

 */
public class VideosListCursorAdapter extends CursorRecyclerViewAdapter<VideosListCursorAdapter.ViewHolder> {

    private static final Logger log = LoggerFactory.getLogger(VideosListCursorAdapter.class);

    private final MovieDetailsFragment.OnMovieDetailsInteractionListener mListener;


	public VideosListCursorAdapter(Context context, Cursor cursor, OnMovieDetailsInteractionListener listener){
		super(context,cursor);
		this.mListener = listener;

	}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
	public void onBindViewHolder(final ViewHolder holder, Cursor cursor) {

		Video video = MoviesDbHelper.cursorToVideo(cursor);

        holder.mItem = video;
		holder.mIdView.setText(Long.toString(video.getId()));
		holder.mContentView.setText(String.format("%s - %s - %s",video.getName(), video.getType(), video.getKey()));

		//TODO mover isso para onCreateViewHolder ???
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onVideoInteraction(holder.mItem);
                }
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Video mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.videoId);
            mContentView = (TextView) view.findViewById(R.id.videoName);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
