package com.gdx.game.battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.gdx.game.GdxRunner;
import com.gdx.game.inventory.item.InventoryItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(GdxRunner.class)
public class DropTableManagerTest {

    private final Json json = new Json();

    @BeforeEach
    void init() {
        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = mock(GL20.class);
        DropTableManager.clearCache();
    }

    @Test
    public void dropTables_ShouldUseValidItemTypeAndProbabilityRange() {
        java.util.ArrayList<JsonValue> list = json.fromJson(java.util.ArrayList.class, Gdx.files.internal("scripts/drop_tables.json"));

        Set<String> tableIds = new HashSet<>();
        for (JsonValue jsonVal : list) {
            DropTableManager.DropTableConfig config = json.readValue(DropTableManager.DropTableConfig.class, jsonVal);
            assertThat(config.getDropTableID()).isNotBlank();
            assertThat(tableIds.add(config.getDropTableID())).isTrue();

            assertThat(config.getDrops()).isNotNull();
            config.getDrops().forEach(drop -> {
                assertThat(drop.getProbability()).isBetween(0.0f, 1.0f);
                assertThat(InventoryItem.ItemTypeID.valueOf(drop.getItemTypeID())).isNotNull();
            });
        }
    }

    @Test
    public void dropTableManager_ShouldReturnEmptyForUnknownTable() {
        assertThat(DropTableManager.getDropsForTable("UNKNOWN_TABLE")).isEmpty();
    }
}
