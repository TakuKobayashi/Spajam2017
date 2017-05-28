package kobayashi.taku.com.egaonotatsuzin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class SoundCircle{
  private Bitmap mImage;
  private PointF mPosition = new PointF();

  public SoundCircle(){
    mImage = ImageCacheManager.getInstance(ImageCacheManager.class).getImageFromAsset("circle/pink_btn.png");
  }

  public void setPosition(float x, float y){
    mPosition.x = x;
    mPosition.y = y;
  }

  public void moveTo(PointF point){
    mPosition.x = mPosition.x + point.x;
    mPosition.y = mPosition.y + point.y;
  }

  public RectF getDst(){
    return new RectF(mPosition.x - ((float) mImage.getWidth() / 2), mPosition.y - ((float) mImage.getHeight() / 2), mPosition.x + ((float) mImage.getWidth() / 2), mPosition.y + ((float) mImage.getHeight() / 2));
  }

  public boolean checkVisible(){
    return mPosition.x > -(float)(mImage.getWidth() / 2);
  }

  public void render(Canvas canvas){
    canvas.drawBitmap(mImage, new Rect(0,0, mImage.getWidth(), mImage.getHeight()), getDst(), null);
  }

  public void release(){
    if(mImage != null){
      mImage.recycle();
      mImage = null;
    }
  }
}