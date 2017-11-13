package org.cocos2dx.cpp;

import android.graphics.ImageFormat;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

public class CameraDetectorThread extends DetectorThread{
    private ArrayList<BufferedCallback> mBufferedCallback = new ArrayList<BufferedCallback>();
    private HashMap<byte[], ByteBuffer> mCacheBuffer = new HashMap<byte[], ByteBuffer>();
    private int mPreviewWidth;
    private int mPreviewHeight;
    private int mOrientation;
    // 過去に遡って保持することができるフレーム数
    private static final int STACKABLE_FRAME_SIZE = 2;
    private ArrayDeque<Frame> mFrameQueue = new ArrayDeque<Frame>();

    public CameraDetectorThread(Detector<?> detector){
        super(detector);
    }

    public void previewFrame(byte[] frameBytes, CameraWrapper camera) {
        synchronized(this.mLock) {
            if(this.mDetectionBuffer != null) {
                //バッファを再利用する
                camera.addCallbackBuffer(this.mDetectionBuffer.array());
                this.mDetectionBuffer = null;
            }

            if(!mCacheBuffer.containsKey(frameBytes)) {
                Log.d(Config.TAG, "Skipping frame. Could not find ByteBuffer associated with the image data from the camera.");
            } else {
                this.mTimestamp = SystemClock.elapsedRealtime() - this.mRealtime;
                ++this.mDetectorId;
                this.mDetectionBuffer = mCacheBuffer.get(frameBytes);
                this.mLock.notifyAll();
            }
        }
    }

    public void setPreviewSize(int width, int height){
        mPreviewWidth = width;
        mPreviewHeight = height;
    }

    public Size getPreviewSize(){
        return new Size(mPreviewWidth, mPreviewHeight);
    }

    public void setDisplayOrientation(int orientation){
        mOrientation = orientation;
    }

    public void addBufferedCallback(BufferedCallback callback){
        mBufferedCallback.add(callback);
    }

    public void removeBufferedCallback(BufferedCallback callback){
        mBufferedCallback.remove(callback);
    }

    public final byte[] putBuffer() {
        int bitsPerPixel = ImageFormat.getBitsPerPixel(ImageFormat.NV21);
        byte[] buffer;
        ByteBuffer byteBuffer;
        if((byteBuffer = ByteBuffer.wrap(buffer = new byte[(int)Math.ceil((double)((long)(mPreviewWidth * mPreviewHeight * bitsPerPixel)) / 8.0D) + 1])).hasArray() && byteBuffer.array() == buffer) {
            mCacheBuffer.put(buffer, byteBuffer);
            return buffer;
        } else {
            throw new IllegalStateException("Failed to create valid buffer for camera source.");
        }
    }

    @Override
    public void release(){
        stopDetect();
        super.release();
    }

    @Override
    public void stopDetect(){
        synchronized(this.mLock) {
            this.setActive(false);
            try {
                if(mThread != null){
                    mThread.join();
                }
            } catch (InterruptedException exception) {
                Log.e(Config.TAG, "Frame processing thread interrupted on release.", exception);
            }

            mCacheBuffer.clear();
            mThread = null;
        }
    }

    @Override
    public void run() {
        while(mActive) {
            Frame frame;
            ByteBuffer byteBuffer;
            synchronized(this.mLock) {
                while(this.mActive && this.mDetectionBuffer == null) {
                    try {
                        this.mLock.wait();
                    } catch (InterruptedException exception) {
                        Log.e(Config.TAG, "Frame processing loop terminated.", exception);
                        return;
                    }
                }

                if(!this.mActive) {
                    return;
                }
                frame = generateFrame(this.mDetectionBuffer, mPreviewWidth, mPreviewHeight, mOrientation);
                mFrameQueue.offerFirst(frame);
                if(mFrameQueue.size() > STACKABLE_FRAME_SIZE){
                    for(int i = 0;i < mFrameQueue.size() - STACKABLE_FRAME_SIZE;++i){
                        Frame pollFrame = mFrameQueue.pollLast();
                    }
                }

                byteBuffer = this.mDetectionBuffer;
                this.mDetectionBuffer = null;
            }

            try {
                this.mDetector.receiveFrame(frame);
            } catch (Throwable exception) {
                Log.e(Config.TAG, "Exception thrown from receiver.", exception);
            } finally {
                for(int i = 0;i < mBufferedCallback.size();++i){
                    mBufferedCallback.get(i).onBuffered(byteBuffer.array());
                }
            }
        }
    }

    public ArrayList<Frame> getFrames(){
        return new ArrayList<Frame>(mFrameQueue);
    }

    public interface BufferedCallback{
        public void onBuffered(byte[] buffer);
    }
}
