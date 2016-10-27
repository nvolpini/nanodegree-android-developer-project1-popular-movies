package app.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.popularmovies.model.Movie;
import app.popularmovies.model.Review;
import app.popularmovies.model.Video;

public class MovieDetailsActivity extends AppCompatActivity implements
		MovieDetailsFragment.OnMovieDetailsInteractionListener {

	private static final Logger log = LoggerFactory.getLogger(MovieDetailsActivity.class);

	private static final String MOVIE_EXTRA_KEY = MovieDetailsActivity.class.getName().concat(".movieExtra");

	public static Intent newIntent(Context context, Movie movie) {
		Intent intent = new Intent(context, MovieDetailsActivity.class);
		intent.putExtra(MOVIE_EXTRA_KEY, movie);
		return intent;
	}

	private Movie getMovieExtra() {
		return getIntent().getParcelableExtra(MOVIE_EXTRA_KEY);
	}


	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        if (savedInstanceState == null) {

			log.trace("setting MovieDetailsFragment args... ");

			MovieDetailsFragment f = MovieDetailsFragment.newInstance(getMovieExtra().getId());

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, f)
                    .commit();
        }
    }


	@Override
	public void onVideoInteraction(Video video) {
		log.trace("iteracao, video: {}",video.getName());
		Intent i = Utils.newVideoIntent(this,video);

		if (i != null) {
			startActivity(i);

		} else {

			Toast.makeText(this,getString(R.string.cannot_handle_video,video.getSite())
					,Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onReviewInteraction(Review review) {
		log.trace("iteracao, review: {}",review.getId());

		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(review.getUrl()));
		startActivity(i);

	}


	@Override
	public void onShareVideoInteraction(Video video) {
		log.trace("iteracao, share video: {}", video.getId());

		Intent i = Utils.newShareVideoIntent(this, video);

		if (i != null) {
			startActivity(i);

		}

	}
}
