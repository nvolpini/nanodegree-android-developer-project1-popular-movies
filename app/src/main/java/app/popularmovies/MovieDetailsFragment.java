package app.popularmovies;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.popularmovies.databinding.FragmentMovieDetailsBinding;
import app.popularmovies.model.Movie;

/**
 * Show the movie details.
 */
public class MovieDetailsFragment extends Fragment {

    public MovieDetailsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        FragmentMovieDetailsBinding binding = DataBindingUtil.inflate(inflater,R.layout.fragment_movie_details, container, false);
		View rootView = binding.getRoot();

		Bundle arguments = getArguments();
		if (arguments != null) {
			Movie movie = arguments.getParcelable(MovieDetailsActivity.MOVIE_EXTRA_KEY);

			if (movie != null) {
				binding.setMovie(movie);
			}
		}


        return rootView;
    }

}
