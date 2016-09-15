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
import app.popularmovies.model.MoviesSearch;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbDiscover;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.Discover;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

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
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    MyItemRecyclerViewAdapter myAdapter;

    private MoviesSearch searchCriteria;

    private List<Movie> moviesList = new ArrayList<>();

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


    public static MoviesFragment newInstance(int columnCount) {
        MoviesFragment fragment = new MoviesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //sample data
        /*
        moviesList = new ArrayList<>();
        moviesList.add(new Movie(1,"Filme1"));
        moviesList.add(new Movie(2,"Filme2"));*/

        searchCriteria = new MoviesSearch();

        if (savedInstanceState != null && savedInstanceState.containsKey("sorting")) {
            //searchCriteria.setSortBy(MoviesSearch.Sorting.valueOf(savedInstanceState.getString("sorting")));
        }

        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //prefs.getBoolean(getString(R.string.pref_key_sync_on_start),false);
        //TODO test if should load on start

       // updateMovies();
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

            searchCriteria.setSortBy(MoviesSearch.Sorting.POPULARITY);
            updateMovies();
            return true;

        } else if (id == R.id.action_sort_by_rating) {

            searchCriteria.setSortBy(MoviesSearch.Sorting.RATING);
            updateMovies();
            return true;

        } else if (id == R.id.action_clear) {

            moviesList.clear();
            myAdapter.updateMovies(moviesList);

        }

            return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //outState.putString("sorting",searchCriteria.getSortBy().name());

    }

    private void updateMovies() {
        log.debug("updating movies...");

        FetchMoviesTask task = new FetchMoviesTask();



        task.execute(searchCriteria);
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


    public class FetchMoviesTask extends AsyncTask<MoviesSearch,Void,List<Movie>> {

        //private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        //private static MoviesJsonParser parser = new MoviesJsonParser();

        @Override
        protected List<Movie> doInBackground(MoviesSearch... params) {

            List<Movie> myMovies = new ArrayList<>();

            if (!isOnline()) {

                log.error("no internet access.");

                noConnection = true;

                return myMovies;

            }

            noConnection = false;


            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            searchCriteria.setLanguage(prefs.getString(getString(R.string.pref_key_movies_language),"en"));

            log.debug("search crit: {}",searchCriteria);

            TmdbApi moviesApi = new TmdbApi(BuildConfig.MOVIESDB_API_KEY);

            TmdbMovies movies = moviesApi.getMovies();

            //TmdbSearch seach = moviesApi.getSearch();
            //seach.searchMovie("",2016,searchCriteria.getLanguage(),false,1);

            TmdbDiscover search = moviesApi.getDiscover();

            Discover discover = new Discover();
            discover.language(searchCriteria.getLanguage());
            discover.year(2016);
            //discover.primaryReleaseYear(2016);
            //discover.getParams().put("primary_release_date.gte","2014-01-01");
            discover.page(1);
            //discover.includeAdult(false);

            if (searchCriteria.getSortBy() == MoviesSearch.Sorting.POPULARITY) {
                discover.sortBy("popularity.desc");
            } else {
                discover.sortBy("vote_average.desc");
            }



            MovieResultsPage res = search.getDiscover(discover);




            int limit = 5;//TODO PREFS



            for (MovieDb md : res.getResults()) {

                Movie m = new Movie();
                m.setId(md.getId());
                m.setTitle(md.getTitle());
                m.setReleaseDate(md.getReleaseDate());
                m.setPosterPath(md.getPosterPath());
                m.setOverview(md.getOverview());
                m.setOriginalTitle(md.getOriginalTitle());
                m.setVoteAverage(md.getVoteAverage());
                myMovies.add(m);
            }



            return myMovies;

        }

        @Override
        protected void onPostExecute(List<Movie> movies) {

            log.debug("postExecute: {}",movies);

            //update adapter
            myAdapter.updateMovies(movies);

            if (noConnection) {

                Toast.makeText(getActivity(),getString(R.string.no_internet_connection),Toast.LENGTH_LONG).show();

            }

        }
    }

    public boolean isOnline() {
        log.debug("checking if we have internet access.");

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
