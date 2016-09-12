package app.popularmovies;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.popularmovies.model.Movie;

/**
 * Created by neimar on 11/09/16.
 */
public class MoviesJsonParser {

    private final String LOG_TAG = MoviesJsonParser.class.getSimpleName();


    public List<Movie> parse(String jsonString) throws JSONException {


        List<Movie> movies = new ArrayList<>();



        /*
        //System.out.println(jsonString);

        JSONObject jsonObject = new JSONObject(jsonString);

        Log.v(LOG_TAG,"results: "+jsonObject.getString("page"));

        System.out.println(jsonObject.toString());

        System.out.println(jsonObject.getString("page"));

        JSONArray resultsArray = jsonObject.getJSONArray("results");

        Log.v(LOG_TAG,"results: ");




        for (int i = 0; i < resultsArray.length(); i++) {


            JSONObject jsonMovie = resultsArray.getJSONObject(i);

            Movie m = parseMovie(jsonMovie);

            movies.add(m);

        }//for
*/


        return movies;

    }

    @NonNull
    private Movie parseMovie(JSONObject jsonMovie) throws JSONException {
        Movie m = new Movie();
        m.setOriginalTitle(jsonMovie.getString("original_title"));
        m.setOverview(jsonMovie.getString("overview"));
        m.setPosterPath(jsonMovie.getString("poster_path"));
        m.setReleaseDate(jsonMovie.getString("release_date"));
        m.setTitle(jsonMovie.getString("title"));
        m.setVoteAverage(jsonMovie.getDouble("vote_average"));
        return m;
    }



}
