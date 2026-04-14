package com.gdx.game.network;

/**
 * DEMO KET NOI 2 MAY QUA LAN
 *
 * Cach chay:
 *   May 1 (Host):  chay DemoServer.main()
 *   May 2 (Join):  chay DemoClient.main() — nhap IP may 1
 *
 * Cach hoat dong:
 *
 *   [May 1 - Server]                  [May 2 - Client]
 *        |                                  |
 *   Mo cong 54555                           |
 *   Cho ket noi...                          |
 *        |  <-------- ket noi -------  connect(ip, 54555)
 *        |                                  |
 *        |  <--- gui PlayerMove(x,y) -----  |
 *        |                                  |
 *        |  --- gui "OK nhan roi" -------->  |
 *        |                                  |
 *
 * Packet la 1 class Java binh thuong, KryoNet tu dong
 * chuyen thanh bytes gui qua mang roi chuyen lai thanh Object.
 */
public class NetworkDemo {

    // Cong mac dinh
    public static final int TCP_PORT = 54555;
    public static final int UDP_PORT = 54777;

    /**
     * Packet gui vi tri nguoi choi.
     * QUAN TRONG: phai co constructor khong tham so (KryoNet yeu cau)
     */
    public static class PlayerMove {
        public float x;
        public float y;
        public String playerName;

        public PlayerMove() {} // bat buoc

        public PlayerMove(String name, float x, float y) {
            this.playerName = name;
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return playerName + " di chuyen den (" + x + ", " + y + ")";
        }
    }

    /**
     * Packet phan hoi tu server
     */
    public static class ServerResponse {
        public String message;

        public ServerResponse() {}

        public ServerResponse(String message) {
            this.message = message;
        }
    }
}
