package app.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

/**
 * Show the movie details.
 */
public class MovieDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
		, View.OnClickListener {

	private static final Logger log = LoggerFactory.getLogger(MovieDetailsFragment.class);

	private static final String MOVIE_PARAM_KEY = MovieDetailsFragment.class.getName().concat(".movie");


	/**
	 * The current movie.
	 * from extras/arguments
	 */
	private Movie movie;

	private OnMovieDetailsInteractionListener mListener;

	private static final int VIDEOS_LOADER = 0;
	private VideosListCursorAdapter videosListAdapter;

	private static final int REVIEWS_LOADER = 1;
	private ReviewListCursorAdapter reviewListAdapter;

	public MovieDetailsFragment() {
	}

	public static MovieDetailsFragment newInstance(Movie movie) {
		Bundle args = new Bundle();
		args.putParcelable(MOVIE_PARAM_KEY, movie);

		MovieDetailsFragment df = new MovieDetailsFragment();
		df.setArguments(args);
		return df;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		//View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

		FragmentMovieDetailsBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_details, container, false);
		View rootView = binding.getRoot();

		Bundle arguments = getArguments();

		if (arguments != null) { //Fragment replaced

			movie = arguments.getParcelable(MOVIE_PARAM_KEY);
		}

		log.trace("movie detail: {}", movie);

		if (movie != null) {
			binding.setMovie(movie);
		}

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

			if (Utils.isOnline(getContext())) {

				FetchVideosTask task = new FetchVideosTask();
				task.execute();

			} else {
				toastNoConnection();
			}

		} else if (v.getId() == R.id.downloadReviews) {

			if (Utils.isOnline(getContext())) {
				FetchReviewsTask task = new FetchReviewsTask();
				task.execute();

			} else {
				toastNoConnection();
			}
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
		getLoaderManager().initLoader(VIDEOS_LOADER, null, this);
		getLoaderManager().initLoader(REVIEWS_LOADER, null, this);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {

		if(movie == null) {
			return null;
		}

		if (id == VIDEOS_LOADER) {

			Uri uri = MovieContract.VideoEntry.buildVideosUri(movie.getId());
			log.trace("onCreateLoader({}) - videos, uri: {}", id, uri);

			return new CursorLoader(getActivity(), uri, null, null, null, null);

		} else {

			Uri uri = MovieContract.ReviewEntry.buildReviewsUri(movie.getId());
			log.trace("onCreateLoader({}) - reviews, uri: {}", id, uri);

			return new CursorLoader(getActivity(), uri, null, null, null, null);
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		log.trace("onLoadFinished, loader: {}", loader.getId());

		if (loader.getId() == VIDEOS_LOADER) {
			videosListAdapter.swapCursor(data);
		} else {
			reviewListAdapter.swapCursor(data);
		}

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		log.trace("onLoaderReset, loader: {}", loader.getId());

		if (loader.getId() == VIDEOS_LOADER) {
			videosListAdapter.swapCursor(null);
		} else {
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

