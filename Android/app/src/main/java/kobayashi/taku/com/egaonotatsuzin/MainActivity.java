package kobayashi.taku.com.egaonotatsuzin;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    private static int REQUEST_CODE = 1;
    private CameraSource mCameraSource;
    private Handler mDebugParameterHandler;
    private Handler mGenerateCircleHandler;
    private TextView mParamsText;
    private SoundGameView mSoundGameView;
    private Runnable mCircleRunnable;
    private float mPrevSmileValue = -1f;

    private SoundGameController mSoundGameController;
    private SoundPool mSoundPool;
    private int taikoSeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mParamsText = (TextView) findViewById(R.id.smileValueText);

        setupSoundEffects();
        setupGameSound();

        ImageView smileIconView = (ImageView) findViewById(R.id.smileIconImage);
        Bitmap smileImage = ImageCacheManager.getInstance(ImageCacheManager.class).getImageFromAsset("images/smile_icon.png");
        smileIconView.setImageBitmap(smileImage);

        ImageView targetImageView = (ImageView) findViewById(R.id.targetImage);
        Bitmap targetImage = ImageCacheManager.getInstance(ImageCacheManager.class).getImageFromAsset("images/target.png");
        targetImageView.setImageBitmap(targetImage);

        mDebugParameterHandler = new Handler(){
            //メッセージ受信
            public void handleMessage(Message message) {
                //メッセージの表示
                mParamsText.setText((String) message.obj.toString());
            };
        };
        mGenerateCircleHandler = new Handler();

        mSoundGameView = (SoundGameView) findViewById(R.id.soundGameView);

        if(Util.hasSelfPermission(this, Manifest.permission.CAMERA)) {
            setupCamera();
        }
        Util.requestPermissions(this, REQUEST_CODE);
    }

    private void setupGameSound(){
        mSoundGameController = new SoundGameController(this, "wonder_music_12");
        /*
        mSoundGameController.setBeatCallback(new SoundGameController.BeatCallback() {
            @Override
            public void onBeat() {
                mSoundGameView.generateCircle();
            }
        });
        */
    }

    private void setupSoundEffects(){
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                // USAGE_MEDIA
                // USAGE_GAME
                .setUsage(AudioAttributes.USAGE_GAME)
                // CONTENT_TYPE_MUSIC
                // CONTENT_TYPE_SPEECH, etc.
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();

        mSoundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                // ストリーム数に応じて
                .setMaxStreams(1)
                .build();

        taikoSeId = mSoundPool.load(this, R.raw.taiko, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(REQUEST_CODE == requestCode){
            int cameraPermissionIndex = -1;
            for(int i = 0;i < permissions.length;++i){
                if(permissions[i].equals(Manifest.permission.CAMERA)){
                    cameraPermissionIndex = i;
                    break;
                }
            }
            if(cameraPermissionIndex > 0 && grantResults[cameraPermissionIndex] == PackageManager.PERMISSION_GRANTED) {
                setupCamera();
            }
        }
    }

    private void setupCamera() {
        Context context = this.getApplicationContext();
        MultiProcessor.Builder multiprocessorBuilder = new MultiProcessor.Builder<>(new MultiProcessor.Factory<Face>() {
            @Override
            public Tracker<Face> create(Face face) {
                return new Tracker<Face>() {
                    @Override
                    public void onNewItem(int faceId, Face item) {
                        Log.d(Config.TAG, "new");
                    }

                    @Override
                    public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
                        SparseArray<Face> faces = detectionResults.getDetectedItems();
                        float maxSmilingScore = Float.MIN_VALUE;
                        for(int i = 0;i < faces.size();++i){
                            if(faces.get(i) == null) continue;
                            Message msg = Message.obtain();
                            msg.obj = "smile:" + faces.get(i).getIsSmilingProbability();
                            mDebugParameterHandler.sendMessage(msg);
                            if(maxSmilingScore < faces.get(i).getIsSmilingProbability()){
                                maxSmilingScore = faces.get(i).getIsSmilingProbability();
                            }
                        }
                        if(mPrevSmileValue < ApplicationParameter.SMILE_THREATHOLD && ApplicationParameter.SMILE_THREATHOLD < maxSmilingScore){
                            Log.d(Config.TAG, "smile!!");
                            executeSmile();
                        }
                        mPrevSmileValue = maxSmilingScore;
                    }

                    @Override
                    public void onMissing(FaceDetector.Detections<Face> detectionResults) {
                        mPrevSmileValue = -1f;
                        Log.d(Config.TAG, "missing");
                    }

                    @Override
                    public void onDone() {
                        Log.d(Config.TAG, "done");
                    }
                };
            }
        });

        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setTrackingEnabled(false)
                .build();
        detector.setProcessor(multiprocessorBuilder.build());

        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();
    }

    private void executeSmile(){
        // one.wav の再生
        // play(ロードしたID, 左音量, 右音量, 優先度, ループ,再生速度)
        //beatRequest();
        mSoundPool.play(taikoSeId, 1.0f, 1.0f, 0, 0, 1);
        if(mSoundGameView.hit()){
            Log.d(Config.TAG, "hit");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupLooper();

        if(mSoundGameController != null){
            mSoundGameController.start();
        }
        try {
            if(mCameraSource != null){
                mCameraSource.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void beatRequest(){
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.addCallback(new HttpRequest.ResponseCallback() {
            @Override
            public void onSuccess(String url, String body) {
                Log.d(Config.TAG, "url:" + url + " body:" + body);
            }
        });
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("beat", 1);
        httpRequest.setParams(params);
        httpRequest.execute(Config.ROOT_URL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(Config.TAG, "pause");
        if(mCameraSource != null){
            mCameraSource.stop();
        }
        if(mSoundGameController != null){
            mSoundGameController.pause();
        }
        mGenerateCircleHandler.removeCallbacks(mCircleRunnable);
    }

    private void setupLooper(){
        mCircleRunnable = new Runnable() {
            public void run() {
                mSoundGameView.generateCircle();
                mGenerateCircleHandler.removeCallbacks(mCircleRunnable);
                mGenerateCircleHandler.postDelayed(mCircleRunnable, 3000);
            }
        };
        mGenerateCircleHandler.postDelayed(mCircleRunnable, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCameraSource.release();
        mSoundGameView.releaseAllImage();
        if(mSoundGameController != null){
            mSoundGameController.release();
        }
        ImageCacheManager.getInstance(ImageCacheManager.class).clearAllCache();
        Util.releaseImageView((ImageView) findViewById(R.id.smileIconImage));
        Util.releaseImageView((ImageView) findViewById(R.id.targetImage));
        mSoundPool.release();
    }
}
