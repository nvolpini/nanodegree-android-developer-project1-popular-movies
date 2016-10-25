package app.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.popularmovies.model.Movie;
import app.popularmovies.model.MoviesListFilter;
import app.popularmovies.util.MoviesListCursorAdapter;

/**
 * Created by neimar on 24/10/16.
 */

public class MoviesListFragment  extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {

	private static final Logger log = LoggerFactory.getLogger(MoviesListFragment.class);

	private static final String FILTER_KEY = "filter";
	private static final String POSITION_KEY = "selected_position";

	private static final int MOVIES_LOADER = 0;

	private MoviesListCursorAdapter moviesListAdapter;

	private MoviesListFilter filter;

	private int gridColumns = 2;

	private int currentPosition = RecyclerView.NO_POSITION;

	//TODO move interface here
	private OnListFragmentInteractionListener mListener;

	public static MoviesListFragment newInstance(MoviesListFilter filter) {
		MoviesListFragment fragment = new MoviesListFragment();
		Bundle args = new Bundle();
		args.putParcelable(FILTER_KEY, filter);
		fragment.setArguments(args);
		return fragment;
	}

	/**
	 * Do not use this constructor
	 * <br/>
	 * To create new instances use {@link #newInstance(MoviesListFilter)}
	 */
	public MoviesListFragment() {
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		//setHasOptionsMenu(true);

		filter = getArguments().getParcelable(FILTER_KEY);

		log.trace("onCreate, filter: {}", filter.toString());

	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.movies_list_fragment, container, false);
		// Set the adapter
		if (view instanceof RecyclerView) {
			Context context = view.getContext();
			RecyclerView recyclerView = (RecyclerView) view;
			if (gridColumns <= 1) {
				recyclerView.setLayoutManager(new LinearLayoutManager(context));
			} else {
				recyclerView.setLayoutManager(new GridLayoutManager(context, gridColumns));
			}

			moviesListAdapter = new MoviesListCursorAdapter(getActivity(), null, mListener) ;

			recyclerView.setAdapter(moviesListAdapter);
		}


		if (savedInstanceState != null && savedInstanceState.containsKey(POSITION_KEY)) {
			currentPosition = savedInstanceState.getInt(POSITION_KEY);
		}

		return view;
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		//inflater.inflate(R.menu.movies,menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
/*

		if (id == R.id.action_refresh) {

			downloadMovies();

			//getLoaderManager().restartLoader(FORECAST_LOADER, null, this);

			return true;

		} else if (id == R.id.action_sort_by_popularity) {

			searchParams.setSortBy(SearchParams.SORT_BY_POPULARITY);
			getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
			//downloadMovies();

			return true;

		} else if (id == R.id.action_sort_by_rating) {

			searchParams.setSortBy(SearchParams.SORT_BY_RATING);
			getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
			//downloadMovies();

			return true;

		} else if (id == R.id.action_favorites) {

			searchParams.setSortBy(SearchParams.SORT_BY_FAVORITES);
			getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
			//downloadMovies();

			return true;


		} else if (id == R.id.action_clear) { //DEBUG purposes

			//moviesList.clear();
			//moviesListAdapter.downloadMovies(moviesList);

			return true;
		}
*/

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		getLoaderManager().initLoader(MOVIES_LOADER, null, this);
		super.onActivityCreated(savedInstanceState);
	}


	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnListFragmentInteractionListener) {
			mListener = (OnListFragmentInteractionListener) context;
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
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Uri uri = filter.getUri();
		return new CursorLoader(getActivity(),uri,null,null,null,null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		moviesListAdapter.swapCursor(data);
		if (currentPosition != RecyclerView.NO_POSITION) {
			//log.trace("returning to position: {}", currentPosition);
			RecyclerView recyclerView = (RecyclerView) getView();
			recyclerView.smoothScrollToPosition(currentPosition);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		moviesListAdapter.swapCursor(null);
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnListFragmentInteractionListener {
		void onListFragmentInteraction(Movie movie);
	}

}
