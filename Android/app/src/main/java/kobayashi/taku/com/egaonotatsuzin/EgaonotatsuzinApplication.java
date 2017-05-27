package kobayashi.taku.com.egaonotatsuzin;

import android.app.Application;

public class EgaonotatsuzinApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		ImageCacheManager.getInstance(ImageCacheManager.class).init(this);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}
}
