package com.gdx.game.network.packets;

/**
 * Packet thong bao co nguoi choi moi ket noi vao game.
 * Host gui broadcast packet nay cho moi client khi co nguoi join.
 */
public class PlayerJoinPacket {

    public int playerId;
    public String playerName;

    public PlayerJoinPacket() {}

    public PlayerJoinPacket(int playerId, String playerName) {
        this.playerId = playerId;
        this.playerName = playerName;
    }

    @Override
    public String toString() {
        return "PlayerJoin[id=" + playerId + ", name=" + playerName + "]";
    }
}
