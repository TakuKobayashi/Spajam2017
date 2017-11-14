package org.cocos2dx.cpp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.AssetDataSource;
import com.google.android.exoplayer2.upstream.DataSource;

import net.taptappun.taku.kobayashi.R;

import org.cocos2dx.lib.Cocos2dxActivity;
import org.cocos2dx.lib.Cocos2dxLocalStorage;

import java.io.IOException;

public class TitleActivity extends Activity {
    private static final int REQUEST_CODE_CAMERA_PERMISSION = 1;
    private static int REQUEST_CODE = 1;
    private WebView mLoginWebview;
    private boolean mIsStartLogin = false;
    private SimpleExoPlayer mExoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.title_view);

        // ディープリンクから起動されたことを取得する
        Intent receiveIntent = getIntent();
        String action = receiveIntent.getAction();
        if (Intent.ACTION_VIEW.equals(action)){
            // 呼び出されたディープリンクのURLを取得する
            Uri uri = receiveIntent.getData();
            String user_token = uri.getQueryParameter("user_token");
            Cocos2dxLocalStorage.setItem("user_token", user_token);
            startPlayList();
        }

        Util.requestPermissions(this, REQUEST_CODE_CAMERA_PERMISSION);

        mLoginWebview = (WebView) findViewById(R.id.login_webview);
        mLoginWebview.setVisibility(View.INVISIBLE);

        ImageView loginButton = (ImageView) findViewById(R.id.login_button);
        loginButton.setImageBitmap(Util.loadImageFromAsset(TitleActivity.this, "images/ui/spotify_login_button.png"));
        loginButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    ImageView pressImage = (ImageView) v;
                    Util.releaseImageView(pressImage);
                    pressImage.setImageBitmap(Util.loadImageFromAsset(TitleActivity.this, "images/ui/spotify_login_button_press.png"));
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    ImageView pressImage = (ImageView) v;
                    Util.releaseImageView(pressImage);
                    pressImage.setImageBitmap(Util.loadImageFromAsset(TitleActivity.this, "images/ui/spotify_login_button.png"));
                    startPlayList();
                    //showLoginWebView();
                }
                return true;
            }
        });
        initExoPlayer();
    }

    private void initExoPlayer(){
        TextureView bgVideoView = (TextureView) findViewById(R.id.top_background_video_view);

        mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
        mExoPlayer.prepare(loadMediaSource("videos/egao_move.mov"));
        mExoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
        mExoPlayer.setVideoTextureView(bgVideoView);
        mExoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {
                Log.d(Config.TAG, "timeline changed:" + timeline + " " + manifest);
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.d(Config.TAG, "tracks changed:" + trackGroups.length + " " + trackSelections.length);
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                Log.d(Config.TAG, "LoadingChanged:" + isLoading);
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.d(Config.TAG, "PlayerStateChanged:" + playWhenReady + " " + playbackState);
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {
                Log.d(Config.TAG, "RepeatModeChanged:" + repeatMode);
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.d(Config.TAG, "PlayerError:" + error.getMessage());
            }

            @Override
            public void onPositionDiscontinuity() {
                Log.d(Config.TAG, "PositionDiscontinuity");
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                Log.d(Config.TAG, "PlaybackParametersChanged:" + playbackParameters);
            }
        });
        mExoPlayer.addVideoListener(new SimpleExoPlayer.VideoListener() {
            @Override
            public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                Log.d(Config.TAG, "VideoSizeChanged:" + width + " " + height + " " + unappliedRotationDegrees + " " + pixelWidthHeightRatio);
            }

            @Override
            public void onRenderedFirstFrame() {
                Log.d(Config.TAG, "RenderedFirstFrame");
            }
        });
    }

    private MediaSource loadMediaSource(String assetsFileName){
        DataSource.Factory factory = new DataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                return new AssetDataSource(TitleActivity.this);
            }
        };
        MediaSource videoSource = new ExtractorMediaSource(
                Uri.parse("file://android_asset/" + assetsFileName),
                factory,
                new DefaultExtractorsFactory(),
                null,
                new ExtractorMediaSource.EventListener() {
                    @Override
                    public void onLoadError(IOException error) {
                        error.printStackTrace();
                        Log.d(Config.TAG, error.getMessage());
                    }
                }
        );

        return new LoopingMediaSource(videoSource);
    }


    private void showLoginWebView(){
        mIsStartLogin = true;
        mLoginWebview.setVisibility(View.VISIBLE);
        mLoginWebview.getSettings().setJavaScriptEnabled(true);
        mLoginWebview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                setProgressBarIndeterminateVisibility(true);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                setProgressBarIndeterminateVisibility(false);
            }
        });
        mLoginWebview.loadUrl("https://taptappun.net/egaonotatsuzin/authentication/sign_in");
    }

    private void startPlayList(){
        Intent intent = new Intent(TitleActivity.this, AppActivity.class);
        finish();
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(mLoginWebview.getVisibility() == View.VISIBLE) {
                Util.releaseWebView(mLoginWebview);
                mLoginWebview.setVisibility(View.INVISIBLE);
                mIsStartLogin = false;
            }else{
                finish();
            }
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (requestCode != REQUEST_CODE_CAMERA_PERMISSION)
            return;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!mExoPlayer.isPlayingAd()){
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mExoPlayer.setPlayWhenReady(false);
        if(mIsStartLogin){
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
        Util.releaseImageView((ImageView) findViewById(R.id.login_button));
        Util.releaseWebView(mLoginWebview);
    }
}
