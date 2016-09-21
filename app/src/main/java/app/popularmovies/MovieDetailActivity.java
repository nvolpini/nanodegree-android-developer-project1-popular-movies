package app.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String MOVIE_ID_EXTRA_KEY = "app.popularmovies.movieId";
    public static final String MOVIE_EXTRA_KEY = "app.popularmovies.movie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        if (savedInstanceState == null) {


            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MovieDetailActivityFragment())
                    .commit();
        }
    }

}
