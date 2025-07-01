package com.handsome.ddz.game;

import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.SocketIOClient;
import com.handsome.ddz.config.GameConfig;
import com.handsome.ddz.config.RobStatus;
import com.handsome.ddz.config.RoomConfig;
import com.handsome.ddz.models.RoomStatus;
import com.handsome.ddz.util.IDMaker;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    private List<Player> robPlayer = new ArrayList<>();

    private Player roomMaster;

    private List<List<Card>> threeCards = new ArrayList<>();// 地主牌

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
            Player player1 = playerList.get(i);
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

    public void playerReady(Player player) {
        for (Player p : playerList) {
            p.sendPlayerReady(player.getAccountId());
        }
    }

    /**
     *
     * @param player
     * @return error code
     */
    public int playerStart(Player player){
        if(playerList.size() != 3){
            return -2;
        }

        //判断是有都准备成功
        for (Player p : playerList) {
            if (!Objects.equals(p.getAccountId(), this.ownPlayer.getAccountId())) {
                if (!p.isReady()) {
                    return -3;
                }
            }
        }

        //下发游戏开始广播消息
        //gameStart()
        changeState(RoomStatus.ROOM_GAMESTART);

        //开始游戏
        return 0;
    }

    private void gameStart() {
        for(Player player: this.playerList){
            player.gameStart();
        }
    }

    private void changeState(RoomStatus state) {
        if(this.state==state){
            return;
        }
        this.state = state;
        switch(state){
            case ROOM_WAITREADY:
                break;
            case ROOM_GAMESTART:
                gameStart();
                //切换到发牌状态
                changeState(RoomStatus.ROOM_PUSHCARD);
                break;
            case ROOM_PUSHCARD:
                //这个函数把54张牌分成4份[玩家1，玩家2，玩家3,底牌]
                this.threeCards = this.carder.splitThreeCards();
                for(int i = 0; i < this.playerList.size(); i++){
                    Player player = playerList.get(i);
                    player.sendCard(this.threeCards.get(i));
                }
                //切换到抢地主状态
                changeState(RoomStatus.ROOM_ROBSTATE);
                break;
            case ROOM_ROBSTATE:
                this.robPlayer=new ArrayList<>();
                for(int i=this.playerList.size()-1; i>=0; i--){
                    robPlayer.add(this.playerList.get(i));
                }
                turnRob();
                break;
            case ROOM_SHOWBOTTOMCARD:
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                changeState(RoomStatus.ROOM_PLAYING);
                //下个当前状态给客户端
                for (Player player : this.playerList) {
                    player.sendRoomState(RoomStatus.ROOM_PLAYING);
                }
                break;
            case ROOM_PLAYING:
                resetChuCardPlayer();
                //下发出牌消息
                turnchuCard();
                break;
            default:
                break;
        }
    }

    private void turnRob() {
        if(this.robPlayer.isEmpty()){
            //都抢过了，需要确定最终地主人选,直接退出
            changeMaster();
            //改变房间状态，显示底牌
            changeState(RoomStatus.ROOM_SHOWBOTTOMCARD);
            return;
        }

        //弹出已经抢过的用户
        Player canPlayer = this.robPlayer.remove(0);
        if(this.robPlayer.isEmpty() && this.roomMaster==null){
            //没有抢地主，并且都抢过了,就设置为最后抢的玩家
            this.roomMaster = canPlayer;
            //return
        }

        for (Player player : this.playerList) {
            //通知下一个可以抢地主的玩家
            player.sendCanRob(canPlayer.getAccountId());
        }
    }

    private void changeMaster() {
        for(Player player: this.playerList){
            player.sendChangeMaster(this.roomMaster.getAccountId());
        }

        //显示底牌
        for(Player player: this.playerList){
            //把三张底牌的消息发送给房间里的用户
            player.sendShowBottomCard(this.threeCards.get(3));
        }
    }

    private void resetChuCardPlayer() {
    }

    private void turnchuCard() {
    }

    public void playerRobMaster(Player player,Integer data) {
        if(RobStatus.notRob==data){
            //记录当前抢到地主的玩家id

        }else if(RobStatus.rob==data){
            this.roomMaster = player;
        }else{
            System.out.println("playerRobmaster state error:" + data);
        }
        if(player==null){
            System.out.println("trun rob master end");
            return;
        }
        //广播这个用户抢地主状态(抢了或者不抢)
        Integer value = data;
        for(Player p: this.playerList){
            JSONObject d = new JSONObject();
            d.put("accountid",player.getAccountId());
            d.put("state",value);
            p.sendRobState(d);
        }
        turnRob();
    }

    public void playerChuBuCard(Player player, Integer data) {

    }

    public void playerChuCard(Player player, Integer data) {

    }
}
