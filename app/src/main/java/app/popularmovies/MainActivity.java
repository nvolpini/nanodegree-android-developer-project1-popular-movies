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

        //TODO review this - is it needed?
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
                            //, new MoviesFragment()
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

		/**
		 * TODO REVISOR: qual a melhor forma de monitor se uma preferencia foi alterada na tela de preferencias ?
		 * para poder atualizar a lista de filmes quando o usuário altear o idioma, por exemplo ?
		 *
		 *
		 */



        //TODO is this the best way to monitor for changes in preferences?

		/**
		 * I can use {@link android.preference.Preference.OnPreferenceChangeListener}, however
		 * usually the listener should be registered at onResume and unregister at onPause but
		 * for this case shouldn't be like this, because I want to monitor exactly when the
		 * main activity is paused because I'll be on the preferences activity ...
		 *
		 */

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
			//TODO REVISOR como salvar o estado para que ao voltar das prefs não seja necessário recarregar a lista de filmes ?
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onListFragmentInteraction(Movie movie) {

        log.trace("iteracao, movie: {}",movie.getOriginalTitle());

        Intent intent = new Intent(this,MovieDetailActivity.class);


        //TODO had to save manually, saving as parcelableExtra didnt work
        Bundle b = new Bundle();
        MoviesService.get().saveMovie(movie,b); //TODO estou salvando um bundle manualmente ao inves de usar o parcelable direto
        intent.putExtras(b);

        //TODO used to identify the existence
        intent.putExtra(MovieDetailActivity.MOVIE_ID_EXTRA_KEY,movie.getId());


		//TODO didnt work. See MovieDetailActivityFragment.onCreateView
		//TODO REVISOR isso não funcionou, não consigo restaurar no MovieDetailActivityFragment.onCreateView
        //intent.putExtra(MovieDetailActivity.MOVIE_EXTRA_KEY, movie);

        startActivity(intent);

    }

}
