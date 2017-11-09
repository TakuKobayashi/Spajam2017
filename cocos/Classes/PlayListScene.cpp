//
// Created by taku on 2017/11/09.
//

#include <ui/UIListView.h>
#include "PlayListScene.h"

USING_NS_CC;

Scene* PlayListScene::createScene()
{
    return PlayListScene::create();
}

// on "init" you need to initialize your instance
bool PlayListScene::init()
{
    //////////////////////////////
    // 1. super init first
    if ( !Scene::init() )
    {
        return false;
    }
    
    auto visibleSize = Director::getInstance()->getVisibleSize();
    Vec2 origin = Director::getInstance()->getVisibleOrigin();

    auto playlistView = ui::ListView::create();
    playlistView->setClippingEnabled(false);
    playlistView->setDirection(ui::ListView::Direction::VERTICAL);
    playlistView->setPosition(Vec2::ZERO);
    for(int i = 0;i < 10;++i){
        playlistView->pushBackCustomItem(playlistCellContainer());
    }
    playlistView->setPosition(Vec2(origin.x + playlistView->getContentSize().width / 2, origin.y + visibleSize.height));
    this->addChild(playlistView);

    return true;
}

ui::Layout* PlayListScene::playlistCellContainer(){
    auto visibleSize = Director::getInstance()->getVisibleSize();
    Vec2 origin = Director::getInstance()->getVisibleOrigin();

    ui::Layout* celllayout = ui::Layout::create();

    ui::Text* soundTitleLabel = ui::Text::create("hogehoge", "fonts/arial.ttf", 16);
    soundTitleLabel->setColor(Color3B(255,0,0));
    soundTitleLabel->setPosition(Vec2(soundTitleLabel->getContentSize().width / 2, soundTitleLabel->getContentSize().height / 2));
    celllayout->addChild(soundTitleLabel);
    celllayout->setContentSize(Size(visibleSize.width, soundTitleLabel->getContentSize().height));
    return celllayout;
}
