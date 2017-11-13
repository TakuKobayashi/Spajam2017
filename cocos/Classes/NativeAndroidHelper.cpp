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

void NativeAndroidHelper::releaseCamera()
{
    JniMethodInfo methodInfo;
    if (!JniHelper::getStaticMethodInfo(methodInfo, CLASS_NAME, "releaseCamera", "()V")) {
        return;
    }
    methodInfo.env->CallStaticVoidMethod(methodInfo.classID, methodInfo.methodID);
    methodInfo.env->DeleteLocalRef(methodInfo.classID);
}

void NativeAndroidHelper::beat()
{
    log("beat");
}
#endif