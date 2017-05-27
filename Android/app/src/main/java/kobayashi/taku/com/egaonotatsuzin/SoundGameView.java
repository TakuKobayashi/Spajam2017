package kobayashi.taku.com.egaonotatsuzin;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class SoundGameView extends View {

  private Bitmap mClearImage = null;
  private Bitmap mRenderBaseImage = null;

  public SoundGameView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if(mRenderBaseImage != null){
      mRenderBaseImage.recycle();
      mRenderBaseImage = null;
    }
    mRenderBaseImage = mClearImage.copy(Bitmap.Config.ARGB_8888, true);
    Canvas bitmapCanvas = new Canvas(mRenderBaseImage);
    canvas.drawBitmap(mRenderBaseImage, null, new Rect(0, 0, mRenderBaseImage.getWidth(), mRenderBaseImage.getHeight()), null);
    this.invalidate();
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    Log.d(Config.TAG, "w:" + w + " h:" + h + "ow:" + oldw + "oh:" + oldh);
    if(mClearImage != null){
      mClearImage.recycle();
      mClearImage = null;
    }
    mClearImage = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
  }

  public void releaseAllImage(){
    if(mRenderBaseImage != null){
      mRenderBaseImage.recycle();
      mRenderBaseImage = null;
    }
    if(mClearImage != null){
      mClearImage.recycle();
      mClearImage = null;
    }
  }
}