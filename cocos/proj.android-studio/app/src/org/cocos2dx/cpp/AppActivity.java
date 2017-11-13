/****************************************************************************
Copyright (c) 2015-2017 Chukong Technologies Inc.
 
http://www.cocos2d-x.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
****************************************************************************/
package org.cocos2dx.cpp;

import android.content.Context;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import org.cocos2dx.lib.Cocos2dxActivity;

import java.io.IOException;

public class AppActivity extends Cocos2dxActivity {
    private static final int REQUEST_CODE_CAMERA_PERMISSION = 1;
    private Camera mCamera;
    private static int REQUEST_CODE = 1;
    private SurfaceView mCameraPreview = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setEnableVirtualButton(false);
        super.onCreate(savedInstanceState);
        // Workaround in https://stackoverflow.com/questions/16283079/re-launch-of-activity-on-home-button-but-only-the-first-time/16447508
        if (!isTaskRoot()) {
            // Android launched another instance of the root activity into an existing task
            //  so just quietly finish and go away, dropping the user back into the activity
            //  at the top of the stack (ie: the last state of this task)
            // Don't need to finish it again since it's finished in super.onCreate .
            return;
        }
        // DO OTHER INITIALIZATION BELOW
        Util.requestPermissions(this, REQUEST_CODE_CAMERA_PERMISSION);
        gApplicationContext = this.getApplicationContext();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode != REQUEST_CODE_CAMERA_PERMISSION)
            return;
        if(!Util.existConfirmPermissions(this) && mIsCameraActivate){
            startFaceDetectCamera();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!Util.existConfirmPermissions(this) && mIsCameraActivate){
            startFaceDetectCamera();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseFaceDetectCamera();
    }

    //---------------------------------------------------------------------------------------------------------------------------------
    // TODO Management
    private static boolean mIsCameraActivate = false;
    private static Context gApplicationContext;
    private static CameraWrapper gCamera = null;

    public static void startCamera(){
        mIsCameraActivate = true;
        startFaceDetectCamera();
    }

    private static void startFaceDetectCamera(){
        if(gCamera == null){
            gCamera = new CameraWrapper();
        }

        MultiProcessor.Builder multiprocessorBuilder = new MultiProcessor.Builder<>(new MultiProcessor.Factory<Face>() {
            @Override
            public Tracker<Face> create(Face face) {
                return new Tracker<Face>() {
                    @Override
                    public void onNewItem(int faceId, Face item) {
                        Log.d(Config.TAG, "newItem");
                    }

                    @Override
                    public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
                        Log.d(Config.TAG, "update");
                    }

                    @Override
                    public void onMissing(FaceDetector.Detections<Face> detectionResults) {
                        Log.d(Config.TAG, "missing");
                    }

                    @Override
                    public void onDone() {
                        Log.d(Config.TAG, "done");
                    }
                };
            }
        });

        FaceDetector detector = new FaceDetector.Builder(gApplicationContext)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setTrackingEnabled(true)
                .build();
        detector.setProcessor(multiprocessorBuilder.build());

        CameraDetectorThread detectorThread = new CameraDetectorThread(detector);
        gCamera.setDetectorThread(detectorThread);
        gCamera.start();
    }

    public static void releaseCamera(){
        mIsCameraActivate = false;
        releaseFaceDetectCamera();
    }

    private static void releaseFaceDetectCamera(){
        if(gCamera != null) {
            gCamera.release();
            gCamera = null;
        }
    }

    //---------------------------------------------------------------------------------------------------------------------------------
}
