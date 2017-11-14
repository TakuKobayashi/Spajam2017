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
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import org.cocos2dx.lib.Cocos2dxActivity;

import java.io.IOException;

public class AppActivity extends Cocos2dxActivity {
    private static final int REQUEST_CODE_CAMERA_PERMISSION = 1;

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
        startFaceDetectCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode != REQUEST_CODE_CAMERA_PERMISSION)
            return;
        if(!Util.existConfirmPermissions(this) && gIsCameraActivate){
            startFaceDetectCamera();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!Util.existConfirmPermissions(this) && gIsCameraActivate){
            startFaceDetectCamera();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseFaceDetectCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(gCamera != null){
            gCamera.release();
            gCamera = null;
        }
    }

    //---------------------------------------------------------------------------------------------------------------------------------
    // TODO Management
    private static boolean gIsCameraActivate = false;
    private static Context gApplicationContext;
    private static CameraSource gCamera = null;

    public static void startCamera(){
        Log.d(Config.TAG, "startCamera");
        gIsCameraActivate = true;
        cameraStart();
    }

    private static void startFaceDetectCamera(){
        /*
        if(gCamera == null){
            gCamera = new CameraWrapper();
            Log.d(Config.TAG, "new");
        }
        */

        MultiProcessor.Builder multiprocessorBuilder = new MultiProcessor.Builder<>(new MultiProcessor.Factory<Face>() {
            @Override
            public Tracker<Face> create(Face face) {
                return new Tracker<Face>() {
                    @Override
                    public void onNewItem(int faceId, Face item) {
                        Log.d(Config.TAG, "newItem");
                        callDetect();
                    }

                    @Override
                    public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
                        Log.d(Config.TAG, "update");
                        SparseArray<Face> faces = detectionResults.getDetectedItems();
                        float maxSmilingScore = Float.MIN_VALUE;
                        for(int i = 0;i < faces.size();++i){
                            Face detectFace = faces.get(i);
                            Log.d(Config.TAG, "" + detectFace);
                            if(detectFace == null) continue;
                            Log.d(Config.TAG, "smileScore:" + detectFace.getIsSmilingProbability());
                            if(maxSmilingScore < detectFace.getIsSmilingProbability()){
                                maxSmilingScore = detectFace.getIsSmilingProbability();
                            }
                        }
                        Log.d(Config.TAG, "maxSmile:" + maxSmilingScore);
                        callSmile(Math.max(maxSmilingScore, 0));
                    }

                    @Override
                    public void onMissing(FaceDetector.Detections<Face> detectionResults) {
                        Log.d(Config.TAG, "missing");
                    }

                    @Override
                    public void onDone() {
                        Log.d(Config.TAG, "done");
                        callGone();
                    }
                };
            }
        });

        FaceDetector detector = new FaceDetector.Builder(gApplicationContext)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setTrackingEnabled(true)
                .build();
        detector.setProcessor(multiprocessorBuilder.build());

        gCamera = new CameraSource.Builder(gApplicationContext, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();
/*
        CameraDetectorThread detectorThread = new CameraDetectorThread(detector);
        gCamera.setDetectorThread(detectorThread);
        gCamera.start();
*/
    }

    private native static void callSmile(float score);
    private native static void callDetect();
    private native static void callGone();

    private static void cameraStart(){
        try {
            gCamera.start();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(Config.TAG, "error:" + e.getMessage());
        }
    }

    public static void releaseCamera(){
        Log.d(Config.TAG, "stopCamera");
        gIsCameraActivate = false;
        releaseFaceDetectCamera();
    }

    private static void releaseFaceDetectCamera(){
        if(gCamera != null) {
            gCamera.stop();
        }
    }

    //---------------------------------------------------------------------------------------------------------------------------------
}
