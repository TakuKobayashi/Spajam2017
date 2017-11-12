//
// Created by taku on 2017/11/09.
//

#ifndef PROJ_ANDROID_STUDIO_PLAYINGSCENE_H
#define PROJ_ANDROID_STUDIO_PLAYINGSCENE_H

#include "cocos2d.h"
#include "PlayListScene.h"
#include "ui/CocosGUI.h"

class PlayingScene : public cocos2d::Scene{
private:
    cocos2d::ui::LoadingBar mScoreBar;
    float mTime = 0;
    float mBaseScale = 1.0f;
    void fireBeatBall();

public:
    static cocos2d::Scene* createScene(int index);
    virtual bool init();
    void update(float dt) override;
    CREATE_FUNC(PlayingScene);
};


#endif //PROJ_ANDROID_STUDIO_PLAYINGSCENE_H
