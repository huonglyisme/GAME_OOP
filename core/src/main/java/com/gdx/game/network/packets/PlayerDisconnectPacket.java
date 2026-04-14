package com.gdx.game.network.packets;

/**
 * Packet thong bao nguoi choi ngat ket noi.
 * Host gui broadcast khi co nguoi thoat game.
 */
public class PlayerDisconnectPacket {

    public int playerId;
    public String reason;

    public PlayerDisconnectPacket() {}

    public PlayerDisconnectPacket(int playerId, String reason) {
        this.playerId = playerId;
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "PlayerDisconnect[id=" + playerId + ", reason=" + reason + "]";
    }
}
