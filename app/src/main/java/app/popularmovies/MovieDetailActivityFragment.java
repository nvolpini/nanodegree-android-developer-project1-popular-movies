package app.popularmovies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import app.popularmovies.databinding.FragmentMovieDetailBinding;
import app.popularmovies.model.Movie;
import app.popularmovies.service.MoviesService;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {

    public MovieDetailActivityFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        FragmentMovieDetailBinding bindind = DataBindingUtil.inflate(inflater,R.layout.fragment_movie_detail, container, false);
		View rootView = bindind.getRoot();


		Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra(MovieDetailActivity.MOVIE_ID_EXTRA_KEY)) {

			//reataurar direto do parcelable abaixo nao funcionou
            Movie movie = MoviesService.get().restoreMovie(intent.getExtras());

			//TODO REVISOR - porque não consigo restaurar o parcelable direto aqui ???
			//Caused by: java.lang.NullPointerException: expected receiver of type app.popularmovies.model.Movie, but got null
			//Movie movie = intent.getParcelableExtra(MovieDetailActivity.MOVIE_EXTRA_KEY);

			if (movie != null) {
				bindind.setMovie(movie);
			}

        }

        return rootView;
    }

}
