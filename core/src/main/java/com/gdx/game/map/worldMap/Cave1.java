package com.gdx.game.map.worldMap;

import com.badlogic.gdx.math.Vector2;
import com.gdx.game.audio.AudioObserver;
import com.gdx.game.component.Component;
import com.gdx.game.entities.Entity;
import com.gdx.game.entities.EntityConfig;
import com.gdx.game.entities.EntityFactory;
import com.gdx.game.map.Map;
import com.gdx.game.map.MapFactory;
import com.gdx.game.profile.ProfileManager;

import static com.gdx.game.audio.AudioObserver.AudioTypeEvent.VILLAGE_THEME;

public class Cave1 extends Map {

    private static String mapPath = "asset/map/CAVE_1.tmx";

    public Cave1() {
        super(MapFactory.MapType.CAVE_1, mapPath);

        Entity hunter = EntityFactory.getInstance().getEntityByName(EntityFactory.EntityName.HUNTER_WOUNDED);
        initSpecialEntityPosition(hunter);
        mapEntities.add(hunter);
    }

    @Override
    public AudioObserver.AudioTypeEvent getMusicTheme() {
        return VILLAGE_THEME;
    }

    @Override
    public void unloadMusic() {
        notify(AudioObserver.AudioCommand.MUSIC_STOP, getMusicTheme());
    }

    @Override
    public void loadMusic() {
        notify(AudioObserver.AudioCommand.MUSIC_LOAD, getMusicTheme());
        notify(AudioObserver.AudioCommand.MUSIC_PLAY_LOOP, getMusicTheme());
    }

    private void initSpecialEntityPosition(Entity entity) {
        Vector2 position = new Vector2(0, 0);

        if (specialNPCStartPositions.containsKey(entity.getEntityConfig().getEntityID())) {
            position = specialNPCStartPositions.get(entity.getEntityConfig().getEntityID());
        }
        entity.sendMessage(Component.MESSAGE.INIT_START_POSITION, json.toJson(position));

        Object stored = ProfileManager.getInstance().getProperty(entity.getEntityConfig().getEntityID(), Object.class);
        if (stored instanceof EntityConfig entityConfig) {
            entity.setEntityConfig(entityConfig);
        }
    }
}
