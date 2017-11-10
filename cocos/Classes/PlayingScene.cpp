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
    auto visibleSize = Director::getInstance()->getVisibleSize();
    Vec2 origin = Director::getInstance()->getVisibleOrigin();
    log("width %f, height %f", visibleSize.width, visibleSize.height);

    auto bgImage = ui::ImageView::create("images/ui/bg.png");
    float baseScale = std::max(visibleSize.width / bgImage->getContentSize().width, visibleSize.height / bgImage->getContentSize().height);
    bgImage->setScale(baseScale);

    Size bgImageBounseBoxSize = bgImage->getBoundingBox().size;
    bgImage->setPosition(Vec2(origin.x + bgImageBounseBoxSize.width / 2, origin.y + bgImageBounseBoxSize.height / 2));
    this->addChild(bgImage);

    float laneHeight = 11 * baseScale;

    auto targetImage = ui::ImageView::create("images/ui/target.png");
    targetImage->setScale(baseScale);
    Size targetImageBoxSize = targetImage->getBoundingBox().size;
    targetImage->setPosition(Vec2(origin.x + targetImageBoxSize.width / 2, laneHeight + origin.y + targetImageBoxSize.height / 2));
    this->addChild(targetImage);

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