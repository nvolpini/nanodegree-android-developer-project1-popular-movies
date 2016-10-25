package app.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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

import app.popularmovies.data.MovieContract;
import app.popularmovies.model.Movie;
import app.popularmovies.model.SearchParams;

/**
 * A fragment representing a list of movie posters.
 *
 * <p>
 *     Create new instances using {@link app.popularmovies.MoviesFragment#newInstance(int, SearchParams)}
 * </p>
 *
 * <p>
 * Can take two arguments:
 * <br>{@link MoviesFragment#SEARCH_PARAMS_PARCELABLE_KEY} - SearchParams
 * <br>{@link MoviesFragment#ARG_COLUMN_COUNT} - number of columns.
 * </p>
 *
 * <p>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 * </p>
 */
public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final Logger log = LoggerFactory.getLogger(MoviesFragment.class);

    public static final String ARG_COLUMN_COUNT = "column-count";
    public static final String MOVIES_LIST_PARCELABLE_KEY = "moviesList";
    public static final String SEARCH_PARAMS_PARCELABLE_KEY = "searchParams";
    private int mColumnCount = 2;

	private int mPosition = RecyclerView.NO_POSITION;
	private boolean mUseTodayLayout;

	private static final String SELECTED_KEY = "selected_position";

	private static final int MOVIES_LOADER = 0;

    /**
     * Will receive notifications when a movie is clicked.
     * See {@link MainActivity#onListFragmentInteraction(Movie)}
     */
    private OnListFragmentInteractionListener mListener;


	MoviesListCursorAdapter moviesListAdapter;

    /**
     * Holds the search parameters used to fetch the movies.
     * Saved and restored as state
     */
    private SearchParams searchParams;

    /**
     * indicates no connection on the last fetch attempt
     */
    private boolean noConnection = true;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MoviesFragment() {
    }

	/**
	 * @param columnCount
	 * @param searchParams
	 * @return
	 */
    public static MoviesFragment newInstance(int columnCount, SearchParams searchParams) {
        MoviesFragment fragment = new MoviesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putParcelable(MoviesFragment.SEARCH_PARAMS_PARCELABLE_KEY, searchParams);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        log.trace("onCreate");

        setHasOptionsMenu(true);

		//TODO agora o fragment e criado automaticamente (direto no layout)
		//remover isso
        //define the search params via argument
        if (getArguments() != null) {
			log.trace("argumentos recebidos !!");
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            searchParams = getArguments().getParcelable(SEARCH_PARAMS_PARCELABLE_KEY);
        }

        //no previous instance saved, create new data
        if (savedInstanceState == null) {

            log.trace("new moviesList...");
            //moviesList = new ArrayList<>(); //empty movies list

            if (searchParams == null) {
                log.trace("no search params passed as argument. Getting default...");
                searchParams = Utils.newSearchParams(getActivity());
            }

            //TODO Do this here or at onStart() - neither seem to be right - check loaders

            //auto sync on start
            //if(Utils.isSyncOnStart(getActivity())) {
              //  downloadMovies();
            //}

        } else { //previous state saved, restore


            if (savedInstanceState.containsKey(SEARCH_PARAMS_PARCELABLE_KEY)) {
                log.trace("restoring params from state...");
                searchParams = savedInstanceState.getParcelable(SEARCH_PARAMS_PARCELABLE_KEY);
            }

        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.movies_fragment, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            moviesListAdapter = new MoviesListCursorAdapter(getActivity(), null, mListener) ;

            recyclerView.setAdapter(moviesListAdapter);
        }


		if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
			// The listview probably hasn't even been populated yet.  Actually perform the
			// swapout in onLoadFinished.
			mPosition = savedInstanceState.getInt(SELECTED_KEY);
		}

        return view;
    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		getLoaderManager().initLoader(MOVIES_LOADER, null, this);
		super.onActivityCreated(savedInstanceState);
	}

	// since we read the location when we create the loader, all we need to do is restart things
	void onLocationChanged( ) {
		//updateWeather();
		getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
	}

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.movies,menu);

    }

    /**
     * TODO highlight the selected sorting option, so the users know the current sorting param
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

		if (id == R.id.action_sort_by_popularity) {

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

        return super.onOptionsItemSelected(item);
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

		Uri uri = MovieContract.PopularMoviesEntry.CONTENT_URI;

		if (searchParams.isSortByRating()) {
			uri = MovieContract.TopRatedMoviesEntry.CONTENT_URI;

		} else if (searchParams.isSortByFavorites()) {
			uri = MovieContract.FavoriteMoviesEntry.CONTENT_URI;
		}

		log.trace("onCreateLoader, uri: {}",uri);

		return new CursorLoader(getActivity(),
				uri,
				null,
				null,
				null,
				null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		log.trace("onLoadFinished..");

		moviesListAdapter.swapCursor(data);
		if (mPosition != RecyclerView.NO_POSITION) {

			log.trace("position: {}", mPosition);

			// If we don't need to restart the loader, and there's a desired position to restore
			// to, do so now.
			RecyclerView recyclerView = (RecyclerView) getView();
			recyclerView.smoothScrollToPosition(mPosition);
		}
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
		log.trace("onLoaderReset..");
		moviesListAdapter.swapCursor(null);
    }

    public void setSearchParams(SearchParams searchParams) {
		this.searchParams = searchParams;
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
        void onListFragmentInteraction(Movie item);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

        log.trace("saving moviesList...");

        //outState.putParcelableArrayList(MOVIES_LIST_PARCELABLE_KEY, moviesList);

        outState.putParcelable(SEARCH_PARAMS_PARCELABLE_KEY, searchParams);

		if (mPosition != RecyclerView.NO_POSITION) {
			outState.putInt(SELECTED_KEY, mPosition);
		}

        super.onSaveInstanceState(outState);


    }




    @Override
    public void onStart() {
        super.onStart();

        log.trace("onStart()");

        //where to autoload ?
        //TODO check loaders - https://developer.android.com/guide/components/loaders.html
       // downloadMovies();
    }

    @Override
    public void onPause() {
        super.onPause();
        log.trace("onPause()");

    }

    @Override
    public void onResume() {
        super.onResume();
        log.trace("onResume()");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log.trace("onDestroy()");

    }



}
