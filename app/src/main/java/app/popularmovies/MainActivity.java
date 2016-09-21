package app.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class MainActivity extends AppCompatActivity implements MoviesFragment.OnListFragmentInteractionListener
    ,SharedPreferences.OnSharedPreferenceChangeListener {

    private static final Logger log = LoggerFactory.getLogger(MainActivity.class);

    private SearchParams searchParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        log.debug("onCreate");

        //reset all prefs
        //PreferenceManager.getDefaultSharedPreferences(this).edit().clear().apply();

        //TODO review this - is it needed?
        PreferenceManager.setDefaultValues(this,R.xml.pref_general,false);
        PreferenceManager.setDefaultValues(this,R.xml.pref_data_sync,false);


        if (savedInstanceState != null && savedInstanceState.containsKey(MoviesFragment.SEARCH_PARAMS_KEY)) {

            searchParams = savedInstanceState.getParcelable(MoviesFragment.SEARCH_PARAMS_KEY);

            log.debug("loading params from state: {}",searchParams);

        } else if (savedInstanceState == null) {

            searchParams = MoviesService.get().newSearchParams();

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

            searchParams.setLanguage(prefs.getString(getString(R.string.pref_key_movies_language), MoviesService.DEFAULT_LANGUAGE));
            searchParams.setSortBy(prefs.getString(getString(R.string.pref_key_default_sorting), MoviesService.SORT_BY_POPULARITY));

            log.debug("params from prefs: {} ",searchParams);


            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container
                            //, new MoviesFragment()
                            ,MoviesFragment.newInstance(2, searchParams)
                    )
                    .commit();
        }


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {

        log.debug("saving state...");

        outState.putParcelable(MoviesFragment.SEARCH_PARAMS_KEY, searchParams);

        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        log.debug("restoring state...");

    }

    @Override
    protected void onResume() {
        super.onResume();
        log.debug("onResume()");

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

    }


    @Override
    protected void onPause() {
        super.onPause();
        log.debug("onPause()");

        //PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);

    }


    @Override
    protected void onStart() {
        super.onStart();
        log.debug("onStart()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        log.debug("onDestroy()");
    }


    @Override
    protected void onRestart() {
        log.debug("onRestart()");

        super.onRestart();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main,menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onListFragmentInteraction(Movie movie) {

        log.debug("iteracao, movie: {}",movie.getOriginalTitle());

        Intent intent = new Intent(this,MovieDetailActivity.class);

        //TODO rever
        intent.putExtra(MovieDetailActivity.MOVIE_ID_EXTRA_KEY,movie.getId());


        Bundle b = new Bundle();
        MoviesService.get().saveMovie(movie,b);
        intent.putExtras(b);

        //didnt work
        //intent.putExtra(MovieDetailActivity.MOVIE_EXTRA_KEY, movie);

        startActivity(intent);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        log.debug("pref changed: {}",key);

        if (key.equals(getString(R.string.pref_key_default_sorting))) {
            searchParams.setSortBy(sharedPreferences.getString(getString(R.string.pref_key_default_sorting),MoviesService.SORT_BY_POPULARITY));
            log.debug("pref changed '{}' to '{}'",key,searchParams.getSortBy());
        }

        if (key.equals(getString(R.string.pref_key_movies_language))) {
            searchParams.setLanguage(sharedPreferences.getString(getString(R.string.pref_key_movies_language),MoviesService.DEFAULT_LANGUAGE));
            log.debug("pref changed '{}' to '{}'",key,searchParams.getLanguage());
        }

    }
}
