package com.gdx.game.network.packets;

import com.gdx.game.network.RemotePlayer;

/**
 * Server gui cho client moi join de dong bo danh sach nguoi choi hien co.
 *
 * Chi gui 1 lan luc ket noi thanh cong. Sau do dung PlayerMovePacket
 * hoac cac packet khac de update tung event.
 */
public class GameStateSyncPacket {

    public RemotePlayer[] players;

    public GameStateSyncPacket() {}

    public GameStateSyncPacket(RemotePlayer[] players) {
        this.players = players;
    }
}
