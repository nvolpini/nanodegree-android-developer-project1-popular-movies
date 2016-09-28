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

import static app.popularmovies.Utils.changeLanguageAccordingToPrefs;

public class MainActivity extends AppCompatActivity implements MoviesFragment.OnListFragmentInteractionListener {

    private static final Logger log = LoggerFactory.getLogger(MainActivity.class);

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

            log.trace("params from prefs: {} ",searchParams);


            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container
							//TODO talvez usar 3 colunas de dispositivos maiores
                            ,MoviesFragment.newInstance(2, searchParams)
                    )
                    .commit();
        }


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
		changeLanguageAccordingToPrefs(this,searchParams);

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
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onListFragmentInteraction(Movie movie) {

        log.trace("iteracao, movie: {}",movie.getOriginalTitle());

        Intent intent = new Intent(this,MovieDetailActivity.class);
        intent.putExtra(MovieDetailActivity.MOVIE_EXTRA_KEY, movie);

        startActivity(intent);

    }

}
