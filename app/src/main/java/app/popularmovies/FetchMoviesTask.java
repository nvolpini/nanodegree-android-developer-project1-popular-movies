package app.popularmovies;

import android.content.Context;
import android.os.AsyncTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.popularmovies.model.SearchParams;
import app.popularmovies.service.MoviesService;

/**
 * Created by neimar on 02/10/16.
 */

public class FetchMoviesTask extends AsyncTask<SearchParams,Void,Void> {

	private static final Logger log = LoggerFactory.getLogger(FetchMoviesTask.class);

	private final Context mContext;

	public FetchMoviesTask(Context context) {
		mContext = context;
	}
	
	@Override
	protected Void doInBackground(SearchParams... params) {

		if (params.length == 0) {
			return null;
		}

		SearchParams searchParams = params[0];

		try {

			MoviesService.get().downloadAndSaveMovies(mContext,searchParams);



		} catch (Exception e) {

			log.error("error fetching and saving movies.",e);

			//TODO tratar/user level

		}

		return null;
	}

}