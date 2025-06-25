package com.handsome.ddz.game;

import com.alibaba.fastjson.JSONObject;
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

    private Long roomId;

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
        this.roomId = IDMaker.randomId();
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
        playerList.add(player);
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
