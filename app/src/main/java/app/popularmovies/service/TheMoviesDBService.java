package app.popularmovies.service;

import android.content.ContentValues;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.util.Log;
import android.widget.ImageView;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import app.popularmovies.BuildConfig;
import app.popularmovies.R;
import app.popularmovies.Utils;
import app.popularmovies.data.MovieContract;
import app.popularmovies.data.MoviesDbHelper;
import app.popularmovies.model.Movie;
import app.popularmovies.model.MovieDetails;
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
 * <p>
 * Created by neimar on 26/09/16.
 */

public class TheMoviesDBService {

	private static final Logger log = getLogger(TheMoviesDBService.class);

	private static final String API_URL = "https://api.themoviedb.org/3/";

	public TheMoviesDBService() {
	}

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


	private static final OkHttpClient client;

	private boolean moviesLanguageChanged = false;

	static {

		HttpLoggingInterceptor logging = new HttpLoggingInterceptor();


		OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
		// add your other interceptors …

		if (BuildConfig.DEBUG) {
			logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
			httpClientBuilder.addNetworkInterceptor(new StethoInterceptor());
		} else {
			logging.setLevel(HttpLoggingInterceptor.Level.NONE);
		}

		// add logging as last interceptor
		httpClientBuilder.addInterceptor(logging);

		client = httpClientBuilder.build();

		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(API_URL)
				.addConverterFactory(JacksonConverterFactory.create())
				.client(client)
				.build();


		service = retrofit.create(MoviesDBService.class);




	}

	public static void renderImage(final Context context, final ImageView imageView, Movie movie) {

		if (movie == null) {
			log.warn("cannot render image for null movie");
			return;
		}

		Picasso picasso = new Picasso.Builder(context)
				.downloader(new OkHttp3Downloader(client))
				.build();

		try {
			Picasso.setSingletonInstance(picasso);
		} catch (IllegalStateException ignored) {
			// Picasso instance was already set
			// cannot set it after Picasso.with(Context) was already in use
		}

		if (movie.getPosterPath() == null) {

			Picasso.with(context)
					.load(R.drawable.no_poster_185)
					.into(imageView);

		} else {

			final String imageUrl = TheMoviesDBService.getMoviePosterUrl(context, movie);

			Picasso.with(context)

					.load(imageUrl)
					.networkPolicy(NetworkPolicy.OFFLINE)
					.placeholder(R.drawable.loading_poster_185)
					.error(R.drawable.no_poster_185)
					.into(imageView, new Callback() {
						@Override
						public void onSuccess() {

						}

						@Override
						public void onError() {
							//Try again online if cache failed
							Picasso.with(context)
									.load(imageUrl)
									.placeholder(R.drawable.loading_poster_185)
									.error(R.drawable.no_poster_185)
									.into(imageView, new Callback() {
										@Override
										public void onSuccess() {

										}

										@Override
										public void onError() {
											Log.v("Picasso", "Could not fetch image");
										}
									});
						}
					});
		}
	}

	@BindingAdapter({"bind:moviePoster"})
	public static void loadImage(ImageView view, Movie movie) {

		log.trace("moviePoster: {}", movie);

		String imageUrl = TheMoviesDBService.getMoviePosterUrl(view.getContext(), movie);

		Picasso.with(view.getContext())
				.load(imageUrl)
				.placeholder(R.drawable.loading_poster_185)
				.error(R.drawable.no_poster_185)
				.into(view);

		//TODO null pointer
		//TheMoviesDBService.renderImage(view.getContext(), view, movie);

	}


	public static String getMoviePosterUrl(Context context, Movie movie) {

		String screenSize = Utils.getSizeName(context);

		log.trace("screen size is: {}",screenSize);

		//TODO testar tamanho da tela do dispositivo e baixar a imagem do tamanho mais adequado

		return movie == null ? null : String.format("http://image.tmdb.org/t/p/w500/%s"
				,movie.getPosterPath());
	}

	public void fetchMovies(Context context, SearchParams params) throws MoviesDataException {

		List<Movie> movies = fetchMovies(MoviesEndPoint.POPULAR, params);
		saveMovies(context, movies, MoviesEndPoint.POPULAR, params);

		movies = fetchMovies(MoviesEndPoint.TOP_RATED, params);
		saveMovies(context, movies, MoviesEndPoint.TOP_RATED, params);

	}

	private List<Movie> fetchMovies(MoviesEndPoint endPoint, SearchParams params) throws MoviesDataException {

		if (params.getMoviesToDownload()==0 || params.getMoviesToDownload()%20 != 0) {
			params.setMoviesToDownload(20);
		}

		log.debug("Fetching movies from '{}', Lang: '{}', movies count: {}, "
				, endPoint, params.getLanguage(), params.getMoviesToDownload());

		ArrayList<Movie> results = new ArrayList<>(params.getMoviesToDownload());

		int pagesToDownload = params.getMoviesToDownload() / 20;

		log.trace("pages to download: {}", pagesToDownload);

		Call<ResultsPage<Movie>> resultCall = null;


		for (int page = 1; page <= pagesToDownload; page++) {


			log.debug("downloading results page {}", page);


			resultCall = service.movies(endPoint.getPath(), BuildConfig.MOVIESDB_API_KEY
					, params.getLanguage(), page);


			try {

				ResultsPage<Movie> res = resultCall.execute().body();

				results.addAll(res.getResults());

			} catch (IOException e) {
				log.error("error querying movies from '{}'.", endPoint, e);
				throw new MoviesDataException("Error querying movies from " + endPoint, e);
			}
		}

		return results;

	}


	private void saveMovies(Context context, List<Movie> moviesList, MoviesEndPoint endPoint
			, SearchParams params) throws MoviesDataException {


		Vector<ContentValues> regs = new Vector<ContentValues>(moviesList.size());

		for (Movie movie : moviesList) {

			//set the control fields
			movie.setMovieDownloaded(new Date().getTime());
			movie.setMovieDownloadLanguage(params.getLanguage());

			long movieId = MoviesDbHelper.addMovie(context, movie, moviesLanguageChanged);

			ContentValues values = new ContentValues();

			if (endPoint == MoviesEndPoint.POPULAR) {

				values.put(MovieContract.PopularMoviesEntry.COLUMN_MOVIE_ID, movieId);
				values.put(MovieContract.PopularMoviesEntry.COLUMN_POSITION, regs.size() + 1);

			} else {

				values.put(MovieContract.TopRatedMoviesEntry.COLUMN_MOVIE_ID, movieId);
				values.put(MovieContract.TopRatedMoviesEntry.COLUMN_POSITION, regs.size() + 1);

			}

			regs.add(values);

		}//for

		int inserted = 0;
		// add to database
		if (regs.size() > 0) {
			ContentValues[] cvArray = new ContentValues[regs.size()];
			regs.toArray(cvArray);
			inserted = context.getContentResolver().bulkInsert(
					endPoint == MoviesEndPoint.POPULAR
							? MovieContract.PopularMoviesEntry.CONTENT_URI
							: MovieContract.TopRatedMoviesEntry.CONTENT_URI
					, cvArray);
		}

		log.debug("Inserted {} movies into {}", inserted, endPoint);
	}


	public void fetchVideos(Context context, Movie movie) throws MoviesDataException {



		List<Video> videos = fetchVideos(movie, Utils.getPreferredLanguage(context));
		saveVideos(context, movie, videos);

	}

	private List<Video> fetchVideos(Movie movie, String language) throws MoviesDataException {

		log.debug("Fetching movie videos, movie: {}, Lang: '{}'"
				, movie, language);

		ArrayList<Video> results = new ArrayList<>();

		try {
			//TODO tratar fallback do idioma - so baixa no idioma passado

			Call<Video.Results> resultCall = service.videos(Integer.toString(movie.getMoviesDbId())
					, BuildConfig.MOVIESDB_API_KEY, language);

			Video.Results res = resultCall.execute().body();

			results.addAll(res.getVideos());

		} catch (IOException e) {
			log.error("error fetching movie videos '{}'.", movie, e);
			throw new MoviesDataException("Error fetching movie videos: " + movie, e);
		}

		return results;

	}

	private void saveVideos(Context context, Movie movie, List<Video> videos) throws MoviesDataException {
		Vector<ContentValues> regs = new Vector<ContentValues>(videos.size());

		for (Video video : videos) {

			video.setMovieId(movie.getId());

			ContentValues values = MoviesDbHelper.getContentValues(video);

			regs.add(values);

		}//for

		int inserted = 0;
		// add to database
		if (regs.size() > 0) {
			ContentValues[] cvArray = new ContentValues[regs.size()];
			regs.toArray(cvArray);
			inserted = context.getContentResolver().bulkInsert(
					MovieContract.VideoEntry.buildVideosUri(movie.getId())
					, cvArray);
		}

		log.debug("Inserted {} videos for movie {}", inserted, movie.getTitle());
	}


	public void fetchReviews(Context context, Movie movie, int page) throws MoviesDataException {

		List<Review> videos = fetchReviews(movie, Utils.getPreferredLanguage(context), page);
		saveReviews(context, movie, videos);

	}

	private List<Review> fetchReviews(Movie movie, String language, int page) throws MoviesDataException {

		log.debug("Fetching movie reviews, movie: {}, Lang: '{}', page: {}"
				, movie, language, page);

		ArrayList<Review> results = new ArrayList<>();

		//TODO tratar fallback do idioma - so baixa no idioma passado e reviews em portugues sao raras

		try {

			Call<ResultsPage<Review>> resultCall = service.reviews(Integer.toString(movie.getMoviesDbId())
					, BuildConfig.MOVIESDB_API_KEY
					, language, page);

			ResultsPage<Review> res = resultCall.execute().body();

			results.addAll(res.getResults());

		} catch (IOException e) {
			log.error("error fetching movie reviews '{}'.", movie, e);
			throw new MoviesDataException("Error fetching movie reviews: " + movie, e);
		}

		return results;

	}

	private void saveReviews(Context context, Movie movie, List<Review> reviews) throws MoviesDataException {
		Vector<ContentValues> regs = new Vector<ContentValues>(reviews.size());

		for (Review review : reviews) {

			review.setMovieId(movie.getId());

			//save locally only 200 characters of the review
			if (review.getContent().length()>200) {
				review.setContent(review.getContent().substring(0, 200));
			}

			ContentValues values = MoviesDbHelper.getContentValues(review);

			regs.add(values);

		}//for

		int inserted = 0;
		// add to database
		if (regs.size() > 0) {
			ContentValues[] cvArray = new ContentValues[regs.size()];
			regs.toArray(cvArray);
			inserted = context.getContentResolver().bulkInsert(
					MovieContract.ReviewEntry.buildReviewsUri(movie.getId())
					, cvArray);
		}

		log.debug("Inserted {} reviews for movie {}", inserted, movie.getTitle());
	}

	public interface MoviesDBService {

		@GET("movie/{end_point}")
		Call<ResultsPage<Movie>> movies(@Path("end_point") String endPoint, @Query("api_key") String apiKey
				, @Query("language") String language, @Query("page") int page);

		@GET("movie/{movie_id}/videos")
		Call<Video.Results> videos(@Path("movie_id") String movieId, @Query("api_key") String apiKey
				, @Query("language") String language);

		@GET("movie/{movie_id}/reviews")
		Call<ResultsPage<Review>> reviews(@Path("movie_id") String movieId, @Query("api_key") String apiKey
				, @Query("language") String language, @Query("page") int page);

		//TODO
		@GET("movie/{movie_id}")
		Call<MovieDetails> movieDetails(@Path("movie_id") String movieId, @Query("api_key") String apiKey
				, @Query("language") String language
				, @Query("append_to_response") String appendToResponse
				, @Query("page") int page);

	}


	public boolean isMoviesLanguageChanged() {
		return moviesLanguageChanged;
	}

	public void setMoviesLanguageChanged(boolean moviesLanguageChanged) {
		this.moviesLanguageChanged = moviesLanguageChanged;
	}

}
