package app.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra(MovieDetailActivity.MOVIE_ID_EXTRA_KEY)) {

            Movie movie = MoviesService.get().restoreMovie(intent.getExtras());

            //TODO didnt work
            //Movie movie = intent.getParcelableExtra(MovieDetailActivity.MOVIE_EXTRA_KEY);


            updateView(rootView, movie);

        }

        return rootView;
    }

    private void updateView(View rootView, Movie movie)  {

        if (movie == null) {
            return;
        }


        ((TextView) rootView.findViewById(R.id.title)).setText(movie.getTitle());
        ((TextView) rootView.findViewById(R.id.originalTitle)).setText(movie.getOriginalTitle());
        ((TextView) rootView.findViewById(R.id.synospsis)).setText(movie.getOverview());
        ((TextView) rootView.findViewById(R.id.releaseDate)).setText(movie.getYear());
        ((TextView) rootView.findViewById(R.id.userRating)).setText(String.format("%1$.1f/10", movie.getVoteAverage()));

        String imageUrl = MoviesService.get().getMoviePosterUrl(movie);

        Picasso.with(getContext())
                .load(imageUrl)
                .placeholder(R.drawable.loading_poster_185)
                .error(R.drawable.no_poster_185)
                .into((ImageView) rootView.findViewById(R.id.posterImage));
    }

}
