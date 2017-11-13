package org.cocos2dx.cpp;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.os.SystemClock;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;

import java.nio.ByteBuffer;

public abstract class DetectorThread implements Runnable{
    protected Detector<?> mDetector;
    protected long mRealtime;
    protected final Object mLock;
    protected boolean mActive = false;
    protected long mTimestamp;
    protected int mDetectorId;
    protected ByteBuffer mDetectionBuffer;
    protected Thread mThread;

    DetectorThread(Detector<?> detector) {
        super();
        this.mRealtime = SystemClock.elapsedRealtime();
        this.mLock = new Object();
        this.mDetectorId = 0;
        this.mDetector = detector;
    }

    public void release() {
        this.mDetector.release();
        this.mDetector = null;
    }

    public final void setActive(boolean active) {
        synchronized(this.mLock) {
            this.mActive = active;
            this.mLock.notifyAll();
        }
    }

    public void startDetect(){
        mThread = new Thread(this);
        mThread.start();
        this.setActive(true);
    }

    public void stopDetect(){
        synchronized(this.mLock) {
            this.setActive(false);
            mThread = null;
        }
    }

    public Frame generateFrame(ByteBuffer buffer, int width, int height, int orientation){
        Frame.Builder builder = new Frame.Builder();
        builder.setImageData(buffer, width, height, ImageFormat.NV21);
        builder.setId(this.mDetectorId);
        builder.setTimestampMillis(this.mTimestamp);
        builder.setRotation(orientation);
        return builder.build();
    }

    public Frame generateFrame(Bitmap image, int orientation){
        Frame.Builder builder = new Frame.Builder();
        builder.setBitmap(image);
        builder.setId(this.mDetectorId);
        builder.setTimestampMillis(this.mTimestamp);
        builder.setRotation(orientation);
        return builder.build();
    }
}
