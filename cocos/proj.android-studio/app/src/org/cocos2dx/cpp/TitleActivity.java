package org.cocos2dx.cpp;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import net.taptappun.taku.kobayashi.R;

import org.cocos2dx.lib.Cocos2dxActivity;

import java.io.IOException;

public class TitleActivity extends Activity {
    private static final int REQUEST_CODE_CAMERA_PERMISSION = 1;
    private static int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.title_view);

        Util.requestPermissions(this, REQUEST_CODE_CAMERA_PERMISSION);

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
                    Intent intent = new Intent(TitleActivity.this, AppActivity.class);
                    finish();
                    startActivity(intent);
                }
                return true;
            }
        });
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Util.releaseImageView((ImageView) findViewById(R.id.login_button));
    }
}
