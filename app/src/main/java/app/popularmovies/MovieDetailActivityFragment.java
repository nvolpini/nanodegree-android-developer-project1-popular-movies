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

            //intent.getParcelableExtra()

            updateView(rootView, movie);

        }

        return rootView;
    }

    private void updateView(View rootView, Movie movie)  {

        if (movie == null) {
            return;
        }


        //TODO formatacao datas/numeros

        ((TextView) rootView.findViewById(R.id.originalTitle)).setText(movie.getOriginalTitle());
        ((TextView) rootView.findViewById(R.id.synospsis)).setText(movie.getOverview());
        ((TextView) rootView.findViewById(R.id.releaseDate)).setText(movie.getYear());
        ((TextView) rootView.findViewById(R.id.userRating)).setText(String.format("%1$.1f/10", movie.getVoteAverage())); //TODO

        String imageUrl = MoviesService.get().getMoviePosterUrl(movie);

        Picasso.with(getContext())
                .load(imageUrl)
                .placeholder(R.mipmap.ic_launcher)  //TODO
                .error(R.mipmap.ic_launcher) //TODO
                .into((ImageView) rootView.findViewById(R.id.posterImage));
    }

}
