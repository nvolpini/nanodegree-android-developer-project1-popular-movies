package app.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.popularmovies.model.Movie;
import app.popularmovies.model.SearchParams;
import app.popularmovies.service.MoviesService;

import static app.popularmovies.Utils.changeParamsFromPrefs;

public class MainActivity extends AppCompatActivity implements MoviesFragment.OnListFragmentInteractionListener {

    private static final Logger log = LoggerFactory.getLogger(MainActivity.class);

    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private boolean mTwoPane;
    private String mLocation;

    private SearchParams searchParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        log.trace("onCreate");

        //reset all prefs
        //PreferenceManager.getDefaultSharedPreferences(this).edit().clear().apply();

		//initialize prefs
        PreferenceManager.setDefaultValues(this,R.xml.pref_general,false);
        PreferenceManager.setDefaultValues(this,R.xml.pref_data_sync,false);


        if (savedInstanceState != null && savedInstanceState.containsKey(MoviesFragment.SEARCH_PARAMS_PARCELABLE_KEY)) {

            searchParams = savedInstanceState.getParcelable(MoviesFragment.SEARCH_PARAMS_PARCELABLE_KEY);

            log.trace("loading params from state: {}",searchParams);

        } else if (savedInstanceState == null) {

            searchParams = MoviesService.get().newSearchParams();

            searchParams.setLanguage(Utils.getPreferredLanguage(this));
            searchParams.setSortBy(Utils.getDefaultSorting(this));
			searchParams.setMoviesToDownload(Utils.getMoviesToDownload(this));

            log.trace("params from prefs: {} ",searchParams);

			/*
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container
							//TODO talvez usar 3 colunas de dispositivos maiores
                            ,MoviesFragmentOLD.newInstance(2, searchParams)
                    )
                    .commit();*/
        }


		if (findViewById(R.id.movie_detail_container) != null) {
			// The detail container view will be present only in the large-screen layouts
			// (res/layout-sw600dp). If this view is present, then the activity should be
			// in two-pane mode.
			mTwoPane = true;
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			if (savedInstanceState == null) {
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.movie_detail_container, new MovieDetailsFragment(), DETAILFRAGMENT_TAG)
						.commit();
			}
		} else {
			mTwoPane = false;
			getSupportActionBar().setElevation(0f);
		}

		MoviesFragment moviesFragment =  ((MoviesFragment)getSupportFragmentManager()
				.findFragmentById(R.id.fragment_movies));

		moviesFragment.setSearchParams(searchParams);

		//Bundle args = new Bundle();
		//args.putInt(MoviesFragment.ARG_COLUMN_COUNT, columnCount);
		//args.putParcelable(MoviesFragment.SEARCH_PARAMS_PARCELABLE_KEY, searchParams);
		//moviesFragment.setArguments(args);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {

        log.trace("saving state...");

        outState.putParcelable(MoviesFragment.SEARCH_PARAMS_PARCELABLE_KEY, searchParams);

        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        log.trace("restoring state...");

    }

    @Override
    protected void onResume() {
        super.onResume();
        log.trace("onResume()");

		//check if the user changed the preferred language in the preferences
		//next time he hits refresh it will use the new language
		//same for other prefs
		changeParamsFromPrefs(this,searchParams);



		/**
		 *TODO testar se mudou:
		 * - sortBy
		 * - idioma
		 */

		MoviesFragment mf =  ((MoviesFragment)getSupportFragmentManager()
				.findFragmentById(R.id.fragment_movies));

		//TODO mf.onChange

		MovieDetailsFragment df = (MovieDetailsFragment)getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);

		//TODO mf.onChange
    }


    @Override
    protected void onPause() {
        super.onPause();
        log.trace("onPause()");


    }


    @Override
    protected void onStart() {
        super.onStart();
        log.trace("onStart()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        log.trace("onDestroy()");
    }


    @Override
    protected void onRestart() {
        log.trace("onRestart()");

        super.onRestart();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main,menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_main2) {
			startActivity(new Intent(this, Main2Activity.class));
			return true;
		}

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onListFragmentInteraction(Movie movie) {

        log.trace("iteracao, movie: {}",movie.getOriginalTitle());

		if(mTwoPane) {

			Bundle args = new Bundle();
			args.putParcelable(MovieDetailsActivity.MOVIE_EXTRA_KEY, movie);

			MovieDetailsFragment df = new MovieDetailsFragment();
			df.setArguments(args);

			getSupportFragmentManager().beginTransaction()
					.replace(R.id.movie_detail_container, df, DETAILFRAGMENT_TAG)
					.commit();

		} else {

			Intent intent = new Intent(this, MovieDetailsActivity.class);
			intent.putExtra(MovieDetailsActivity.MOVIE_EXTRA_KEY, movie);

			startActivity(intent);
		}

    }

}
