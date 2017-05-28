package kobayashi.taku.com.egaonotatsuzin;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class SoundGameController{
  private Thread timerCounterThread;
  private boolean isThreadAlive = false;

  private MediaPlayer mSoundPlayer;
  private ArrayList<Double> beatSeconds = new ArrayList<Double>();
  private Handler mHandler = new Handler();
  private BeatCallback mCallback;
  private int mPrevTimePosition;
  private int mBeatSecondIndex = 0;

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
      Collections.sort(beatSeconds);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void startTimerCounterThread(){
    isThreadAlive = true;
    timerCounterThread = new Thread(new Runnable() {
      @Override
      public void run() {
        while(isThreadAlive){
          int nextBeatPosition = (int)(beatSeconds.get(mBeatSecondIndex) * 1000);
          if(mPrevTimePosition <= nextBeatPosition && nextBeatPosition < mSoundPlayer.getCurrentPosition()){

            mHandler.post(new Runnable() {
              @Override
              public void run() {
                if(mCallback != null){
                  mCallback.onBeat();
                }
              }
            });

            boolean isAvailableNext = false;
            for(int i = 0;i < beatSeconds.size();++i){
              if(mSoundPlayer.getCurrentPosition() < (int)(beatSeconds.get(i) * 1000)){
                mBeatSecondIndex = i;
                isAvailableNext = true;
                break;
              }
            }
            // 無限ループから抜ける
            if(!isAvailableNext){
              isThreadAlive = false;
            }
          }
          mPrevTimePosition = mSoundPlayer.getCurrentPosition();
        }
      }
    });
    timerCounterThread.start();
  }

  public void setBeatCallback(BeatCallback callback){
    mCallback = callback;
  }

  public interface BeatCallback{
    public void onBeat();
  }

  public void start(){
    startTimerCounterThread();
    mSoundPlayer.start();
  }

  public void pause(){
    isThreadAlive = false;
    mSoundPlayer.pause();
  }

  public void release(){
    beatSeconds.clear();
    mSoundPlayer.release();
  }
}