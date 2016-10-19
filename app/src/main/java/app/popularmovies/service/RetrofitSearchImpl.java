package app.popularmovies.service;

import android.content.ContentValues;
import android.content.Context;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import org.slf4j.Logger;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import app.popularmovies.BuildConfig;
import app.popularmovies.data.MovieContract;
import app.popularmovies.data.MoviesDbHelper;
import app.popularmovies.model.Movie;
import app.popularmovies.model.ResultsPage;
import app.popularmovies.model.Review;
import app.popularmovies.model.SearchParams;
import app.popularmovies.model.Video;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Implementation based on retrofit (http://square.github.io/retrofit/)
 *
 * Created by neimar on 26/09/16.
 */

public class RetrofitSearchImpl extends AbstractSearchImpl {

	private static final Logger log = getLogger(RetrofitSearchImpl.class);

	private static final String API_URL = "https://api.themoviedb.org/3/";


	private enum MoviesEndPoint {
		POPULAR("popular"), TOP_RATED("top_rated");

		String path;

		MoviesEndPoint(String path) {
			this.path = path;
		}

		public String getPath() {
			return path;
		}
	}

	private static final MoviesDBService service;


	private boolean moviesLanguageChanged = false;

	static {

		HttpLoggingInterceptor logging = new HttpLoggingInterceptor();


		OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
		// add your other interceptors …

		if (BuildConfig.DEBUG) {
			logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
			httpClient.addNetworkInterceptor(new StethoInterceptor()); //TODO
		} else {
			logging.setLevel(HttpLoggingInterceptor.Level.NONE);
		}

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

		ArrayList<Movie> results = new ArrayList<>(params.getMoviesToDownload());

		int pagesToDownload = params.getMoviesToDownload()/20;

		log.trace("pages to download: {}",pagesToDownload);

		Call<MoviesResult> resultCall = null;


		for (int page = 1; page <= pagesToDownload; page++) {


			log.debug("downloading results page {}",page);

			if (params.getSortBy().equals(SORT_BY_POPULARITY)) {

				resultCall = service.popularMovies(BuildConfig.MOVIESDB_API_KEY, params.getLanguage(), page);

			} else {

				resultCall = service.topRatedMovies(BuildConfig.MOVIESDB_API_KEY, params.getLanguage(), page);
			}


			try {

				MoviesResult res = resultCall.execute().body();

				results.addAll(res.getResults());

			} catch (IOException e) {
				log.error("error querying movies ({}).",params.getSortBy(),e);
				throw new MoviesDataException("Error querying movies",e);
			}
		}

		return results;

	}

	@Override
	public Movie getMovieById(int movieId) throws MoviesDataException {
		return null;
	}

	public void fetchMovies(Context context) throws MoviesDataException {

		List<Movie> movies = fetchMovies(MoviesEndPoint.POPULAR);
		saveMovies(context,movies,MoviesEndPoint.POPULAR);

		movies = fetchMovies(MoviesEndPoint.TOP_RATED);
		saveMovies(context,movies,MoviesEndPoint.TOP_RATED);

	}

	private List<Movie> fetchMovies(MoviesEndPoint endPoint) {

		log.debug("Fetching movies from '{}', Lang: '{}', movies count: {}, "
				, endPoint, params.getLanguage(),params.getMoviesToDownload());

		ArrayList<Movie> results = new ArrayList<>(params.getMoviesToDownload());

		int pagesToDownload = params.getMoviesToDownload()/20;

		log.trace("pages to download: {}",pagesToDownload);

		Call<ResultsPage<Movie>> resultCall = null;


		for (int page = 1; page <= pagesToDownload; page++) {


			log.debug("downloading results page {}",page);


			resultCall = service.movies(endPoint.getPath(),BuildConfig.MOVIESDB_API_KEY
					, params.getLanguage(), page);


			try {

				ResultsPage<Movie> res = resultCall.execute().body();

				results.addAll(res.getResults());

			} catch (IOException e) {
				log.error("error querying movies from '{}'.",endPoint,e);
				throw new MoviesDataException("Error querying movies from "+endPoint,e);
			}
		}

		return results;

	}


	private void saveMovies(Context context, List<Movie> moviesList, MoviesEndPoint endPoint) {



		Vector<ContentValues> regs = new Vector<ContentValues>(moviesList.size());

		for (Movie movie : moviesList) {

			long movieId = MoviesDbHelper.addMovie(context,movie, moviesLanguageChanged);

			ContentValues values = new ContentValues();

			if (endPoint == MoviesEndPoint.POPULAR) {

				values.put(MovieContract.PopularMoviesEntry.COLUMN_MOVIE_ID,movieId);
				values.put(MovieContract.PopularMoviesEntry.COLUMN_POSITION,regs.size()+1);

			} else {

				values.put(MovieContract.TopRatedMoviesEntry.COLUMN_MOVIE_ID,movieId);
				values.put(MovieContract.TopRatedMoviesEntry.COLUMN_POSITION,regs.size()+1);

			}

			regs.add(values);

		}//for

		int inserted = 0;
		// add to database
		if ( regs.size() > 0 ) {
			ContentValues[] cvArray = new ContentValues[regs.size()];
			regs.toArray(cvArray);
			inserted = context.getContentResolver().bulkInsert(
					endPoint == MoviesEndPoint.POPULAR
							? MovieContract.PopularMoviesEntry.CONTENT_URI
							: MovieContract.TopRatedMoviesEntry.CONTENT_URI
					, cvArray);
		}

		log.debug("Inserted {} movies into {}",inserted, endPoint);
	}


	public List<Video> fetchVideos(Movie movie) {

		log.debug("Fetching movie videos, movie: {}, Lang: '{}'"
				, movie, params.getLanguage());

		ArrayList<Video> results = new ArrayList<>();

		Call<Video.Results> resultCall = null;

			resultCall = service.videos(Integer.toString(movie.getMoviesDbId()),BuildConfig.MOVIESDB_API_KEY
					, params.getLanguage());


			try {

				Video.Results res = resultCall.execute().body();

				results.addAll(res.getVideos());

			} catch (IOException e) {
				log.error("error fetching movie videos '{}'.",movie,e);
				throw new MoviesDataException("Error fetching movie videos: "+movie,e);
			}

		return results;

	}


	public interface MoviesDBService {

		@GET("movie/popular")
		Call<MoviesResult> popularMovies(@Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);

		@GET("movie/top_rated")
		Call<MoviesResult> topRatedMovies(@Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);


		@GET("movie/{end_point}")
		Call<ResultsPage<Movie>> movies(@Path("end_point") String endPoint, @Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);
		//Call<MoviesResult> movies(@Path("end_point") String endPoint, @Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);

		@GET("movie/{movie_id}/videos")
		Call<Video.Results> videos(@Path("movie_id") String movieId, @Query("api_key") String apiKey, @Query("language") String language);

		@GET("movie/{movie_id}/reviews")
		Call<ResultsPage<Review>> reviews(@Path("movie_id") String movieId, @Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
	public static class MoviesResult implements Serializable {

		@JsonProperty("page")
		private int page;

		@JsonProperty("total_pages")
		private int totalPages;

		@JsonProperty("total_results")
		private int totalResults;

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

		public int getPage() {
			return page;
		}

		public void setPage(int page) {
			this.page = page;
		}

		public int getTotalPages() {
			return totalPages;
		}

		public void setTotalPages(int totalPages) {
			this.totalPages = totalPages;
		}

		public int getTotalResults() {
			return totalResults;
		}

		public void setTotalResults(int totalResults) {
			this.totalResults = totalResults;
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


	public boolean isMoviesLanguageChanged() {
		return moviesLanguageChanged;
	}

	public void setMoviesLanguageChanged(boolean moviesLanguageChanged) {
		this.moviesLanguageChanged = moviesLanguageChanged;
	}
}
