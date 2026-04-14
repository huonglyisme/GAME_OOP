package com.gdx.game.network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;

/**
 * MAY 1 CHAY CAI NAY (Host game)
 *
 * Chay: click Run trong IDE hoac:
 *   cd ECHOES_OF_MANAFALL
 *   ./gradlew :core:run -PmainClass=com.gdx.game.network.DemoServer
 */
public class DemoServer {

    public static void main(String[] args) throws IOException {
        // 1. Tao server
        Server server = new Server();

        // 2. Dang ky cac loai packet (client va server phai dang ky GIONG NHAU)
        server.getKryo().register(NetworkDemo.PlayerMove.class);
        server.getKryo().register(NetworkDemo.ServerResponse.class);

        // 3. Lang nghe su kien
        server.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                System.out.println(">>> Co nguoi ket noi! ID: " + connection.getID());
                System.out.println(">>> IP cua ho: " + connection.getRemoteAddressTCP());
            }

            @Override
            public void received(Connection connection, Object object) {
                // Khi nhan duoc PlayerMove tu client
                if (object instanceof NetworkDemo.PlayerMove move) {
                    System.out.println(">>> Nhan duoc: " + move);

                    // Gui phan hoi lai
                    NetworkDemo.ServerResponse response = new NetworkDemo.ServerResponse(
                        "Server da nhan vi tri cua " + move.playerName
                    );
                    connection.sendTCP(response);
                }
            }

            @Override
            public void disconnected(Connection connection) {
                System.out.println(">>> Nguoi choi ngat ket noi! ID: " + connection.getID());
            }
        });

        // 4. Mo cong va cho
        server.bind(NetworkDemo.TCP_PORT, NetworkDemo.UDP_PORT);
        server.start();

        System.out.println("========================================");
        System.out.println("  SERVER DANG CHAY");
        System.out.println("  Cong TCP: " + NetworkDemo.TCP_PORT);
        System.out.println("  Cho nguoi choi ket noi...");
        System.out.println("========================================");
    }
}
