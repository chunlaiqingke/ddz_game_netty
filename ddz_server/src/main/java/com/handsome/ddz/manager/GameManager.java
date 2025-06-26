package com.handsome.ddz.manager;

import com.handsome.ddz.game.Player;
import com.handsome.ddz.game.Room;

import java.util.HashMap;
import java.util.Map;

public class GameManager {

    static Map<Long, Room> hall = new HashMap<>();

    static Map<String, Player> players = new HashMap<>();

    static Map<String, String> session2Account = new HashMap<>();

    public static boolean existPlayer(long uniqueId) {
        return players.containsKey(uniqueId);
    }

    public static Player getPlayer(String sessionId) {
        String accountId = session2Account.get(sessionId);
        return players.get(accountId);
    }

    public static void addPlayer(Player player) {
        String sessionId = player.getSocket().getSessionId().toString();
        players.put(player.getAccountId(), player);
        session2Account.put(sessionId, player.getAccountId());
    }

    public static void removePlayer(String sessionId) {
        String accountId = session2Account.get(sessionId);
        session2Account.remove(sessionId);
        players.remove(accountId);
    }

    public static void createRoom(Room room) {
        hall.put(room.getRoomId(), room);
    }

    public static Room getRoom(long roomId) {
        return hall.get(roomId);
    }
}
