package com.gdx.game.network;

import java.io.IOException;

/**
 * Smoke test NET-3: start host roi disconnect, verify khong crash.
 *
 * Chay:
 *   java -cp "$(./gradlew -q :core:printClasspath)" com.gdx.game.network.NetworkManagerTest
 *
 * De test ket noi that (2 phia), dung 2 terminal:
 *   Term 1: ./run-server.sh
 *   Term 2: ./run-client.sh
 */
public class NetworkManagerTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        NetworkManager net = NetworkManager.getInstance();

        System.out.println("[1] Mode truoc khi start: " + net.getMode());
        assert net.getMode() == NetworkManager.NetworkMode.NONE;

        System.out.println("[2] Start host...");
        net.startHost();
        System.out.println("    Mode sau khi start: " + net.getMode());
        assert net.isHost();

        System.out.println("[3] Thu start lan 2 (phai throw)...");
        try {
            net.startHost();
            System.out.println("    FAIL: khong throw");
        } catch (IllegalStateException e) {
            System.out.println("    OK: " + e.getMessage());
        }

        Thread.sleep(500);

        System.out.println("[4] Disconnect...");
        net.disconnect();
        System.out.println("    Mode sau disconnect: " + net.getMode());
        assert net.getMode() == NetworkManager.NetworkMode.NONE;

        System.out.println();
        System.out.println("SMOKE TEST PASS");
        System.out.println("De test ket noi that, chay 2 terminal:");
        System.out.println("  Term 1: ./run-server.sh");
        System.out.println("  Term 2: ./run-client.sh");

        System.exit(0);
    }
}
