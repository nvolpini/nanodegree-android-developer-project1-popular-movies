package app.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.popularmovies.data.MovieContract;
import app.popularmovies.data.MoviesDbHelper;
import app.popularmovies.databinding.FragmentMovieDetailsBinding;
import app.popularmovies.model.Movie;
import app.popularmovies.model.Review;
import app.popularmovies.model.Video;
import app.popularmovies.service.TheMoviesDBService;
import app.popularmovies.util.ReviewListCursorAdapter;
import app.popularmovies.util.VideosListCursorAdapter;

/**
 * Show the movie details.
 */
public class MovieDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
		, View.OnClickListener {

	private static final Logger log = LoggerFactory.getLogger(MovieDetailsFragment.class);

	private static final String MOVIE_ID_PARAM_KEY = "movie_id";


	private FragmentMovieDetailsBinding binding;

	private Long movieId;

	private OnMovieDetailsInteractionListener mListener;

	private static final int MOVIE_LOADER = 0;
	private Movie movie;

	private static final int VIDEOS_LOADER = 1;
	private VideosListCursorAdapter videosListAdapter;

	private static final int REVIEWS_LOADER = 2;
	private ReviewListCursorAdapter reviewListAdapter;


	public MovieDetailsFragment() {
	}

	public static MovieDetailsFragment newInstance(Long movieId) {

		Bundle args = new Bundle();

		if (movieId != null) {
			args.putLong(MOVIE_ID_PARAM_KEY, movieId);
		}

		MovieDetailsFragment df = new MovieDetailsFragment();
		df.setArguments(args);
		return df;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);




	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		//View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_details, container, false);
		View rootView = binding.getRoot();

		rootView.findViewById(R.id.favoriteIcon).setOnClickListener(this);

		rootView.findViewById(R.id.downloadVideos).setOnClickListener(this);


		RecyclerView videosRecyclerView = (RecyclerView) rootView.findViewById(R.id.videoListView);
		videosRecyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
		videosListAdapter = new VideosListCursorAdapter(rootView.getContext(), null, mListener);
		videosRecyclerView.setAdapter(videosListAdapter);

		rootView.findViewById(R.id.downloadReviews).setOnClickListener(this);

		RecyclerView reviewRecyclerView = (RecyclerView) rootView.findViewById(R.id.reviewListView);
		reviewRecyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
		reviewListAdapter = new ReviewListCursorAdapter(rootView.getContext(), null, mListener);
		reviewRecyclerView.setAdapter(reviewListAdapter);


		return rootView;
	}


	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.favoriteIcon) {

			ImageButton bar = (ImageButton) v.findViewById(R.id.favoriteIcon);

			MoviesDbHelper.setFavorite(getActivity(), movie, !movie.isFavorite());

		} else if (v.getId() == R.id.downloadVideos) {

			downloadVideos();

		} else if (v.getId() == R.id.downloadReviews) {

			downloadReviews();
		}
	}

	private void downloadReviews() {
		if (Utils.isOnline(getContext())) {
			FetchReviewsTask task = new FetchReviewsTask();
			task.execute();

		} else {
			toastNoConnection();
		}
	}

	private void downloadVideos() {
		if (Utils.isOnline(getContext())) {

			FetchVideosTask task = new FetchVideosTask();
			task.execute();

		} else {
			toastNoConnection();
		}
	}

	private void toastNoConnection() {
		Toast.makeText(getActivity(),getString(R.string.no_internet_connection),Toast.LENGTH_LONG).show();
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnMovieDetailsInteractionListener) {
			mListener = (OnMovieDetailsInteractionListener) context;
		} else {
			throw new RuntimeException(context.toString()
					+ " must implement OnMovieDetailsInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {


		Bundle arguments = getArguments();

		if (arguments != null && arguments.containsKey(MOVIE_ID_PARAM_KEY)) { //Fragment replaced

			movieId = arguments.getLong(MOVIE_ID_PARAM_KEY);
		}

		log.trace("onActivityCreated: {}", movieId);

		getLoaderManager().initLoader(MOVIE_LOADER, null, this);
		getLoaderManager().initLoader(VIDEOS_LOADER, null, this);
		getLoaderManager().initLoader(REVIEWS_LOADER, null, this);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {

		if(movieId == null || movieId == 0) {
			log.error("no movieId");
			return null;
		}

		if (id == MOVIE_LOADER) {

			Uri uri = MovieContract.MovieEntry.buildMovieUri(movieId);

			log.trace("onCreateLoader({}) - movie, uri: {}", id, uri);

			return new CursorLoader(getActivity(), uri, null, null, null, null);

		} else if (id == VIDEOS_LOADER) {

			Uri uri = MovieContract.VideoEntry.buildVideosUri(movieId);
			log.trace("onCreateLoader({}) - videos, uri: {}", id, uri);

			return new CursorLoader(getActivity(), uri, null, null, null, null);

		} else if (id == REVIEWS_LOADER) {

			Uri uri = MovieContract.ReviewEntry.buildReviewsUri(movieId);
			log.trace("onCreateLoader({}) - reviews, uri: {}", id, uri);

			return new CursorLoader(getActivity(), uri, null, null, null, null);
		}

		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		log.trace("onLoadFinished, loader: {}", loader.getId());

		if (loader.getId() == MOVIE_LOADER) {

			movie = MoviesDbHelper.cursorToMovie(data);

			log.trace("movie detail: {}", movie);

			binding.setMovie(movie);

			if (Utils.isAutoDownloadVideos(getActivity()) && (movie.getVideosDownloaded() == null || movie.getVideosDownloaded() == 0)) {
				log.trace("auto downloading videos...");
				downloadVideos();
			}

			if (Utils.isAutoDownloadReviews(getActivity()) && (movie.getReviewsDownloaded() == null || movie.getReviewsDownloaded() == 0)) {
				log.trace("auto downloading reviews...");
				downloadReviews();
			}




		} else if (loader.getId() == VIDEOS_LOADER) {
			videosListAdapter.swapCursor(data);

		} else if (loader.getId() == REVIEWS_LOADER) {
			reviewListAdapter.swapCursor(data);

		}

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		log.trace("onLoaderReset, loader: {}", loader.getId());

		if (loader.getId() == MOVIE_LOADER) {
			movie = null;
			binding.invalidateAll();

		} else if (loader.getId() == VIDEOS_LOADER) {
			videosListAdapter.swapCursor(null);

		} else if (loader.getId() == REVIEWS_LOADER) {
			reviewListAdapter.swapCursor(null);

		}
	}

	/**
	 * Implemented by activities that own this fragment to be able to interact with
	 * the movie data.
	 */
	public interface OnMovieDetailsInteractionListener {

		/**
		 * Called when a movie video is selected
		 *
		 * @param video
		 */
		void onVideoInteraction(Video video);

		/**
		 * Called when a review is selected
		 *
		 * @param review
		 */
		void onReviewInteraction(Review review);

		void onShareVideoInteraction(Video video);
	}


	@Override
	public void onSaveInstanceState(Bundle outState) {

		log.trace("saving movie details state...");


		super.onSaveInstanceState(outState);


	}


	public class FetchVideosTask extends AsyncTask<Void, Void, Void> {


		@Override
		protected Void doInBackground(Void... taskParams) {

			TheMoviesDBService s = new TheMoviesDBService();

			s.fetchVideos(getActivity(), movie);

			return null;
		}
	}


	public class FetchReviewsTask extends AsyncTask<Void, Void, Void> {


		@Override
		protected Void doInBackground(Void... taskParams) {

			TheMoviesDBService s = new TheMoviesDBService();

			//TODO page
			s.fetchReviews(getActivity(), movie, 1);

			return null;
		}
	}
}

