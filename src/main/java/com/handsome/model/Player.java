package com.handsome.model;

import com.handsome.model.enums.PlayerRole;
import com.handsome.model.enums.PlayerType;
import io.netty.channel.Channel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Player implements Serializable {

	private Integer uniqueID;
	private Integer callIndex;
	private PlayerRole role;
	private PlayerType type;
	private String nickName;
	private String accountID;
	private String avatarUrl;
	private int goldCnt;
	private Channel channel;
	private int seatIndex;//room seat index
	private boolean isReady;//ready button is on  or off
	private List<Poker> pokers;
	private Room room;

	public Player() {

	}

	public Player(Channel channel){
		this.channel = channel;
	}

	public Player(String nickName, String accountID, String avatarUrl, Channel channel) {
		this.nickName = nickName;
		this.accountID = accountID;
		this.avatarUrl = avatarUrl;
		this.channel = channel;
	}
}
