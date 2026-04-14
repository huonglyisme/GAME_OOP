package com.gdx.game.network.packets;

/**
 * Packet gui vi tri nguoi choi qua mang.
 *
 * KryoNet yeu cau:
 *   - Constructor khong tham so
 *   - Truong public hoac co getter/setter
 */
public class PlayerMovePacket {

    public int playerId;
    public float x;
    public float y;
    public String mapName;

    public PlayerMovePacket() {}

    public PlayerMovePacket(int playerId, float x, float y, String mapName) {
        this.playerId = playerId;
        this.x = x;
        this.y = y;
        this.mapName = mapName;
    }

    @Override
    public String toString() {
        return "PlayerMove[id=" + playerId + ", x=" + x + ", y=" + y + ", map=" + mapName + "]";
    }
}
