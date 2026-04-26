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

public class Cave2 extends Map {

    private static String mapPath = "asset/map/CAVE_2.tmx";

    public Cave2() {
        super(MapFactory.MapType.CAVE_2, mapPath);

        Entity merchant = EntityFactory.getInstance().getEntityByName(EntityFactory.EntityName.LOST_MERCHANT);
        initSpecialEntityPosition(merchant);
        mapEntities.add(merchant);
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
