package app.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import app.popularmovies.MoviesFragment.OnListFragmentInteractionListener;
import app.popularmovies.model.Movie;
import app.popularmovies.service.MoviesService;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Movie} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MoviesListRecyclerViewAdapter extends RecyclerView.Adapter<MoviesListRecyclerViewAdapter.ViewHolder> {

    private static final Logger log = LoggerFactory.getLogger(MoviesListRecyclerViewAdapter.class);

    private final List<Movie> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MoviesListRecyclerViewAdapter(List<Movie> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movies_fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Movie movie = mValues.get(position);

        holder.mItem = mValues.get(position);


        //TODO buscar imagens maiores conforme a tela do dispositivo.

		//
		/**
		 *TODO REVISOR por favor uma dica para identificar o tamanho da tela e decidir buscar uma imagem maior (maneira recomendada)
		 * conforme última revisão, ajustei para que a imagem ocupe toda a largura da tela (layout/movies_fragment_item.xml) mas em telas maiores a imagem de tamanho 185 fica muito distorcida.
		 */

        if (movie.getPosterPath()==null) {

            Picasso.with(holder.mView.getContext())
                    .load(R.drawable.no_poster_185)
                    .into(holder.mImageView);

        } else {

            String imageUrl = MoviesService.get().getMoviePosterUrl(movie);

            Picasso.with(holder.mView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.loading_poster_185)
                    .error(R.drawable.no_poster_185)
                    .into(holder.mImageView);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void updateMovies(List<Movie> movies) {
        mValues.clear();
        mValues.addAll(movies);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        //public final TextView mIdView;
        //public final TextView mContentView;
        public final ImageView mImageView;
        public Movie mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            //mIdView = (TextView) view.findViewById(R.id.id);
            //mContentView = (TextView) view.findViewById(R.id.content);
            mImageView = (ImageView) view.findViewById(R.id.imageView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mItem.getTitle() + "'";
        }
    }
}
