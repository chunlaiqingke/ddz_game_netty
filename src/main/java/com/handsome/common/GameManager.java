package com.handsome.common;

import com.handsome.model.Player;
import com.handsome.model.Room;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class GameManager {

	private final static Map<Long, Room> ROOM_MAP = new ConcurrentSkipListMap<>();

	public final static Map<Long, Player> PLAYER_MAP = new ConcurrentSkipListMap<>();

	public static void createPlayer(Player player, Long uniqueId){
		Player player1 = PLAYER_MAP.get(uniqueId);
		if (player1 != null){
			return;
		}
		PLAYER_MAP.put(uniqueId, player);
	}

	public static Player getPlayer(Long uniqueId){
		return PLAYER_MAP.get(uniqueId);
	}

	public static void createRoom(Room room, Long uniqueId){
		Room room1 = ROOM_MAP.get(uniqueId);
		if (room1 != null){
			return;
		}
		ROOM_MAP.put(uniqueId, room);
	}
}
