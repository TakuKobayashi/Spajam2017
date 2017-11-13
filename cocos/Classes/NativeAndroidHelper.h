//
// Created by TakuKobayshi on 西暦17/11/13.
//

#ifndef PROJ_ANDROID_STUDIO_NATIVEANDROIDHELPER_H
#define PROJ_ANDROID_STUDIO_NATIVEANDROIDHELPER_H

#include "cocos2d.h"
#include "platform/android/jni/JniHelper.h"
#include <jni.h>

class NativeAndroidHelper {
public:
    static void beat();
    static void startCamera();
    static void releaseCamera();
};


#endif //PROJ_ANDROID_STUDIO_NATIVEANDROIDHELPER_H
