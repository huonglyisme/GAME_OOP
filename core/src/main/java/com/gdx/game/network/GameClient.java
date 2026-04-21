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
 * Logic client-side cho phien co-op.
 *
 * Trach nhiem:
 *   - Giu danh sach nguoi choi khac (local cache) de render
 *   - Nhan GameStateSyncPacket luc vua join -> khoi tao map
 *   - Nhan PlayerJoinPacket -> them player moi
 *   - Nhan PlayerMovePacket -> cap nhat toa do
 *   - Nhan PlayerDisconnectPacket -> xoa player
 *
 * Game loop chi can goi getRemotePlayers() de ve cac nguoi choi khac.
 */
public class GameClient implements NetworkListener {

    private final NetworkManager net = NetworkManager.getInstance();
    private final Map<Integer, RemotePlayer> remotePlayers = new HashMap<>();

    public void connect(String ip) throws IOException {
        net.addListener(this);
        net.connectToHost(ip);
    }

    public void disconnect() {
        net.removeListener(this);
        net.disconnect();
        remotePlayers.clear();
    }

    public Collection<RemotePlayer> getRemotePlayers() {
        return Collections.unmodifiableCollection(remotePlayers.values());
    }

    /** Gui toa do cua chinh minh len server. */
    public void sendMove(int myId, float x, float y, String mapName) {
        net.send(new PlayerMovePacket(myId, x, y, mapName));
    }

    @Override
    public void onPacketReceived(int connectionId, Object packet) {
        if (packet instanceof GameStateSyncPacket sync) {
            remotePlayers.clear();
            if (sync.players != null) {
                for (RemotePlayer p : sync.players) {
                    remotePlayers.put(p.id, p);
                }
            }
            System.out.println("[GameClient] Sync received, " + remotePlayers.size() + " players");
        } else if (packet instanceof PlayerJoinPacket join) {
            remotePlayers.put(join.playerId, new RemotePlayer(join.playerId, join.playerName));
            System.out.println("[GameClient] Join: " + join.playerName);
        } else if (packet instanceof PlayerMovePacket move) {
            RemotePlayer p = remotePlayers.get(move.playerId);
            if (p != null) {
                p.x = move.x;
                p.y = move.y;
                p.mapName = move.mapName;
            }
        } else if (packet instanceof PlayerDisconnectPacket dc) {
            remotePlayers.remove(dc.playerId);
            System.out.println("[GameClient] Disconnect: " + dc.playerId);
        }
    }

    @Override
    public void onDisconnected(int connectionId) {
        System.out.println("[GameClient] Mat ket noi voi server");
        remotePlayers.clear();
    }
}
