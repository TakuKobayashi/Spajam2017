//
// Created by taku on 2017/11/09.
//

#ifndef PROJ_ANDROID_STUDIO_PLAYINGSCENE_H
#define PROJ_ANDROID_STUDIO_PLAYINGSCENE_H

#include "cocos2d.h"
#include "PlayListScene.h"

class PlayingScene : public cocos2d::Scene{
public:
    static cocos2d::Scene* createScene(int index);
    virtual bool init();
    CREATE_FUNC(PlayingScene);
};


#endif //PROJ_ANDROID_STUDIO_PLAYINGSCENE_H
