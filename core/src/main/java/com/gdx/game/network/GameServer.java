package com.gdx.game.network;

import com.gdx.game.network.packets.GameStateSyncPacket;
import com.gdx.game.network.packets.PlayerDisconnectPacket;
import com.gdx.game.network.packets.PlayerJoinPacket;
import com.gdx.game.network.packets.PlayerMovePacket;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Logic server-side cho phien co-op.
 *
 * Trach nhiem:
 *   - Giu danh sach nguoi choi dang connect (connectionId -> RemotePlayer)
 *   - Khi client join: gui GameStateSyncPacket (snapshot toan bo) + broadcast PlayerJoinPacket cho nguoi khac
 *   - Khi nhan PlayerMovePacket: cap nhat state + broadcast cho cac client con lai
 *   - Khi disconnect: xoa khoi map + broadcast PlayerDisconnectPacket
 *
 * Trust model: server khong kiem tra gia tri toa do / hp hop le hay khong,
 * chi forward data. Validate se them sau (neu can).
 */
public class GameServer implements NetworkListener {

    private final NetworkManager net = NetworkManager.getInstance();
    private final Map<Integer, RemotePlayer> players = new HashMap<>();

    public void start() throws IOException {
        net.addListener(this);
        net.startHost();
    }

    public void stop() {
        net.removeListener(this);
        net.disconnect();
        players.clear();
    }

    public Collection<RemotePlayer> getPlayers() {
        return Collections.unmodifiableCollection(players.values());
    }

    @Override
    public void onConnected(int connectionId) {
        RemotePlayer p = new RemotePlayer(connectionId, "Player" + connectionId);
        players.put(connectionId, p);

        RemotePlayer[] snapshot = players.values().toArray(new RemotePlayer[0]);
        net.sendToClient(connectionId, new GameStateSyncPacket(snapshot));

        PlayerJoinPacket join = new PlayerJoinPacket(p.id, p.name);
        for (int id : players.keySet()) {
            if (id != connectionId) {
                net.sendToClient(id, join);
            }
        }

        System.out.println("[GameServer] Player joined: " + p);
    }

    @Override
    public void onDisconnected(int connectionId) {
        RemotePlayer removed = players.remove(connectionId);
        if (removed == null) return;

        PlayerDisconnectPacket packet = new PlayerDisconnectPacket(removed.id, "disconnected");
        for (int id : players.keySet()) {
            net.sendToClient(id, packet);
        }

        System.out.println("[GameServer] Player left: " + removed);
    }

    @Override
    public void onPacketReceived(int connectionId, Object packet) {
        if (packet instanceof PlayerMovePacket move) {
            RemotePlayer p = players.get(connectionId);
            if (p != null) {
                p.x = move.x;
                p.y = move.y;
                p.mapName = move.mapName;
            }
            for (int id : players.keySet()) {
                if (id != connectionId) {
                    net.sendToClient(id, move);
                }
            }
        }
    }
}
