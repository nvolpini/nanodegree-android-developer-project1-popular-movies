package app.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.format.Time;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import app.popularmovies.data.MovieContract;
import app.popularmovies.model.SearchParams;
import app.popularmovies.model.Video;
import app.popularmovies.service.FetchMoviesService;

/**
 * Created by neimar on 24/09/16.
 */

public class Utils {

	private static final Logger log = LoggerFactory.getLogger(Utils.class);

	private static SimpleDateFormat ISO_DATE = new SimpleDateFormat("yyyy-MM-dd");

	public static String getPreferredLanguage(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString(context.getString(R.string.pref_key_movies_language)
				, SearchParams.DEFAULT_LANGUAGE);
	}

	public static void changeParamsFromPrefs(Context context, SearchParams params) {

		if (!getPreferredLanguage(context).equals(params.getLanguage())) {
			params.setLanguage(getPreferredLanguage(context));
			Utils.setMoviesLanguageChanged(context,true);
			log.trace("language changed to: {}",params.getLanguage());
		}

		if (getMoviesToDownload(context) != params.getMoviesToDownload()) {
			params.setMoviesToDownload(getMoviesToDownload(context));
		}

	}

	public static boolean isSyncOnStart(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(context.getString(R.string.pref_key_sync_on_start), true);
	}

	public static boolean isAutoDownloadVideos(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(context.getString(R.string.pref_key_auto_download_videos), false);
	}

	public static boolean isAutoDownloadReviews(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(context.getString(R.string.pref_key_auto_download_reviews), false);
	}


	public static boolean useWifiOnly(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(context.getString(R.string.pref_key_wifi_only), false);
	}

	public static int getMoviesToDownload(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String str = prefs.getString(context.getString(R.string.pref_key_movies_to_download),"20");
		return Integer.parseInt(str);
	}

	public static boolean hasMoviesLanguageChanged(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(context.getString(R.string.pref_key_movies_language_changed), false);
	}

	public static void setMoviesLanguageChanged(Context context, boolean changed) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		prefs.edit().putBoolean(context.getString(R.string.pref_key_movies_language_changed), changed)
			.commit();


	}

	/**
	 *
	 * @param context
	 * @return time (as long) of the last download. Zero means never downloaded.
	 */
	public static long getLastDownloadDate(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getLong(context.getString(R.string.pref_key_last_download), 0);
	}

	public static void updateLastDownloadDate(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		prefs.edit().putLong(context.getString(R.string.pref_key_last_download), new Date().getTime())
				.commit();
	}

	public static boolean isOnline(Context context) {

		log.debug("checking if we have internet access.");

		ConnectivityManager cm =
				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);


		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();


		boolean state = activeNetwork != null &&
				activeNetwork.isConnectedOrConnecting();

		boolean isWiFi = activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;

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

	/**
	 * Exemplo de como obter o tamanho da tela.
	 * @param context
	 * @return
     */
	public static String getSizeName(Context context) {
		int screenLayout = context.getResources().getConfiguration().screenLayout;
		screenLayout &= Configuration.SCREENLAYOUT_SIZE_MASK;

		switch (screenLayout) {
			case Configuration.SCREENLAYOUT_SIZE_SMALL:
				return "small";
			case Configuration.SCREENLAYOUT_SIZE_NORMAL:
				return "normal";
			case Configuration.SCREENLAYOUT_SIZE_LARGE:
				return "large";
			case 4: // Configuration.SCREENLAYOUT_SIZE_XLARGE is API >= 9
				return "xlarge";
			default:
				return "undefined";
		}
	}


	public static void assertNotNull(Object obj, String message) {
		if (obj == null)
			throw new AssertionError(message);
	}


	public static long normalizeDate(long startDate) {
		// normalize the start date to the beginning of the (UTC) day
		Time time = new Time();
		time.set(startDate);
		int julianDay = Time.getJulianDay(startDate, time.gmtoff);
		return time.setJulianDay(julianDay);
	}

	public static Date toDate(String releaseDateString) {

		try {
			return releaseDateString == null ? null : ISO_DATE.parse(releaseDateString);
		} catch (ParseException e) {
			return null;
		}

	}

	public static String toIsoDate(Long longDate) {
		return longDate == null ? null :  ISO_DATE.format(new Date(longDate));
	}

	public static String toIsoDate(Date date) {
		return date == null ? null :  ISO_DATE.format(date);
	}

	public static Intent newVideoIntent(Context context, Video video) {

		String url = getVideoUrl(video);

		Intent i = null;

		if (url != null) {
			i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		}

		return i;
	}

	@Nullable
	public static String getVideoUrl(Video video) {
		String url = null;

		if ("youtube".equalsIgnoreCase(video.getSite())) {
			url = String.format("https://www.youtube.com/watch?v=%s",video.getKey());
		} else {
			log.error("video site not known: {}", video.getSite());
		}
		return url;
	}

	public static Intent newShareVideoIntent(Context context, Video video) {

		String url = getVideoUrl(video);

		Intent i = null;

		if (url != null) {

			//load the movie to get the name
			Cursor movieNameCursor = context.getContentResolver().query(MovieContract.MovieEntry.buildMovieUri(video.getMovieId())
					, new String[]{MovieContract.MovieEntry.COLUMN_TITLE}, null, null, null
			);

			movieNameCursor.moveToFirst();
			String movieTitle = movieNameCursor.getString(0);

			i = new Intent(Intent.ACTION_SEND);
			i.setType("text/plain");

			//subject is not used in some apps.
			i.putExtra(Intent.EXTRA_SUBJECT, String.format("%s",video.getName()));
			i.putExtra(Intent.EXTRA_TEXT, String.format("%s - %s - %s",movieTitle, video.getName(), url));
		}

		return i;
	}

	public static SearchParams newSearchParams(Context context) {

		SearchParams searchParams = new SearchParams();

		searchParams.setLanguage(Utils.getPreferredLanguage(context));
		searchParams.setMoviesToDownload(Utils.getMoviesToDownload(context));

		return searchParams;

	}


	public static void downloadMovies(Context context) {

		SearchParams searchParams = Utils.newSearchParams(context);


		log.trace("downloading movies, params: {}", searchParams);

		if (Utils.isOnline(context)) {

			Intent intent = FetchMoviesService.newIntent(context, searchParams);
			context.startService(intent);

		} else {
			log.trace("no internet access.");
			Toast.makeText(context, context.getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
		}
	}

}
