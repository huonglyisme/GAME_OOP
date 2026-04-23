package com.gdx.game.map;

import com.gdx.game.map.worldMap.CastleFinal;
import com.gdx.game.map.worldMap.CastleRoom;
import com.gdx.game.map.worldMap.Cave1;
import com.gdx.game.map.worldMap.Cave3;
import com.gdx.game.map.worldMap.Forest;
import com.gdx.game.map.worldMap.Village;

import java.util.Hashtable;

public class MapFactory {
    //All maps for the game
    private static Hashtable<MapType, Map> mapTable = new Hashtable<>();

    public enum MapType {
        VILLAGE,
        FOREST,
        CAVE_1,
        CAVE_3,
        CASTLE_ROOM,
        CASTLE_FINAL
    }

    public static Hashtable<MapType, Map> getMapTable() {
        return mapTable;
    }

    public static Map getMap(MapType mapType) {
        Map map = mapTable.get(mapType);
        if (map != null) {
            return map;
        }
        switch (mapType) {
            case VILLAGE -> map = new Village();
            case FOREST -> map = new Forest();
            case CAVE_1 -> map = new Cave1();
            case CAVE_3 -> map = new Cave3();
            case CASTLE_ROOM -> map = new CastleRoom();
            case CASTLE_FINAL -> map = new CastleFinal();
        }
        if (map != null) {
            mapTable.put(mapType, map);
        }
        return map;
    }

    public static void clearCache() {
        for(Map map: mapTable.values()) {
            map.dispose();
        }
        mapTable.clear();
    }
}
