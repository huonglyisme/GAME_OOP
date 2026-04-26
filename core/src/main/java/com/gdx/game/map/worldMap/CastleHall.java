package com.gdx.game.map.worldMap;

import com.gdx.game.audio.AudioObserver;
import com.gdx.game.map.Map;
import com.gdx.game.map.MapFactory;

import static com.gdx.game.audio.AudioObserver.AudioTypeEvent.VILLAGE_THEME;

public class CastleHall extends Map {

    private static String mapPath = "asset/map/CASTLE_HALL.tmx";

    public CastleHall() {
        super(MapFactory.MapType.CASTLE_HALL, mapPath);
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
}
