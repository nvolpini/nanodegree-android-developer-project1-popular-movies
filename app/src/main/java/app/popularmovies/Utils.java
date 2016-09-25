package app.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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

    public static String getDefaultSorting(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_key_default_sorting)
                , MoviesService.SORT_BY_POPULARITY);
    }

    public static boolean isSyncOnStart(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_key_sync_on_start),false);
    }

    public static boolean isOnline() {
        log.debug("checking if we have internet access.");

/*
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean state = cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();

        log.debug("testing via ConnectivityManager returned: {}",state);


        return state;*/


        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;


    }
}
