package app.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MovieDetailsActivity extends AppCompatActivity {

    public static final String MOVIE_EXTRA_KEY = "app.popularmovies.movie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        if (savedInstanceState == null) {

			Bundle args = new Bundle();
			args.putParcelable(MovieDetailsActivity.MOVIE_EXTRA_KEY
					, getIntent().getParcelableExtra(MOVIE_EXTRA_KEY));

            MovieDetailsFragment f = new MovieDetailsFragment();
			f.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, f)
                    .commit();
        }
    }

}
