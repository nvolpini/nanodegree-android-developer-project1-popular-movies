package app.popularmovies.util;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.popularmovies.MoviesListFragment.OnListFragmentInteractionListener;
import app.popularmovies.R;
import app.popularmovies.data.MoviesDbHelper;
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
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.movies_list_fragment_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, Cursor cursor) {

		Movie movie = MoviesDbHelper.cursorToMovie(cursor);

		holder.mItem = movie;


		//TODO buscar imagens maiores conforme a tela do dispositivo.

		//
		/**
		 *TODO REVISOR por favor uma dica para identificar o tamanho da tela e decidir buscar uma imagem maior (maneira recomendada)
		 * conforme última revisão, ajustei para que a imagem ocupe toda a largura da tela (layout/movies_fragment_item.xml) mas em telas maiores a imagem de tamanho 185 fica muito distorcida.
		 */

		if (movie.getPosterPath() == null) {

			Picasso.with(holder.mView.getContext())
					.load(R.drawable.no_poster_185)
					.into(holder.mImageView);

		} else {

			final String imageUrl = TheMoviesDBService.getMoviePosterUrl(mContext, movie);

			Picasso.with(holder.mView.getContext())
					.load(imageUrl)
					.networkPolicy(NetworkPolicy.OFFLINE)
					.placeholder(R.drawable.loading_poster_185)
					.error(R.drawable.no_poster_185)
					.into(holder.mImageView, new Callback() {
						@Override
						public void onSuccess() {

						}

						@Override
						public void onError() {
							//Try again online if cache failed
							Picasso.with(holder.mView.getContext())
									.load(imageUrl)
									.placeholder(R.drawable.loading_poster_185)
									.error(R.drawable.no_poster_185)
									.into(holder.mImageView, new Callback() {
										@Override
										public void onSuccess() {

										}

										@Override
										public void onError() {
											Log.v("Picasso", "Could not fetch image");
										}
									});
						}
					});
		}

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

		public ViewHolder(View view) {
			super(view);
			mView = view;

			mView.setClickable(true);

			mImageView = (ImageView) view.findViewById(imageView);
		}

		@Override
		public String toString() {
			return super.toString() + " '" + mItem.getTitle() + "'";
		}
	}
}
