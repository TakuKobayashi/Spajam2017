//
// Created by TakuKobayshi on 西暦17/11/13.
//

#ifndef PROJ_ANDROID_STUDIO_NATIVEANDROIDHELPER_H
#define PROJ_ANDROID_STUDIO_NATIVEANDROIDHELPER_H

#include "cocos2d.h"
#include "platform/android/jni/JniHelper.h"
#include <jni.h>
#include "PlayingScene.h"

class NativeAndroidHelper {
public:
    static std::string getUserToken();
    static void startSound(std::string spotifyId);
    static void smile(float score);
    static void detect();
    static void gone();
    static void frame(long millisecond);
    static void startCamera();
    static void releaseCamera();
};


#endif //PROJ_ANDROID_STUDIO_NATIVEANDROIDHELPER_H
