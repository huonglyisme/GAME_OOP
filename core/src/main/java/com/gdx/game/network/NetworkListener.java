package com.gdx.game.network;

/**
 * Callback khi co su kien mang xay ra.
 *
 * Cac module khac cua game (Battle, Map, Player...) implement interface nay
 * de nhan thong bao ma khong can biet KryoNet la gi.
 *
 * Cach dung:
 *   NetworkManager.getInstance().addListener(new NetworkListener() {
 *       public void onPacketReceived(int connectionId, Object packet) { ... }
 *   });
 */
public interface NetworkListener {

    /** Mot connection moi duoc tao (chi Host goi, sau khi client ket noi vao). */
    default void onConnected(int connectionId) {}

    /** Mot connection bi ngat. */
    default void onDisconnected(int connectionId) {}

    /** Nhan duoc packet tu ben kia. */
    default void onPacketReceived(int connectionId, Object packet) {}
}
