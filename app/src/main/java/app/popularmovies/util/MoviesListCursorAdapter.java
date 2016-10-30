package app.popularmovies.util;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.popularmovies.MoviesListFragment.OnListFragmentInteractionListener;
import app.popularmovies.R;
import app.popularmovies.data.MoviesDbHelper;
import app.popularmovies.databinding.MoviesListFragmentItemBinding;
import app.popularmovies.model.Movie;
import app.popularmovies.service.TheMoviesDBService;

import static app.popularmovies.R.id.imageView;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Movie} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MoviesListCursorAdapter extends CursorRecyclerViewAdapter<MoviesListCursorAdapter.ViewHolder> {

	private static final Logger log = LoggerFactory.getLogger(MoviesListCursorAdapter.class);


	private final OnListFragmentInteractionListener mListener;

	public MoviesListCursorAdapter(Context context, Cursor cursor, OnListFragmentInteractionListener listener) {
		super(context, cursor);
		this.mListener = listener;

	}


	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		/*View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.movies_list_fragment_item, parent, false);*/


		MoviesListFragmentItemBinding bind = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
				, R.layout.movies_list_fragment_item, parent, false);

		return new ViewHolder(bind);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, Cursor cursor) {

		Movie movie = MoviesDbHelper.cursorToMovie(cursor);

		holder.mItem = movie;

		holder.mBind.setMovie(movie);

		TheMoviesDBService.renderImage(holder.mView.getContext(), holder.mImageView, movie);


		holder.mView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null != mListener) {
					mListener.onListFragmentInteraction(holder.mItem);
				}
			}
		});
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		public final View mView;
		public final ImageView mImageView;
		public Movie mItem;
		public MoviesListFragmentItemBinding mBind;

		//public ViewHolder(View view) {
		public ViewHolder(MoviesListFragmentItemBinding bind) {
			super(bind.getRoot());
			mBind = bind;
			mView = bind.getRoot();

			mView.setClickable(true);

			mImageView = (ImageView) mView.findViewById(imageView);
		}

		@Override
		public String toString() {
			return super.toString() + " '" + mItem.getTitle() + "'";
		}
	}
}
