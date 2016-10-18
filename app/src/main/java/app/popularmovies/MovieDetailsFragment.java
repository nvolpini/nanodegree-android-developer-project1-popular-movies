package app.popularmovies;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import app.popularmovies.data.MovieContract;
import app.popularmovies.databinding.FragmentMovieDetailsBinding;
import app.popularmovies.model.FavoriteInformation;
import app.popularmovies.model.Movie;

/**
 * Show the movie details.
 */
public class MovieDetailsFragment extends Fragment implements View.OnClickListener {

	private static final Logger log = LoggerFactory.getLogger(MovieDetailsFragment.class);

    public MovieDetailsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        FragmentMovieDetailsBinding binding = DataBindingUtil.inflate(inflater,R.layout.fragment_movie_details, container, false);
		View rootView = binding.getRoot();

		Movie movie = getMovie();

		if (movie != null) {
			binding.setMovie(movie);
		}

		rootView.findViewById(R.id.action_set_favorite).setOnClickListener(this);

        return rootView;
    }

	private Movie getMovie() {
		Bundle arguments = getArguments();

		if (getActivity().getIntent() != null && getActivity().getIntent()
				.hasExtra(MovieDetailsActivity.MOVIE_EXTRA_KEY)) {

			return getActivity().getIntent().getParcelableExtra(MovieDetailsActivity.MOVIE_EXTRA_KEY);

		} else if (arguments != null) {

			return arguments.getParcelable(MovieDetailsActivity.MOVIE_EXTRA_KEY);

		}

		return null;
	}

	public FavoriteInformation setFavorite(Movie movie, boolean activated) {

		log.trace("set favorite, movieId: {}, activated: {}",movie.getId(), activated);

		FavoriteInformation f = new FavoriteInformation();

		// First, check if the movie already exists
		Cursor cursor = getActivity().getContentResolver().query(
				MovieContract.FavoriteMoviesEntry.CONTENT_URI,
				new String[]{
						MovieContract.FavoriteMoviesEntry.TABLE_NAME+ "."+
						MovieContract.FavoriteMoviesEntry._ID},
						MovieContract.FavoriteMoviesEntry.TABLE_NAME+ "."+
						MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID + " = ?",
				new String[]{Long.toString(movie.getId())},
				null);

		if (cursor.moveToFirst()) {
			int movieIdIndex = cursor.getColumnIndex(MovieContract.FavoriteMoviesEntry._ID);
			f.setId(cursor.getLong(movieIdIndex));
			log.trace("movie is already a favorite, movieId: {}, favId: {}",movie.getId(), f.getId());

			if (!activated) {
				log.debug("removing favorite: {}", f.getId());

				getActivity().getContentResolver().delete(MovieContract.FavoriteMoviesEntry.CONTENT_URI
						,MovieContract.FavoriteMoviesEntry.TABLE_NAME+ "."+
								MovieContract.FavoriteMoviesEntry._ID+"=?",new String[]{Long.toString(f.getId())});

			}

		} else {

			f.setDateAdded(new Date());
			f.setVotes(0);
			f.setPosition(1);
			movie.setFavoriteInformation(f);

			ContentValues movieValues = new ContentValues();
			movieValues.put(MovieContract.FavoriteMoviesEntry.COLUMN_DATE_ADD,f.getDateAdded().getTime());
			movieValues.put(MovieContract.FavoriteMoviesEntry.COLUMN_VOTES,f.getVotes());
			movieValues.put(MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID, movie.getId());
			movieValues.put(MovieContract.FavoriteMoviesEntry.COLUMN_POSITION,f.getPosition());

			Uri insertedUri = getActivity().getContentResolver().insert(
					MovieContract.FavoriteMoviesEntry.CONTENT_URI,
					movieValues
			);

			// The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
			f.setId(ContentUris.parseId(insertedUri));
			log.trace("favorite movie added: {}",movie);
		}

		cursor.close();

		return f;
	}

	@Override
	public void onClick(View v) {
		if (v.getId()==R.id.action_set_favorite) {
			ToggleButton b = (ToggleButton) v.findViewById(R.id.action_set_favorite);

			setFavorite(getMovie(), b.isChecked());
		}
	}
}
