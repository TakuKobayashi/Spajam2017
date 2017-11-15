#if (CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID)
#include "NativeAndroidHelper.h"
USING_NS_CC;

#define CLASS_NAME "org/cocos2dx/cpp/AppActivity"

void NativeAndroidHelper::startCamera()
{
    JniMethodInfo methodInfo;
    if (!JniHelper::getStaticMethodInfo(methodInfo, CLASS_NAME, "startCamera", "()V")) {
        return;
    }
    methodInfo.env->CallStaticVoidMethod(methodInfo.classID, methodInfo.methodID);
    methodInfo.env->DeleteLocalRef(methodInfo.classID);
}

void NativeAndroidHelper::startSound(std::string spotifyid)
{
    JniMethodInfo methodInfo;
    if (!JniHelper::getStaticMethodInfo(methodInfo, CLASS_NAME, "playSound", "(Ljava/lang/String;)V")) {
        return;
    }
    jstring spotifyidjstr = methodInfo.env->NewStringUTF(spotifyid.c_str());
    methodInfo.env->CallStaticVoidMethod(methodInfo.classID, methodInfo.methodID, spotifyidjstr);
    methodInfo.env->DeleteLocalRef(methodInfo.classID);
}

void NativeAndroidHelper::releaseCamera()
{
    JniMethodInfo methodInfo;
    if (!JniHelper::getStaticMethodInfo(methodInfo, CLASS_NAME, "releaseCamera", "()V")) {
        return;
    }
    methodInfo.env->CallStaticVoidMethod(methodInfo.classID, methodInfo.methodID);
    methodInfo.env->DeleteLocalRef(methodInfo.classID);
}

std::string NativeAndroidHelper::getUserToken()
{
    std::string ret;
    JniMethodInfo methodInfo;
    if (!JniHelper::getStaticMethodInfo(methodInfo, CLASS_NAME, "getUserToken", "()Ljava/lang/String;")) {
        log("call");
        jobject objResult = methodInfo.env->CallStaticObjectMethod(methodInfo.classID, methodInfo.methodID);
        ret = cocos2d::JniHelper::jstring2string((jstring) objResult); // jstringをstd::stringに変換
        methodInfo.env->DeleteLocalRef(methodInfo.classID);
    }
    log("result:%s", ret.c_str());
    return ret;
}

void NativeAndroidHelper::smile(float score)
{
    PlayingScene::smile(score);
}

void NativeAndroidHelper::detect()
{
    PlayingScene::detect();
}

void NativeAndroidHelper::gone()
{
    PlayingScene::gone();
}

void NativeAndroidHelper::frame(long millisecond)
{
    PlayingScene::frame(millisecond);
}
#endif