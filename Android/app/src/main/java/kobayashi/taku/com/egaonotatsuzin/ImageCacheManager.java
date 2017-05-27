package kobayashi.taku.com.egaonotatsuzin;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ImageCacheManager extends ContextSingletonBase{
  private HashMap<String, Bitmap> mAssetImageCaches = new HashMap<String, Bitmap>();

  public void init(Context context){
    super.init(context);
  }

  public Bitmap getImageFromAsset(String path){
    if(mAssetImageCaches.containsKey(path) && !mAssetImageCaches.get(path).isRecycled()){
      return mAssetImageCaches.get(path);
    }
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
    AssetManager mngr = context.getAssets();
    try {
      Log.d(Config.TAG, "--------------------------------------");
      Log.d(Config.TAG, path);
      InputStream is = mngr.open(path);
      Bitmap image = BitmapFactory.decodeStream(is, null, options);
      mAssetImageCaches.put(path, image);
      return image;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public void clearAllCache(){
    for(Map.Entry<String, Bitmap> e : mAssetImageCaches.entrySet()) {
      Bitmap bitmap = mAssetImageCaches.get(e.getKey());
      bitmap.recycle();
    }
    mAssetImageCaches.clear();
  }

  //デストラクタ
  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    clearAllCache();
  }
}
