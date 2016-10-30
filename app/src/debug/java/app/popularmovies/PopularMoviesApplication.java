package app.popularmovies;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.DumperPluginsProvider;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.dumpapp.DumperPlugin;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;

/**
 * Created by neimar on 07/10/16.
 */

public class PopularMoviesApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		initializeStetho(this);

	}

	private void initializeStetho(final Context context) {

		Stetho.initialize(Stetho.newInitializerBuilder(context)
				.enableDumpapp(new DumperPluginsProvider() {
					@Override
					public Iterable<DumperPlugin> get() {
						return new Stetho.DefaultDumperPluginsBuilder(context)
								//.provide(new MyDumperPlugin())
								.finish();
					}
				})
				.enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context))
				.build());

		new OkHttpClient.Builder()
				.addNetworkInterceptor(new StethoInterceptor())
				.build();

	}
}
