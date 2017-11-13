//
// Created by taku on 2017/11/09.
//

#include <ui/UIListView.h>
#include "PlayListScene.h"
#include "PlayingScene.h"

USING_NS_CC;

Scene* PlayListScene::createScene()
{
    return PlayListScene::create();
}

// on "init" you need to initialize your instance
bool PlayListScene::init()
{
    if ( !Scene::init() )
    {
        return false;
    }

    auto visibleSize = Director::getInstance()->getVisibleSize();
    Vec2 origin = Director::getInstance()->getVisibleOrigin();

    auto playlistView = ui::ListView::create();
    // これがないとうまく表示されない
    playlistView->setClippingEnabled(false);
    playlistView->setTouchEnabled(true);
    playlistView->setDirection(ui::ListView::Direction::VERTICAL);
    for(int i = 0;i < 100;++i){
        playlistView->pushBackCustomItem(playlistCellContainer(i));
    }
    // Sizeを指定しないとScrollとして機能しない
    playlistView->setContentSize(visibleSize);
    playlistView->setPosition(Vec2(origin.x, origin.y));
    playlistView->addEventListener([playlistView](Ref *ref, ui::ListView::EventType eventType){
        if (eventType == ui::ListView::EventType::ON_SELECTED_ITEM_END) {
            auto listView = static_cast<ui::ListView*>(ref);
            auto playingScene = PlayingScene::createScene(int(listView->getCurSelectedIndex()));
            Director::getInstance()->replaceScene(playingScene);
        }
    });
    this->addChild(playlistView);
    playlistView->requestDoLayout();

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

ui::Layout* PlayListScene::playlistCellContainer(int position){
    auto visibleSize = Director::getInstance()->getVisibleSize();
    Vec2 origin = Director::getInstance()->getVisibleOrigin();

    ui::Layout* celllayout = ui::Layout::create();
    // 子供もTouchEnableしないとListのタップが有効にならない
    celllayout->setTouchEnabled(true);
    ui::Text* soundTitleLabel = ui::Text::create(StringUtils::format("hogehoge %d", position), "fonts/arial.ttf", 16);
    soundTitleLabel->setColor(Color3B(255,0,0));
    soundTitleLabel->setPosition(Vec2(soundTitleLabel->getContentSize().width / 2, soundTitleLabel->getContentSize().height / 2));
    celllayout->addChild(soundTitleLabel);
    celllayout->setContentSize(Size(visibleSize.width, soundTitleLabel->getContentSize().height));
    return celllayout;
}