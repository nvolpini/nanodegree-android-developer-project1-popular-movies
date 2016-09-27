package app.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.popularmovies.model.SearchParams;
import app.popularmovies.service.MoviesService;

/**
 * Created by neimar on 24/09/16.
 */

public class Utils {

    private static final Logger log = LoggerFactory.getLogger(Utils.class);


    public static String getPreferredLanguage(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_key_movies_language)
                , MoviesService.DEFAULT_LANGUAGE);
    }

    public static void changeLanguageAccordingToPrefs(Context context, SearchParams params) {

		if (!getPreferredLanguage(context).equals(params.getLanguage())) {
			params.setLanguage(getPreferredLanguage(context));
		}

	}

    public static String getDefaultSorting(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_key_default_sorting)
                , MoviesService.SORT_BY_POPULARITY);
    }

    public static boolean isSyncOnStart(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_key_sync_on_start),false);
    }

    public static boolean isOnline(Context context) {
        log.debug("checking if we have internet access.");


        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean state = cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();

        log.debug("testing via ConnectivityManager returned: {}",state);


        return state;

		/**
		 *
		 * TODO REVISOR: O código abaixo tem a vantagem de verificar se existe "acesso" de fato,
		 * pois apesar de existir uma conexão (wireless conectado), o acesso pode ser controlado
		 * por um proxy (em hoteis por exemplo, a conexão a rede é livre, mas o acesso exige um login)
		 * e nesse caso o teste acima (recomendado) vai retornar que existe uma conexão,
		 * mas as solicitações HTTP podem não se completar.
		 *
		 * A desvantagem é que ele é bem mais lento que o teste acima, BEM mais lento.
		 *
		 * Faz sentido ter essa preocupação ?
		 *
		 */
		/*
        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;*/


    }
}