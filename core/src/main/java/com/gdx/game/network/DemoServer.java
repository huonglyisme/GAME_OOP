package com.gdx.game.network;

import com.gdx.game.network.packets.PlayerMovePacket;

import java.io.IOException;

/**
 * MAY 1 CHAY CAI NAY (Host game)
 *
 * Demo su dung NetworkManager thay vi truc tiep dung KryoNet.
 */
public class DemoServer {

    public static void main(String[] args) throws IOException {
        NetworkManager net = NetworkManager.getInstance();

        net.addListener(new NetworkListener() {
            @Override
            public void onConnected(int connectionId) {
                System.out.println(">>> Co nguoi ket noi! ID: " + connectionId);
            }

            @Override
            public void onDisconnected(int connectionId) {
                System.out.println(">>> Nguoi choi ngat ket noi! ID: " + connectionId);
            }

            @Override
            public void onPacketReceived(int connectionId, Object packet) {
                if (packet instanceof PlayerMovePacket move) {
                    System.out.println(">>> Nhan duoc: " + move);
                    net.sendToClient(connectionId,
                        new com.gdx.game.network.packets.PlayerMovePacket(
                            -1, move.x, move.y, "ack:" + move.mapName));
                }
            }
        });

        net.startHost();

        System.out.println("========================================");
        System.out.println("  SERVER DANG CHAY");
        System.out.println("  Cong TCP: " + NetworkManager.DEFAULT_TCP_PORT);
        System.out.println("  Cho nguoi choi ket noi...");
        System.out.println("========================================");
    }
}
