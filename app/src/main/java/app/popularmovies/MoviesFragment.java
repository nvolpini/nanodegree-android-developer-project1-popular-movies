package app.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.popularmovies.model.Movie;
import app.popularmovies.model.SearchParams;
import app.popularmovies.service.IMovieSearch;
import app.popularmovies.service.MoviesService;

/**
 * A fragment representing a list of Items.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MoviesFragment extends Fragment {

    private static final Logger log = LoggerFactory.getLogger(MoviesFragment.class);

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    public static final String MOVIES_PARCELABLE_KEY = "moviesList";
    public static final String SEARCH_PARAMS_KEY = "searchParams";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    MyItemRecyclerViewAdapter myAdapter;

    private SearchParams searchParams;

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
        args.putParcelable(MoviesFragment.SEARCH_PARAMS_KEY, searchParams);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        log.debug("onCreate");

        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            searchParams = getArguments().getParcelable(SEARCH_PARAMS_KEY);
        }

        if (savedInstanceState == null) {

            log.debug("new moviesList...");
            moviesList = new ArrayList<>(); //???

            if (searchParams == null) {
                log.debug("no search params passed as argument. Getting default...");
                searchParams = MoviesService.get().newSearchParams();
            }

            //auto sync on start
            if(PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(getString(R.string.pref_key_sync_on_start),false)) {
                log.debug("sync on start...");
                updateMovies();
            }

        } else {

            if (savedInstanceState.containsKey(MOVIES_PARCELABLE_KEY)) {
                log.debug("restoring moviesList from state...");
                moviesList = savedInstanceState.getParcelableArrayList(MOVIES_PARCELABLE_KEY);
            }

            if (savedInstanceState.containsKey(SEARCH_PARAMS_KEY)) {
                log.debug("restoring params from state...");
                searchParams = savedInstanceState.getParcelable(SEARCH_PARAMS_KEY);
            }

        }




    }




    @Override
    public void onSaveInstanceState(Bundle outState) {

        log.debug("saving moviesList...");

        outState.putParcelableArrayList(MOVIES_PARCELABLE_KEY, moviesList);

        outState.putParcelable(SEARCH_PARAMS_KEY, searchParams);

        super.onSaveInstanceState(outState);


    }




    @Override
    public void onStart() {
        super.onStart();

        log.debug("onStart()");


        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //prefs.getBoolean(getString(R.string.pref_key_sync_on_start),false);
        //TODO test if should load on start

       // updateMovies();
    }

    @Override
    public void onPause() {
        log.debug("onPause()");

        super.onPause();
    }

    @Override
    public void onResume() {
        log.debug("onResume()");

        super.onResume();
    }

    @Override
    public void onDestroy() {
        log.debug("onDestroy()");

        super.onDestroy();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.movies,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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

        } else if (id == R.id.action_clear) {

            moviesList.clear();
            myAdapter.updateMovies(moviesList);

        }

            return super.onOptionsItemSelected(item);
    }

    private void saveSorting(String sorting) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        prefs.edit().putString(getString(R.string.pref_key_default_sorting),sorting);


    }

    private String getSorting() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        return prefs.getString(getString(R.string.pref_key_default_sorting), MoviesService.SORT_BY_POPULARITY);

    }

    private void updateMovies() {
        log.debug("updating movies, params: {}",searchParams);

        FetchMoviesTask task = new FetchMoviesTask();
        task.execute();
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

            myAdapter = new MyItemRecyclerViewAdapter(moviesList, mListener);

            recyclerView.setAdapter(myAdapter);
        }
        return view;
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


    public class FetchMoviesTask extends AsyncTask<Void,Void,List<Movie>> {


        @Override
        protected List<Movie> doInBackground(Void... params) {

            List<Movie> myMovies = new ArrayList<>();

            if (!isOnline()) {

                log.error("no internet access.");

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

            log.debug("postExecute: {}",newMovies);

            if (noConnection) {

                Toast.makeText(getActivity(),getString(R.string.no_internet_connection),Toast.LENGTH_LONG).show();

                //TODO devel mode
                Toast.makeText(getActivity(),"Using sample data",Toast.LENGTH_LONG).show();

                newMovies.addAll(MoviesService.get().getSampleData());


            }


            //update adapter
            myAdapter.updateMovies(newMovies);

        }
    }

    public boolean isOnline() {
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
