package com.gdx.game.network;

import com.gdx.game.network.packets.PlayerMovePacket;

import java.io.IOException;
import java.util.Scanner;

/**
 * MAY 2 CHAY CAI NAY (Join game)
 *
 * Demo su dung NetworkManager.
 */
public class DemoClient {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        NetworkManager net = NetworkManager.getInstance();

        net.addListener(new NetworkListener() {
            @Override
            public void onPacketReceived(int connectionId, Object packet) {
                if (packet instanceof PlayerMovePacket move) {
                    System.out.println("<<< Server tra loi: " + move);
                }
            }

            @Override
            public void onDisconnected(int connectionId) {
                System.out.println("<<< Mat ket noi voi server!");
            }
        });

        System.out.println("========================================");
        System.out.println("  NHAP IP CUA MAY HOST (vd: 192.168.1.5)");
        System.out.println("  Hoac nhap 'localhost' neu test tren 1 may");
        System.out.print("  IP: ");
        String ip = scanner.nextLine().trim();

        try {
            net.connectToHost(ip);
            System.out.println("  KET NOI THANH CONG!");
            System.out.println("========================================");
        } catch (IOException e) {
            System.out.println("  LOI: Khong ket noi duoc toi " + ip);
            System.out.println("  Kiem tra: Server da chay chua? Cung mang WiFi chua?");
            return;
        }

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

                PlayerMovePacket move = new PlayerMovePacket(2, x, y, "VILLAGE");
                net.send(move);
                System.out.println(">>> Da gui: " + move);
            } catch (Exception e) {
                System.out.println("Sai format! Nhap 2 so cach nhau, vd: 5 10");
            }
        }

        net.disconnect();
        System.out.println("Da ngat ket noi.");
    }
}
