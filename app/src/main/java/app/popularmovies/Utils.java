package app.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
		return prefs.getBoolean(context.getString(R.string.pref_key_sync_on_start), false);
	}

	public static boolean useWifiOnly(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(context.getString(R.string.pref_key_wifi_only), false);
	}

	public static boolean isOnline(Context context) {

		log.debug("checking if we have internet access.");

		ConnectivityManager cm =
				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);


		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();


		boolean state = activeNetwork != null &&
				activeNetwork.isConnectedOrConnecting();

		boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;

		log.debug("testing via ConnectivityManager returned: {}, isWifi: {}", state, isWiFi);

		if (state && !isWiFi && Utils.useWifiOnly(context)) {
			log.trace("Connection allowed only via wifi. No connection then...");
			return false;
		}

		return state;

		//another way to check, this will guarantee that we can access remote data. Much slower though
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
