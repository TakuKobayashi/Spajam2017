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
  private ArrayList<Double> beatSeconds = new ArrayList<Double>();

  public SoundGameController(Context context, String assetName) {
    AssetFileDescriptor afd = null;
    try {
      afd = context.getAssets().openFd(Config.SOUND_FILE_ROOT + assetName + ".wav");
      mSoundPlayer = new MediaPlayer();
      mSoundPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
      mSoundPlayer.setVolume(0.3f, 0.3f);
      mSoundPlayer.prepare();

      String tempoCsv = Util.loadTextFromAsset(context, Config.TEMPO_CSV_FILE_ROOT + assetName + ".csv");
      String[] beats = tempoCsv.split(",");
      for(int i = 0;i < beats.length;++i){
        beatSeconds.add(Double.parseDouble(beats[i]));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void start(){
    mSoundPlayer.start();
  }

  public void pause(){
    mSoundPlayer.pause();
  }

  public void release(){
    beatSeconds.clear();
    mSoundPlayer.release();
  }
}