#include "WebSocket.h"

USING_NS_CC;

WebSocket::WebSocket(std::string url)
{
    mUrl = url;
};

void WebSocket::connect()
{
    mWebsocket = new cocos2d::network::WebSocket();
    mWebsocket->init(*this, mUrl.c_str());
}

void WebSocket::close()
{
    if (mWebsocket->getReadyState() == cocos2d::network::WebSocket::State::OPEN)
    {
        mWebsocket->close();
    }
}

WebSocket::~WebSocket()
{
    mWebsocket->close();
}

void WebSocket::send(std::string message)
{
    if (mWebsocket->getReadyState() == cocos2d::network::WebSocket::State::OPEN)
    {
        mWebsocket->send(message);
    }
}

void WebSocket::onOpen(cocos2d::network::WebSocket* ws)
{
    CCLOG("WebSocket connection opened: %s", mUrl.c_str());
    if ( onConnectionOpened )
    {
        onConnectionOpened();
    }
}

void WebSocket::onMessage(cocos2d::network::WebSocket* ws, const cocos2d::network::WebSocket::Data &data)
{
    if ( onMessageReceived )
    {
        onMessageReceived(data.bytes);
    }
    
}

void WebSocket::onError(cocos2d::network::WebSocket* ws, const cocos2d::network::WebSocket::ErrorCode& error)
{
    if ( onErrorOccurred )
    {
        onErrorOccurred(error);
    }
}

void WebSocket::onClose(cocos2d::network::WebSocket* ws)
{
    if ( onConnectionClosed )
    {
        onConnectionClosed();
    }
}
