
#include <jni.h>
#include <android/log.h>
#include "cocos2d.h"
#include "../../../../Classes/NativeAndroidHelper.h"

using namespace cocos2d;

#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

extern "C" {
JNIEXPORT void JNICALL Java_org_cocos2dx_cpp_AppActivity_callSmile(JNIEnv *env, jobject obj, jfloat score){
    NativeAndroidHelper::smile(score);
}

JNIEXPORT void JNICALL Java_org_cocos2dx_cpp_AppActivity_callDetect(JNIEnv *env, jobject obj){
    NativeAndroidHelper::detect();
}

JNIEXPORT void JNICALL Java_org_cocos2dx_cpp_AppActivity_callGone(JNIEnv *env, jobject obj){
    NativeAndroidHelper::gone();
}
JNIEXPORT void JNICALL Java_org_cocos2dx_cpp_AppActivity_callFrame(JNIEnv *env, jobject obj, jlong millisecond){
    NativeAndroidHelper::frame(millisecond);
}
}