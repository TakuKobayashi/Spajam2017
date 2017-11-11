#include "PlayingScene.h"

USING_NS_CC;

Scene* PlayingScene::createScene(int index)
{
    return PlayingScene::create();
}

bool PlayingScene::init()
{
    if ( !Scene::init() )
    {
        return false;
    }
    ui::LoadingBar* mScoreBar;

    auto visibleSize = Director::getInstance()->getVisibleSize();
    Vec2 origin = Director::getInstance()->getVisibleOrigin();
    log("width %f, height %f", visibleSize.width, visibleSize.height);

//    auto webView = cocos2d::experimental::ui::WebView::create();
//    webView->loadURL("https://www.yahoo.co.jp");

    // ポジションとサイズを調整
//    Size screen = Director::getInstance()->getOpenGLView()->getDesignResolutionSize();
//    webView->setContentSize(Size(screen.width, screen.height));
//    webView->setPosition(Vec2(screen.width / 2, screen.height/2));
//    this->addChild(webView);

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

    auto scoreBarBase = ui::ImageView::create("images/ui/frame.png");
    scoreBarBase->setScale(baseScale);
    Size scoreBarBaseBoxSize = scoreBarBase->getBoundingBox().size;
    scoreBarBase->setPosition(Vec2(origin.x + visibleSize.width - scoreBarBaseBoxSize.width / 2, origin.y + visibleSize.height - scoreBarBaseBoxSize.height / 2));
    this->addChild(scoreBarBase);

    Vec2 scoreBarPadding = Vec2(1 * baseScale, 1 * baseScale);
    mScoreBar = ui::LoadingBar::create("images/ui/gauge.png");
    mScoreBar->setScale(baseScale);
    Size scoreBarBoxSize = mScoreBar->getBoundingBox().size;
    mScoreBar->setPosition(Vec2(origin.x + visibleSize.width - (scoreBarBoxSize.width / 2) - scoreBarPadding.x,origin.y + visibleSize.height - (scoreBarBoxSize.height / 2) - scoreBarPadding.y));
    mScoreBar->setPercent(50);
    this->addChild(mScoreBar);

    this->scheduleUpdate();
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

void PlayingScene::update(float dt){
    mTime += dt;
    log(mTime);
}