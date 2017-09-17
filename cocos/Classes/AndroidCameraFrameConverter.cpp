#if (CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID)
#include "AndroidCameraFrameConverter.h"
#include "platform/android/jni/JniHelper.h"
#include <jni.h>

USING_NS_CC;

#define CLASS_NAME "org/cocos2dx/cpp/AppActivity"

extern "C" {
    JNIEXPORT jintArray JNICALL Java_org_cocos2dx_cpp_AppActivity_decodeYUV420SP(JNIEnv *env, jobject thiz, jbyteArray yuv420sp, jint width, jint height) {
        jbyte *yuv420 = env->GetByteArrayElements(yuv420sp, 0);
        int frameSize = width * height;
        jintArray r = env->NewIntArray(frameSize);
        jint *narr = env->GetIntArrayElements(r, 0);
        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; ++i, ++yp) {
                int y = (0xff & ((int) yuv420[yp])) - 16;
                if (y < 0) y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420[uvp++]) - 128;
                    u = (0xff & yuv420[uvp++]) - 128;
                }

                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                if (r < 0) r = 0; else if (r > 262143) r = 262143;
                if (g < 0) g = 0; else if (g > 262143) g = 262143;
                if (b < 0) b = 0; else if (b > 262143) b = 262143;

                narr[yp] =
                        0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }
        }
        env->ReleaseByteArrayElements(yuv420sp, yuv420, 0);
        env->ReleaseIntArrayElements(r, narr, 0);
        return r;
    }
};

#endif