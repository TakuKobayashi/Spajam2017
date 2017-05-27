package kobayashi.taku.com.egaonotatsuzin;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;

public class SoundGameController{
  private MediaPlayer mSoundPlayer;

  public SoundGameController(Context context, String filename) {
    AssetFileDescriptor afd = null;
    try {
      afd = context.getAssets().openFd(filename);
      mSoundPlayer = new MediaPlayer();
      mSoundPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
      mSoundPlayer.prepare();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void start(){
    mSoundPlayer.start();
  }

  public void release(){
    mSoundPlayer.release();
  }
}