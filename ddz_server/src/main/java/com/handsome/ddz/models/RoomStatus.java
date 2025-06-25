package com.handsome.ddz.models;

public enum RoomStatus {
    ROOM_INVALID,
    ROOM_WAITREADY,  //等待游戏
    ROOM_GAMESTART,  //开始游戏
    ROOM_PUSHCARD,   //发牌
    ROOM_ROBSTATE,    //抢地主
    ROOM_SHOWBOTTOMCARD, //显示底牌
    ROOM_PLAYING,
    ;//出牌阶段
}
