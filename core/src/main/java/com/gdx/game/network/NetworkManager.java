package com.gdx.game.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.gdx.game.network.packets.PlayerDisconnectPacket;
import com.gdx.game.network.packets.PlayerJoinPacket;
import com.gdx.game.network.packets.PlayerMovePacket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton quan ly ket noi mang cho game.
 *
 * Che giau KryoNet khoi phan con lai cua game. Cac module khac chi can:
 *   NetworkManager.getInstance().startHost(...);
 *   NetworkManager.getInstance().send(packet);
 *   NetworkManager.getInstance().addListener(listener);
 *
 * Mot tai thoi diem chi co 1 mode duy nhat: NONE / HOST / CLIENT.
 */
public class NetworkManager {

    public static final int DEFAULT_TCP_PORT = 54555;
    public static final int DEFAULT_UDP_PORT = 54777;
    private static final int CONNECT_TIMEOUT_MS = 5000;

    public enum NetworkMode { NONE, HOST, CLIENT }

    private static NetworkManager instance;

    private Server server;
    private Client client;
    private NetworkMode mode = NetworkMode.NONE;
    private final List<NetworkListener> listeners = new ArrayList<>();

    private NetworkManager() {}

    public static NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }

    // ==================== MODE ====================

    public NetworkMode getMode() {
        return mode;
    }

    public boolean isHost() {
        return mode == NetworkMode.HOST;
    }

    public boolean isClient() {
        return mode == NetworkMode.CLIENT;
    }

    // ==================== START HOST ====================

    /** May nay lam host, mo cong va cho client ket noi vao. */
    public void startHost(int tcpPort, int udpPort) throws IOException {
        if (mode != NetworkMode.NONE) {
            throw new IllegalStateException("Da co ket noi dang chay: " + mode);
        }

        server = new Server();
        registerPackets(server.getKryo());
        server.addListener(new ServerBridge());
        server.start();
        server.bind(tcpPort, udpPort);

        mode = NetworkMode.HOST;
    }

    public void startHost() throws IOException {
        startHost(DEFAULT_TCP_PORT, DEFAULT_UDP_PORT);
    }

    // ==================== CONNECT CLIENT ====================

    /** May nay lam client, ket noi den host. */
    public void connectToHost(String ip, int tcpPort, int udpPort) throws IOException {
        if (mode != NetworkMode.NONE) {
            throw new IllegalStateException("Da co ket noi dang chay: " + mode);
        }

        client = new Client();
        registerPackets(client.getKryo());
        client.addListener(new ClientBridge());
        client.start();
        client.connect(CONNECT_TIMEOUT_MS, ip, tcpPort, udpPort);

        mode = NetworkMode.CLIENT;
    }

    public void connectToHost(String ip) throws IOException {
        connectToHost(ip, DEFAULT_TCP_PORT, DEFAULT_UDP_PORT);
    }

    // ==================== DISCONNECT ====================

    public void disconnect() {
        if (server != null) {
            server.stop();
            server = null;
        }
        if (client != null) {
            client.stop();
            client = null;
        }
        mode = NetworkMode.NONE;
    }

    // ==================== SEND ====================

    /**
     * Gui packet.
     *   - HOST: broadcast toi tat ca client
     *   - CLIENT: gui cho host
     */
    public void send(Object packet) {
        if (mode == NetworkMode.HOST) {
            server.sendToAllTCP(packet);
        } else if (mode == NetworkMode.CLIENT) {
            client.sendTCP(packet);
        } else {
            throw new IllegalStateException("Chua co ket noi, khong gui duoc");
        }
    }

    /** Chi host dung: gui toi 1 client cu the. */
    public void sendToClient(int connectionId, Object packet) {
        if (mode != NetworkMode.HOST) {
            throw new IllegalStateException("Chi host moi goi sendToClient duoc");
        }
        server.sendToTCP(connectionId, packet);
    }

    // ==================== LISTENER ====================

    public void addListener(NetworkListener listener) {
        listeners.add(listener);
    }

    public void removeListener(NetworkListener listener) {
        listeners.remove(listener);
    }

    // ==================== INTERNAL ====================

    /** Dang ky cac class packet voi Kryo. Server va client phai dang ky giong nhau. */
    private void registerPackets(Kryo kryo) {
        kryo.register(PlayerMovePacket.class);
        kryo.register(PlayerJoinPacket.class);
        kryo.register(PlayerDisconnectPacket.class);
        // Them packet moi vao day khi can
    }

    /** Bridge giua KryoNet Listener -> NetworkListener cua game (ben Server). */
    private class ServerBridge extends Listener {
        @Override
        public void connected(Connection c) {
            for (NetworkListener l : listeners) l.onConnected(c.getID());
        }

        @Override
        public void disconnected(Connection c) {
            for (NetworkListener l : listeners) l.onDisconnected(c.getID());
        }

        @Override
        public void received(Connection c, Object packet) {
            if (packet instanceof com.esotericsoftware.kryonet.FrameworkMessage) return;
            for (NetworkListener l : listeners) l.onPacketReceived(c.getID(), packet);
        }
    }

    /** Bridge ben Client. */
    private class ClientBridge extends Listener {
        @Override
        public void connected(Connection c) {
            for (NetworkListener l : listeners) l.onConnected(c.getID());
        }

        @Override
        public void disconnected(Connection c) {
            for (NetworkListener l : listeners) l.onDisconnected(c.getID());
        }

        @Override
        public void received(Connection c, Object packet) {
            if (packet instanceof com.esotericsoftware.kryonet.FrameworkMessage) return;
            for (NetworkListener l : listeners) l.onPacketReceived(c.getID(), packet);
        }
    }
}
