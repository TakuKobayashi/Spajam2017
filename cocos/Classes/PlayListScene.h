#ifndef PROJ_ANDROID_STUDIO_PLAYLISTSCENE_H
#define PROJ_ANDROID_STUDIO_PLAYLISTSCENE_H

#include "cocos2d.h"
#include "ui/CocosGUI.h"
#include "PlayingScene.h"

class PlayListScene : public cocos2d::Scene{
private:
    cocos2d::ui::Layout* playlistCellContainer(int position);

public:
    static cocos2d::Scene* createScene();

    virtual bool init();
    // implement the "static create()" method manually
    CREATE_FUNC(PlayListScene);
};


#endif //PROJ_ANDROID_STUDIO_PLAYLISTSCENE_H
