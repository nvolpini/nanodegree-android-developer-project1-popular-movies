package app.popularmovies;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import app.popularmovies.model.Movie;
import app.popularmovies.model.SearchParams;
import app.popularmovies.service.IMovieSearch;
import app.popularmovies.service.MoviesService;

/**
 * A fragment representing a list of movie posters.
 *
 * <p>
 *     Create new instances using {@link MoviesFragment#newInstance(int, SearchParams)}
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
public class MoviesFragment extends Fragment  {

    private static final Logger log = LoggerFactory.getLogger(MoviesFragment.class);

    private static final String ARG_COLUMN_COUNT = "column-count";
    public static final String MOVIES_LIST_PARCELABLE_KEY = "moviesList";
    public static final String SEARCH_PARAMS_PARCELABLE_KEY = "searchParams";
    private int mColumnCount = 2;

    /**
     * Will receive notifications when a movie is clicked.
     * See {@link MainActivity#onListFragmentInteraction(Movie)}
     */
    private OnListFragmentInteractionListener mListener;


    MoviesListRecyclerViewAdapter moviesListAdapter;

    /**
     * Holds the search parameters used to fetch the movies.
     * Saved and restored as state
     */
    private SearchParams searchParams;

    /**
     * Holds the movies list.
     * Saved and restored as state
     */
    private ArrayList<Movie> moviesList = new ArrayList<>();

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

        //define the search params via argument
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            searchParams = getArguments().getParcelable(SEARCH_PARAMS_PARCELABLE_KEY);
        }

        //no previous instance saved, create new data
        if (savedInstanceState == null) {

            log.trace("new moviesList...");
            moviesList = new ArrayList<>(); //empty movies list

            if (searchParams == null) {
                log.trace("no search params passed as argument. Getting default...");
                searchParams = MoviesService.get().newSearchParams();
            }

            //TODO Do this here or at onStart() - neither seem to be right - check loaders

            //auto sync on start
            if(Utils.isSyncOnStart(getActivity())) {
                updateMovies();
            }

        } else { //previous state saved, restore

            if (savedInstanceState.containsKey(MOVIES_LIST_PARCELABLE_KEY)) {
                log.trace("restoring moviesList from state...");
                moviesList = savedInstanceState.getParcelableArrayList(MOVIES_LIST_PARCELABLE_KEY);
            }

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

            moviesListAdapter = new MoviesListRecyclerViewAdapter(moviesList, mListener);

            recyclerView.setAdapter(moviesListAdapter);
        }
        return view;
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

        if (id == R.id.action_refresh) {

            updateMovies();

            return true;

        } else if (id == R.id.action_sort_by_popularity) {

            searchParams.setSortBy(MoviesService.SORT_BY_POPULARITY);
            updateMovies();

            return true;

        } else if (id == R.id.action_sort_by_rating) {

            searchParams.setSortBy(MoviesService.SORT_BY_RATING);
            updateMovies();

            return true;

        } else if (id == R.id.action_clear) { //DEBUG purposes

            moviesList.clear();
            moviesListAdapter.updateMovies(moviesList);

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
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

        outState.putParcelableArrayList(MOVIES_LIST_PARCELABLE_KEY, moviesList);

        outState.putParcelable(SEARCH_PARAMS_PARCELABLE_KEY, searchParams);

        super.onSaveInstanceState(outState);


    }




    @Override
    public void onStart() {
        super.onStart();

        log.trace("onStart()");

        //TODO where to autoload
        //TODO check loaders - https://developer.android.com/guide/components/loaders.html
       // updateMovies();
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


    private void updateMovies() {
        log.trace("updating movies, params: {}",searchParams);

        FetchMoviesTask task = new FetchMoviesTask();
        task.execute();
    }


    public class FetchMoviesTask extends AsyncTask<Void,Void,List<Movie>> {


        @Override
        protected List<Movie> doInBackground(Void... params) {

            List<Movie> myMovies = new ArrayList<>();

            if (!Utils.isOnline()) {

                log.trace("no internet access.");

                noConnection = true;

                return myMovies;

            }

            noConnection = false;


            IMovieSearch search = MoviesService.get().newSearch(searchParams);

            myMovies = search.list();


            return myMovies;

        }

        @Override
        protected void onPostExecute(List<Movie> newMovies) {

            log.trace("postExecute: {}",newMovies);

            if (noConnection) {

                Toast.makeText(getActivity(),getString(R.string.no_internet_connection),Toast.LENGTH_LONG).show();

                //TODO devel mode
                Toast.makeText(getActivity(),"Using sample data",Toast.LENGTH_LONG).show();

                newMovies.addAll(MoviesService.get().getSampleData());


            }


            //update adapter
            moviesListAdapter.updateMovies(newMovies);

        }
    }

}
