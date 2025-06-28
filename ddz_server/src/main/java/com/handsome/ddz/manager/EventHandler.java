package com.handsome.ddz.manager;

import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.SocketIOClient;
import com.handsome.ddz.game.Player;
import com.handsome.ddz.game.PlayerInfo;
import com.handsome.ddz.game.Room;
import com.handsome.ddz.network.SocketHelper;

import java.util.ArrayList;
import java.util.List;

public class EventHandler {

    public void handleEvent(SocketIOClient client, JSONObject req) {
        System.out.println(req.toJSONString());
        switch (req.getString("cmd")) {
            case "wxlogin":
                handleWxLogin(client, req);
                break;
            case "createroom_req":
                handleCreateRoom(client, req);
                break;
            case "joinroom_req":
                handleJoinRoom(client, req);
                break;
            case "enterroom_req":
                handleEnterRoom(client, req);
                break;
            default:
                break;
        }
    }

    private void handleWxLogin(SocketIOClient client, JSONObject req) {
        JSONObject json = new JSONObject();
        JSONObject data = req.getJSONObject("data");
        PlayerInfo playerInfo = new PlayerInfo();
        playerInfo.setAccountId(data.getString("accountID"));
        playerInfo.setAvatarUrl(data.getString("avatarUrl"));
        playerInfo.setNickName(data.getString("nickName"));
        playerInfo.setGoldCount(data.getLong("goldcount"));
        Player player = new Player(playerInfo, client, req.getInteger("callindex"));
        GameManager.addPlayer(player);
        SocketHelper._notify("login_resp", 0, json, req.getInteger("callindex"), client);
    }

    private void handleCreateRoom(SocketIOClient client, JSONObject req) {
        JSONObject data = req.getJSONObject("data");
        Player player = GameManager.getPlayer(client.getSessionId().toString());
        Room room = new Room(data, player);
        GameManager.createRoom(room);

        JSONObject resData = new JSONObject();
        resData.put("roomid", room.getRoomId());
        resData.put("bottom", room.getBottom());
        resData.put("rate", room.getRate());
        SocketHelper._notify("createroom_resp", 0, resData, req.getInteger("callindex"), client);
    }

    private void handleJoinRoom(SocketIOClient client, JSONObject req) {
        JSONObject data = req.getJSONObject("data");
        Player player = GameManager.getPlayer(client.getSessionId().toString());
        Room room = GameManager.getRoom(data.getString("roomid"));
        room.joinPlayer(player);
        JSONObject resData = new JSONObject();
        resData.put("roomid", room.getRoomId());
        resData.put("bottom", room.getBottom());
        resData.put("rate", room.getRate());
        resData.put("gold", room.getGold());
        SocketHelper._notify("joinroom_resp", 0, resData, req.getInteger("callindex"), client);
    }

    private void handleEnterRoom(SocketIOClient client, JSONObject req) {
        JSONObject data = req.getJSONObject("data");
        Player player = GameManager.getPlayer(client.getSessionId().toString());
        Room room = player.getRoom();
        JSONObject enterRoomParam = room.enterRoom(player);
        SocketHelper._notify("createroom_resp", 0, enterRoomParam, req.getInteger("callindex"), client);
    }
}
