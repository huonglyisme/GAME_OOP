package com.gdx.game.network;

/**
 * Dataclass mo ta 1 nguoi choi khac trong phien multiplayer.
 *
 * Server giu map: connectionId -> RemotePlayer.
 * Client giu map local de ve nguoi choi khac tren man hinh.
 *
 * Trust model: khong validate, gia tri nhan duoc tu client duoc tin tuong.
 */
public class RemotePlayer {

    public int id;
    public String name;
    public float x;
    public float y;
    public String mapName;
    public int hp;

    public RemotePlayer() {
        this.mapName = "VILLAGE";
        this.hp = 100;
    }

    public RemotePlayer(int id, String name) {
        this();
        this.id = id;
        this.name = name;
    }

    public RemotePlayer(int id, String name, float x, float y, String mapName, int hp) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.mapName = mapName;
        this.hp = hp;
    }

    @Override
    public String toString() {
        return "RemotePlayer{id=" + id + ", name=" + name
            + ", pos=(" + x + "," + y + "), map=" + mapName + ", hp=" + hp + "}";
    }
}
