package com.gdx.game.battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.gdx.game.entities.EntityConfig;

import java.util.ArrayList;

public final class DropTableManager {

    private static final String DROP_TABLES_PATH = "scripts/drop_tables.json";

    private static final Json JSON = new Json();
    private static final ObjectMap<String, Array<EntityConfig.Drop>> DROP_TABLES = new ObjectMap<>();
    private static boolean loaded;

    private DropTableManager() {
    }

    public static Array<EntityConfig.Drop> getDropsForTable(String dropTableID) {
        if (dropTableID == null || dropTableID.isEmpty()) {
            return new Array<>();
        }

        ensureLoaded();
        Array<EntityConfig.Drop> drops = DROP_TABLES.get(dropTableID);
        if (drops == null) {
            return new Array<>();
        }

        // Return a copy to avoid accidental mutation of the shared table cache.
        Array<EntityConfig.Drop> copiedDrops = new Array<>();
        for (EntityConfig.Drop drop : drops) {
            EntityConfig.Drop copied = new EntityConfig.Drop();
            copied.setItemTypeID(drop.getItemTypeID());
            copied.setProbability(drop.getProbability());
            copiedDrops.add(copied);
        }
        return copiedDrops;
    }

    static void clearCache() {
        DROP_TABLES.clear();
        loaded = false;
    }

    private static void ensureLoaded() {
        if (loaded) {
            return;
        }

        ArrayList<JsonValue> list = JSON.fromJson(ArrayList.class, Gdx.files.internal(DROP_TABLES_PATH));
        for (JsonValue jsonVal : list) {
            DropTableConfig config = JSON.readValue(DropTableConfig.class, jsonVal);
            if (config.dropTableID == null || config.dropTableID.isEmpty()) {
                continue;
            }

            Array<EntityConfig.Drop> drops = config.drops == null ? new Array<>() : config.drops;
            DROP_TABLES.put(config.dropTableID, drops);
        }

        loaded = true;
    }

    public static class DropTableConfig {
        private String dropTableID;
        private Array<EntityConfig.Drop> drops;

        public String getDropTableID() {
            return dropTableID;
        }

        public Array<EntityConfig.Drop> getDrops() {
            return drops;
        }
    }
}
