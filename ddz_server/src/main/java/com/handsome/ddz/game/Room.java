package com.handsome.ddz.game;

import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.SocketIOClient;
import com.handsome.ddz.config.GameConfig;
import com.handsome.ddz.config.RoomConfig;
import com.handsome.ddz.models.RoomStatus;
import com.handsome.ddz.util.IDMaker;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 房间里应该包含：玩家列表，房间状态，房主，观战人
 */
@Data
public class Room {

    private String roomId;

    private List<Player> playerList = new ArrayList<>();

    private Player ownPlayer;

    private List<Player> viewers = new ArrayList<>();

    private Integer bottom;

    private Integer rate;

    private Integer gold;

    private RoomStatus state;

    private Carder carder;

    private Player lostPlayer;

    private Player robPlayer;

    private Player roomMaster;

    private List<Card> masterCards = new ArrayList<>();

    private List<Card> playingCards = new ArrayList<>();//存储出牌的用户(一轮)

    private List<Card> curPushCardList = new ArrayList<>();//当前玩家出牌列表

    private List<Card> lastPushCardList = new ArrayList<>();//玩家上一次出的牌

    private List<Card> lastPushCardAccountId = new ArrayList<>();//最后一个出牌的accountid


    public Room(JSONObject roomInfo, Player ownPlayer) {
        this.roomId = IDMaker.randomId(6);
        this.ownPlayer = ownPlayer;
        RoomConfig roomConfig = GameConfig.getRate(roomInfo.getInteger("rate"));
        this.bottom = roomConfig.getBottom();
        this.rate = roomConfig.getRate();
        this.gold = roomConfig.getNeedCostGold();
        this.state = RoomStatus.ROOM_INVALID;
        this.carder = new Carder();
        joinPlayer(ownPlayer);
    }

    public void joinPlayer(Player player) {
        player.setSeatIndex(getSeatIndex());
        player.setRoom(this);

        JSONObject playerInfo = new JSONObject();
        playerInfo.put("accountid", player.getAccountId());
        playerInfo.put("nick_name", player.getNickName());
        playerInfo.put("avatarUrl", player.getAvatarUrl());
        playerInfo.put("goldcount", player.getGold());
        playerInfo.put("seatindex", player.getSeatIndex());

        //send all player
        for (int i = 0; i < playerList.size(); i++) {
            Player player1 = playerList.get(0);
            player1.sendPlayerJoinRoom(playerInfo);
        }

        playerList.add(player);
    }

    public JSONObject enterRoom(Player  player){
        List<JSONObject> player_data = new ArrayList<>();
        for(int i=0; i<playerList.size(); i++){
            JSONObject data = new JSONObject();
            data.put("accountid", playerList.get(i).getAccountId());
            data.put("nick_name", playerList.get(i).getNickName());
            data.put("avatarUrl", playerList.get(i).getAvatarUrl());
            data.put("goldcount", playerList.get(i).getGold());
            data.put("seatindex", playerList.get(i).getSeatIndex());
            data.put("isready", playerList.get(i).isReady());
            player_data.add(data);
        }

        JSONObject enterroom_para = new JSONObject();
        enterroom_para.put("seatindex", player.getSeatIndex());
        enterroom_para.put("roomid", roomId);
        enterroom_para.put("playerdata", player_data);
        enterroom_para.put("housemanageid", ownPlayer.getAccountId());

        return enterroom_para;
    }

    public int getSeatIndex(){
        int seatindex = 1;
        if(playerList.isEmpty()){
            return seatindex;
        }

        int index = 1;
        for(int i=0;i<playerList.size();i++){
            if(index!=playerList.get(i).getSeatIndex()){
                return index;
            }
            index++;
        }

        return index;
    }
}
