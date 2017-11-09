#include "PlayingScene.h"

USING_NS_CC;

Scene* PlayingScene::createScene(int index)
{
    log("%d", index);
    return PlayingScene::create();
}

bool PlayingScene::init()
{
    if ( !Scene::init() )
    {
        return false;
    }

    return true;
}