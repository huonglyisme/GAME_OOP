package com.gdx.game.network;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.util.Scanner;

/**
 * MAY 2 CHAY CAI NAY (Join game)
 *
 * Chay: click Run trong IDE hoac:
 *   cd ECHOES_OF_MANAFALL
 *   ./gradlew :core:run -PmainClass=com.gdx.game.network.DemoClient
 */
public class DemoClient {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        // 1. Tao client
        Client client = new Client();

        // 2. Dang ky packet (PHAI GIONG SERVER)
        client.getKryo().register(NetworkDemo.PlayerMove.class);
        client.getKryo().register(NetworkDemo.ServerResponse.class);

        // 3. Lang nghe phan hoi tu server
        client.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof NetworkDemo.ServerResponse response) {
                    System.out.println("<<< Server noi: " + response.message);
                }
            }

            @Override
            public void disconnected(Connection connection) {
                System.out.println("<<< Mat ket noi voi server!");
            }
        });

        client.start();

        // 4. Nhap IP va ket noi
        System.out.println("========================================");
        System.out.println("  NHAP IP CUA MAY HOST (vd: 192.168.1.5)");
        System.out.println("  Hoac nhap 'localhost' neu test tren 1 may");
        System.out.print("  IP: ");
        String ip = scanner.nextLine().trim();

        try {
            client.connect(5000, ip, NetworkDemo.TCP_PORT, NetworkDemo.UDP_PORT);
            System.out.println("  KET NOI THANH CONG!");
            System.out.println("========================================");
        } catch (IOException e) {
            System.out.println("  LOI: Khong ket noi duoc toi " + ip);
            System.out.println("  Kiem tra: Server da chay chua? Cung mang WiFi chua?");
            return;
        }

        // 5. Gui vi tri thu
        System.out.println("");
        System.out.println("Nhap toa do de gui cho server (vd: 5 10)");
        System.out.println("Nhap 'quit' de thoat");
        System.out.println("");

        while (true) {
            System.out.print("Nhap x y: ");
            String line = scanner.nextLine().trim();

            if (line.equals("quit")) {
                break;
            }

            try {
                String[] parts = line.split("\\s+");
                float x = Float.parseFloat(parts[0]);
                float y = Float.parseFloat(parts[1]);

                // Gui packet cho server
                NetworkDemo.PlayerMove move = new NetworkDemo.PlayerMove("Player2", x, y);
                client.sendTCP(move);
                System.out.println(">>> Da gui: " + move);
            } catch (Exception e) {
                System.out.println("Sai format! Nhap 2 so cach nhau, vd: 5 10");
            }
        }

        client.close();
        System.out.println("Da ngat ket noi.");
    }
}
