package com.handsome.ddz.network;

import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.SocketIOClient;

public class SocketHelper {

    public static void sendMessage(String event, Object data, SocketIOClient socket) {
        socket.sendEvent(event, data);
    }

    public static void _notify(String type, Integer result, Object data, Integer callIndex, SocketIOClient socket) {
        JSONObject payload = new JSONObject();
        payload.put("type", type);
        payload.put("data", data);
        payload.put("result", result);
        payload.put("callBackIndex", callIndex);
        sendMessage("notify", payload, socket);
    }
}
