#ifndef __WebSocket_H__
#define __WebSocket_H__

#include "cocos2d.h"
#include "network/WebSocket.h"

class WebSocket: public cocos2d::network::WebSocket::Delegate
{
private:
    std::string mUrl;
    cocos2d::network::WebSocket* mWebsocket;
public:
    std::function<void(std::string message)> onMessageReceived;
    std::function<void()> onConnectionClosed;
    std::function<void(const cocos2d::network::WebSocket::ErrorCode &error)> onErrorOccurred;
    std::function<void()> onConnectionOpened;
    
    WebSocket(std::string url);
    
    void connect();
    
    virtual void onOpen(cocos2d::network::WebSocket* ws);
    virtual void onMessage(cocos2d::network::WebSocket* ws, const cocos2d::network::WebSocket::Data& data);
    virtual void onError(cocos2d::network::WebSocket* ws, const cocos2d::network::WebSocket::ErrorCode& error);
    virtual void onClose(cocos2d::network::WebSocket* ws);
    
    void close();
    void send(std::string message);
    
    ~WebSocket();
};

#endif /* WebSocket_h */
