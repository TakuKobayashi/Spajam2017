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

    log("playlist");
    auto visibleSize = Director::getInstance()->getVisibleSize();
    Vec2 origin = Director::getInstance()->getVisibleOrigin();

    auto playlistView = ui::ListView::create();
    playlistView->setClippingEnabled(false);
    playlistView->setDirection(ui::ListView::Direction::VERTICAL);
    for(int i = 0;i < 10;++i){
        playlistView->pushBackCustomItem(playlistCellContainer());
    }
    this->addChild(playlistView);
    return true;
}

ui::Layout* PlayListScene::playlistCellContainer(){
    log("layout");
    auto visibleSize = Director::getInstance()->getVisibleSize();
    Vec2 origin = Director::getInstance()->getVisibleOrigin();

    ui::Layout* celllayout = ui::Layout::create();
    celllayout->setContentSize(Size(visibleSize.width, 150));
/*
    ui::Text* soundTitleLabel = ui::Text::create("hogehoge", "fonts/arial.ttf", 16);
    soundTitleLabel->setColor(Color3B(255,0,0));
    soundTitleLabel->setPosition(Vec2(soundTitleLabel->getContentSize().width / 2, celllayout->getContentSize().height - soundTitleLabel->getContentSize().height / 2));
    celllayout->addChild(soundTitleLabel);
    */
    auto start_button = Sprite::create("images/ui/spotify_login_button.png");
    celllayout->addChild(soundTitleLabel);

    return celllayout;
    /*
    Layout* layout = ui::Layout::create();
    layout->setContentSize(Size(280, 150));
    Size backgroundSize = background->getContentSize();
    layout->setPosition(Vec2((widgetSize.width - backgroundSize.width) / 2.0f +
                             (backgroundSize.width - layout->getContentSize().width) / 2.0f,
                             (widgetSize.height - backgroundSize.height) / 2.0f +
                             (backgroundSize.height - layout->getContentSize().height) / 2.0f));
    _uiLayer->addChild(layout);

    Button* button = Button::create("cocosui/animationbuttonnormal.png", "cocosui/animationbuttonpressed.png");
    button->setPosition(Vec2(button->getContentSize().width / 2.0f,
                             layout->getContentSize().height - button->getContentSize().height / 2.0f));
    layout->addChild(button);

    Button* titleButton = Button::create("cocosui/backtotopnormal.png", "cocosui/backtotoppressed.png");
    titleButton->setTitleText("Title Button");
    titleButton->setPosition(Vec2(layout->getContentSize().width / 2.0f, layout->getContentSize().height / 2.0f));
    layout->addChild(titleButton);

    Button* button_scale9 = Button::create("cocosui/button.png", "cocosui/buttonHighlighted.png");
    button_scale9->setScale9Enabled(true);
    button_scale9->setContentSize(Size(100.0f, button_scale9->getVirtualRendererSize().height));
    button_scale9->setPosition(Vec2(layout->getContentSize().width - button_scale9->getContentSize().width / 2.0f,
                                    button_scale9->getContentSize().height / 2.0f));

    layout->addChild(button_scale9);
     */
}