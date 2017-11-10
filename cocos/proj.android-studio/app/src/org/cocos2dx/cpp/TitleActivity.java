package org.cocos2dx.cpp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import net.taptappun.taku.kobayashi.R;

import org.cocos2dx.lib.Cocos2dxActivity;
import org.cocos2dx.lib.Cocos2dxLocalStorage;

import java.io.IOException;

public class TitleActivity extends Activity {
    private static final int REQUEST_CODE_CAMERA_PERMISSION = 1;
    private static int REQUEST_CODE = 1;
    private WebView mLoginWebview;
    private boolean mIsStartLogin = false;

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
        loginButton.setImageResource(R.mipmap.spotify_login_button);
        loginButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    ImageView pressImage = (ImageView) v;
                    Util.releaseImageView(pressImage);
                    pressImage.setImageResource(R.mipmap.spotify_login_button_press);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    ImageView pressImage = (ImageView) v;
                    Util.releaseImageView(pressImage);
                    pressImage.setImageResource(R.mipmap.spotify_login_button);
                    startPlayList();
                    //showLoginWebView();
                }
                return true;
            }
        });
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
        if(keyCode== KeyEvent.KEYCODE_BACK && mLoginWebview.getVisibility() == View.VISIBLE){
            Util.releaseWebView(mLoginWebview);
            mLoginWebview.setVisibility(View.INVISIBLE);
            mIsStartLogin = false;
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mIsStartLogin){
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Util.releaseImageView((ImageView) findViewById(R.id.login_button));
        Util.releaseWebView(mLoginWebview);
    }
}
