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

public class CastleRoom extends Map {

    private static String mapPath = "asset/map/CASTLE_ROOM.tmx";

    public CastleRoom() {
        super(MapFactory.MapType.CASTLE_ROOM, mapPath);

        addEnemy(EntityFactory.EntityName.RABITE6);
        addEnemy(EntityFactory.EntityName.RABITE7);
        addEnemy(EntityFactory.EntityName.RABITE9);
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

    private void addEnemy(EntityFactory.EntityName name) {
        Entity entity = EntityFactory.getInstance().getEntityByName(name);
        initEnemyPosition(entity);
        mapEntities.add(entity);
    }

    private void initEnemyPosition(Entity entity) {
        Vector2 position = new Vector2(0, 0);

        if (enemyStartPositions.containsKey(entity.getEntityConfig().getEntityID())) {
            position = enemyStartPositions.get(entity.getEntityConfig().getEntityID());
        }
        entity.sendMessage(Component.MESSAGE.INIT_START_POSITION, json.toJson(position));

        Object stored = ProfileManager.getInstance().getProperty(entity.getEntityConfig().getEntityID(), Object.class);
        if (stored instanceof EntityConfig entityConfig) {
            entity.setEntityConfig(entityConfig);
        }
    }
}
