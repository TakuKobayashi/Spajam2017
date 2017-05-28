package kobayashi.taku.com.egaonotatsuzin;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
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
  private ArrayList<SoundCircle> mSoundCircleList = new ArrayList<SoundCircle>();
  private RectF mHitRect = new RectF();

  public SoundGameView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public void setHitRect(RectF rect){
    mHitRect = rect;
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
    ArrayList<SoundCircle> willRemoveCircle = new ArrayList<SoundCircle>();

    for(int i = 0;i < mSoundCircleList.size();++i){
      SoundCircle soundCircle = mSoundCircleList.get(i);
      soundCircle.moveTo(new PointF(-3f, 0));
      soundCircle.render(bitmapCanvas);
      if(!soundCircle.checkVisible()){
        willRemoveCircle.add(soundCircle);
      }
    }
    canvas.drawBitmap(mRenderBaseImage, null, new Rect(0, 0, mRenderBaseImage.getWidth(), mRenderBaseImage.getHeight()), null);

    for(int i = 0;i < willRemoveCircle.size();++i){
      SoundCircle soundCircle = willRemoveCircle.get(i);
      mSoundCircleList.remove(soundCircle);
    }
    this.invalidate();
  }

  public void generateCircle(){
    SoundCircle soundCircle = new SoundCircle();
    soundCircle.setPosition(this.getWidth(), (float) this.getHeight() / 2);
    mSoundCircleList.add(soundCircle);
  }

  public boolean hit(){
    boolean isHit = false;
    for(int i = 0;i < mSoundCircleList.size();++i){
      RectF dst = mSoundCircleList.get(i).getDst();
      if(mHitRect.contains(dst)){
        isHit = true;
      }
    }
    return isHit;
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
    for(int i = 0;i < mSoundCircleList.size();++i){
      mSoundCircleList.get(i).release();
    }
    mSoundCircleList.clear();
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