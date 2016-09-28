package app.popularmovies.service;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import org.slf4j.Logger;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import app.popularmovies.BuildConfig;
import app.popularmovies.model.Movie;
import app.popularmovies.model.SearchParams;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static app.popularmovies.service.MoviesService.SORT_BY_POPULARITY;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Implementation based on retrofit (http://square.github.io/retrofit/)
 *
 * Created by neimar on 26/09/16.
 */

public class RetrofitSearchImpl extends AbstractSearchImpl {

	private static final Logger log = getLogger(RetrofitSearchImpl.class);

	private static final String API_URL = "https://api.themoviedb.org/3/";

	private static final MoviesDBService service;

	static {

		HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
		// set your desired log level
		//logging.setLevel(HttpLoggingInterceptor.Level.BODY); //TODO how to change when packaging for production ?
		logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

		OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
		// add your other interceptors …

		// add logging as last interceptor
		httpClient.addInterceptor(logging);


		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(API_URL)
				.addConverterFactory(JacksonConverterFactory.create())
				.client(httpClient.build())
				.build();


		service = retrofit.create(MoviesDBService.class);

	}

	public RetrofitSearchImpl(SearchParams params) {
		super(params);
	}

	@Override
	public List<Movie> list() throws MoviesDataException {


		validate();

		log.debug("Fetching movies. Lang: {}, sort by: {} ", params.getLanguage()
				, params.getSortBy());


		Call<MoviesResult> resultCall = null;

		if (params.getSortBy().equals(SORT_BY_POPULARITY)) {

			resultCall = service.popularMovies(BuildConfig.MOVIESDB_API_KEY, params.getLanguage());

		} else {

			resultCall = service.topRatedMovies(BuildConfig.MOVIESDB_API_KEY, params.getLanguage());
		}


		try {

			MoviesResult res = resultCall.execute().body();

			return res.getResults();

		} catch (IOException e) {
			log.error("error querying movies.",e);
			throw new MoviesDataException("Error querying movies",e);
		}



	}

	@Override
	public Movie getMovieById(int movieId) throws MoviesDataException {
		return null;
	}

	public interface MoviesDBService {

		@GET("movie/popular")
		Call<MoviesResult> popularMovies(@Query("api_key") String apiKey, @Query("language") String language);

		@GET("movie/top_rated")
		Call<MoviesResult> topRatedMovies(@Query("api_key") String apiKey, @Query("language") String language);

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
	public static class MoviesResult implements Serializable {


		@JsonProperty("results")
		private List<Movie> results;

		public MoviesResult() {
		}

		public List<Movie> getResults() {
			return results;
		}

		public void setResults(List<Movie> results) {
			this.results = results;
		}

		/**
		 * Handle unknown properties and print a message
		 *
		 * @param key
		 * @param value
		 */
		@JsonAnySetter
		public void handleUnknown(String key, Object value) {
			StringBuilder sb = new StringBuilder();
			sb.append("Unknown property: '").append(key);
			sb.append("' value: '").append(value).append("'");

			log.trace(sb.toString());
		}
	}
}
