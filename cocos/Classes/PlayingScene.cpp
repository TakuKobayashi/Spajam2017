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
    mBaseScale = std::max(visibleSize.width / bgImage->getContentSize().width, visibleSize.height / bgImage->getContentSize().height);
    bgImage->setScale(mBaseScale);

    Size bgImageBounseBoxSize = bgImage->getBoundingBox().size;
    bgImage->setPosition(Vec2(origin.x + bgImageBounseBoxSize.width / 2, origin.y + bgImageBounseBoxSize.height / 2));
    this->addChild(bgImage);

    float laneHeight = 11 * mBaseScale;

    auto targetImage = ui::ImageView::create("images/ui/target.png");
    targetImage->setScale(mBaseScale);
    Size targetImageBoxSize = targetImage->getBoundingBox().size;
    targetImage->setPosition(Vec2(origin.x + targetImageBoxSize.width / 2, laneHeight + origin.y + targetImageBoxSize.height / 2));
    this->addChild(targetImage);

    auto scoreBarBase = ui::ImageView::create("images/ui/frame.png");
    scoreBarBase->setScale(mBaseScale);
    Size scoreBarBaseBoxSize = scoreBarBase->getBoundingBox().size;
    scoreBarBase->setPosition(Vec2(origin.x + visibleSize.width - scoreBarBaseBoxSize.width / 2, origin.y + visibleSize.height - scoreBarBaseBoxSize.height / 2));
    this->addChild(scoreBarBase);

    Vec2 scoreBarPadding = Vec2(1 * mBaseScale, 1 * mBaseScale);
    mScoreBar = ui::LoadingBar::create("images/ui/gauge.png");
    mScoreBar->setScale(mBaseScale);
    Size scoreBarBoxSize = mScoreBar->getBoundingBox().size;
    mScoreBar->setPosition(Vec2(origin.x + visibleSize.width - (scoreBarBoxSize.width / 2) - scoreBarPadding.x,origin.y + visibleSize.height - (scoreBarBoxSize.height / 2) - scoreBarPadding.y));
    mScoreBar->setPercent(50);
    this->addChild(mScoreBar);

    this->scheduleUpdate();
    auto systemButtonListener = EventListenerKeyboard::create();
    systemButtonListener->onKeyReleased = [this](cocos2d::EventKeyboard::KeyCode keyCode, cocos2d::Event *event)
    {
        if (keyCode == EventKeyboard::KeyCode::KEY_BACK)
        {
            this->release();
        }
    };
    this->getEventDispatcher()->addEventListenerWithSceneGraphPriority(systemButtonListener, this);
    fireBeatBall();
#if (CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID)
    NativeAndroidHelper::startCamera();
#endif
    return true;
}

void PlayingScene::fireBeatBall(){
    auto visibleSize = Director::getInstance()->getVisibleSize();
    Vec2 origin = Director::getInstance()->getVisibleOrigin();
    auto beatBall = Sprite::create("images/tamaA.png");
    beatBall->setScale(mBaseScale);
    float laneHeight = 11 * mBaseScale;
    Size beatBallBoxSize = beatBall->getBoundingBox().size;
    beatBall->setPosition(Vec2(origin.x + visibleSize.width + (beatBallBoxSize.width / 2), laneHeight + origin.y + beatBallBoxSize.height / 2));
    this->addChild(beatBall);
    auto moveAction = MoveTo::create(2, Vec2(-(beatBallBoxSize.width / 2),laneHeight + origin.y + beatBallBoxSize.height / 2));
    beatBall->runAction(Sequence::create(moveAction, [this, beatBall]{
        this->removeChild(beatBall);
    }, NULL) );
}

void PlayingScene::beat(){
    int id = experimental::AudioEngine::play2d("sounds/taiko.mp3", false, 1.0f);
    log("%d", id);
}

void PlayingScene::release(){
#if (CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID)
    NativeAndroidHelper::releaseCamera();
#endif
    Director::getInstance()->replaceScene(PlayListScene::createScene());
}

void PlayingScene::update(float dt){
    mTime += dt;
    log(mTime);
}