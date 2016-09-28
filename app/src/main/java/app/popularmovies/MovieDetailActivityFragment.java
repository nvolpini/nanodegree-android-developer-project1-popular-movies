package app.popularmovies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.popularmovies.databinding.FragmentMovieDetailBinding;
import app.popularmovies.model.Movie;

/**
 * Show the movie details.
 */
public class MovieDetailActivityFragment extends Fragment {

    public MovieDetailActivityFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        FragmentMovieDetailBinding binding = DataBindingUtil.inflate(inflater,R.layout.fragment_movie_detail, container, false);
		View rootView = binding.getRoot();


		Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra(MovieDetailActivity.MOVIE_EXTRA_KEY)) {

			Movie movie = intent.getParcelableExtra(MovieDetailActivity.MOVIE_EXTRA_KEY);

			if (movie != null) {
				binding.setMovie(movie);
			}

        }

        return rootView;
    }

}
