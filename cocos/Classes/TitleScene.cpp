#include "TitleScene.h"
#include "SimpleAudioEngine.h"

USING_NS_CC;

Scene* TitleScene::createScene()
{
    return TitleScene::create();
}

// Print useful error message instead of segfaulting when files are not there.
static void problemLoading(const char* filename)
{
    printf("Error while loading: %s\n", filename);
    printf("Depending on how you compiled you might have to add 'Resources/' in front of filenames in HelloWorldScene.cpp\n");
}

// on "init" you need to initialize your instance
bool TitleScene::init()
{
    //////////////////////////////
    // 1. super init first
    if ( !Scene::init() )
    {
        return false;
    }

    auto visibleSize = Director::getInstance()->getVisibleSize();
    Vec2 origin = Director::getInstance()->getVisibleOrigin();

    auto start_button = Sprite::create("images/ui/spotify_login_button.png");
    auto start_button_menu = MenuItemSprite::create(start_button, start_button, [this](Ref* sender){
        //シーン移動
        Director::getInstance()->replaceScene(PlayListScene::createScene());
    });
    auto playlistSceneMenu = Menu::create(start_button_menu, NULL);
    playlistSceneMenu->setPosition(Vec2(visibleSize.width/2 + origin.x, origin.y + start_button->getContentSize().height/2));
    this->addChild(playlistSceneMenu, 1);


    // android back press event
    auto systemButtonListener = EventListenerKeyboard::create();
    systemButtonListener->onKeyReleased = [](cocos2d::EventKeyboard::KeyCode keyCode, cocos2d::Event *event)
    {
        if (keyCode == EventKeyboard::KeyCode::KEY_BACK)
        {
            Director::getInstance()->end();
        }
    };
    this->getEventDispatcher()->addEventListenerWithSceneGraphPriority(systemButtonListener, this);
    return true;
}


void TitleScene::menuCloseCallback(Ref* pSender)
{
    //Close the cocos2d-x game scene and quit the application
    Director::getInstance()->end();

    #if (CC_TARGET_PLATFORM == CC_PLATFORM_IOS)
    exit(0);
#endif

    /*To navigate back to native iOS screen(if present) without quitting the application  ,do not use Director::getInstance()->end() and exit(0) as given above,instead trigger a custom event created in RootViewController.mm as below*/

    //EventCustom customEndEvent("game_scene_close_event");
    //_eventDispatcher->dispatchEvent(&customEndEvent);


}
