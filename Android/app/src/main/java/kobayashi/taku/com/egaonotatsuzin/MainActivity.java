package kobayashi.taku.com.egaonotatsuzin;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

public class MainActivity extends Activity {
    private static int REQUEST_CODE = 1;
    private CameraSource mCameraSource;
    private Handler mHandler;
    private TextView mParamsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mParamsText = (TextView) findViewById(R.id.smileValueText);
        mHandler = new Handler(){
            //メッセージ受信
            public void handleMessage(Message message) {
                //メッセージの表示
                mParamsText.setText((String) message.obj.toString());
            };
        };

        if(Util.hasSelfPermission(this, Manifest.permission.CAMERA)) {
            setupCamera();
        }
        Util.requestPermissions(this, REQUEST_CODE);
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
                        Log.d(Config.TAG, "faces:" + faces.size());
                        for(int i = 0;i < faces.size();++i){
                            if(faces.get(i) == null) continue;
                            Message msg = Message.obtain();
                            msg.obj = "smile:" + faces.get(i).getIsSmilingProbability();
                            mHandler.sendMessage(msg);
                            Log.d(Config.TAG, "smile:" + faces.get(i).getIsSmilingProbability());
                        }
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

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if(mCameraSource != null){
                mCameraSource.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(Config.TAG, "pause");
        if(mCameraSource != null){
            mCameraSource.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCameraSource.release();
    }
}
