package app.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MovieDetailsActivity extends AppCompatActivity {

	private static final Logger log = LoggerFactory.getLogger(MovieDetailsFragment.class);

    public static final String MOVIE_EXTRA_KEY = "app.popularmovies.movie";
	public static final String PARAMS_EXTRA_KEY = "app.popularmovies.params";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        if (savedInstanceState == null) {

			log.trace("setting MovieDetailsFragment args... ");

			Bundle args = new Bundle();
			args.putParcelable(MovieDetailsActivity.MOVIE_EXTRA_KEY
					, getIntent().getParcelableExtra(MOVIE_EXTRA_KEY));

			args.putParcelable(MovieDetailsActivity.PARAMS_EXTRA_KEY
					, getIntent().getParcelableExtra(PARAMS_EXTRA_KEY));


			MovieDetailsFragment f = new MovieDetailsFragment();
			f.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, f)
                    .commit();
        }
    }

}
