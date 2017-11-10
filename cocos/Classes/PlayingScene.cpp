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

    auto systemButtonListener = EventListenerKeyboard::create();
    systemButtonListener->onKeyReleased = [](cocos2d::EventKeyboard::KeyCode keyCode, cocos2d::Event *event)
    {
        if (keyCode == EventKeyboard::KeyCode::KEY_BACK)
        {
            Director::getInstance()->replaceScene(PlayListScene::createScene());
        }
    };
    this->getEventDispatcher()->addEventListenerWithSceneGraphPriority(systemButtonListener, this);
    return true;
}