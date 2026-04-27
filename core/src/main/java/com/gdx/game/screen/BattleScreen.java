package com.gdx.game.screen;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.math.Vector2;
import com.gdx.game.entities.EntityConfig;
import com.gdx.game.GdxGame;
import com.gdx.game.audio.AudioObserver;
import static com.gdx.game.audio.AudioObserver.AudioTypeEvent.BATTLE_THEME;
import com.gdx.game.battle.BattleHUD;
import com.gdx.game.battle.BattleInventoryUI;
import com.gdx.game.battle.BattleObserver;
import com.gdx.game.battle.BattleState;
import static com.gdx.game.common.UtilityClass.registerBonusClass;
import com.gdx.game.component.Component;
import com.gdx.game.entities.Entity;
import com.gdx.game.entities.EntityBonus;
import com.gdx.game.entities.EntityFactory;
import com.gdx.game.entities.player.PlayerHUD;
import com.gdx.game.inventory.InventoryObserver;
import com.gdx.game.inventory.InventoryUI;
import com.gdx.game.inventory.item.InventoryItemLocation;
import com.gdx.game.manager.ResourceManager;
import com.gdx.game.map.MapManager;
import com.gdx.game.profile.ProfileManager;
import com.gdx.game.screen.transition.effects.FadeOutTransitionEffect;
import com.gdx.game.screen.transition.effects.TransitionEffect;

public class BattleScreen extends BaseScreen implements BattleObserver {

    private InputMultiplexer multiplexer;
    private OrthographicCamera camera;
    private Stage battleStage;
    private MapManager mapManager;
    private PlayerHUD playerHUD;
    protected BattleHUD battleHUD;

    
    private void setupWinScreen() {
    ArrayList<TransitionEffect> effects = new ArrayList<>();
    effects.add(new FadeOutTransitionEffect(1f));

    setScreenWithTransition(
            (BaseScreen) gdxGame.getScreen(),
            new WinScreen(gdxGame, mapManager, resourceManager),
            effects
        );
    }


    private BattleState battleState;

    public BattleScreen(GdxGame gdxGame, PlayerHUD playerHUD_, MapManager mapManager_, ResourceManager resourceManager) {
        super(gdxGame, resourceManager);
        super.musicTheme = BATTLE_THEME;
        this.mapManager = mapManager_;
        this.playerHUD = playerHUD_;

        battleState = new BattleState();
        battleState.addObserver(this);
        playerHUD.setBattleState(battleState);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport = new StretchViewport(camera.viewportWidth, camera.viewportHeight, camera);
        battleStage = new Stage(viewport, gdxGame.getBatch());
        battleHUD = new BattleHUD(mapManager, battleStage, battleState);

        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(battleHUD.getBattleHUDStage());
    }

    /*public BattleHUD getBattleHUD() {
        return battleHUD;
    }*/

    /**
     * Compute a position that pushes the player away from the opponent they just fought.
     * Uses the enemy's position as the anchor and tries 4 directions to find a
     * collision-free spot. Returns null if the foe can't be found.
     */
    private Vector2 computePushedPosition() {
        Entity player = mapManager.getPlayer();
        EntityFactory.EntityName encounteredType = player.getEntityEncounteredType();
        if (encounteredType == null) {
            return null;
        }
        Entity foe = null;
        for (Entity e : mapManager.getCurrentMapEntities()) {
            if (encounteredType.toString().equals(e.getEntityConfig().getEntityID())) {
                foe = e;
                break;
            }
        }
        if (foe == null) {
            return null;
        }

        Vector2 foePos = foe.getCurrentPosition();
        if (foePos == null) {
            return null;
        }

        float pushDistance = 2.0f;
        // Try 4 cardinal directions: down, up, left, right
        float[][] offsets = {{0, -pushDistance}, {0, pushDistance}, {-pushDistance, 0}, {pushDistance, 0}};
        for (float[] offset : offsets) {
            Vector2 candidate = new Vector2(foePos.x + offset[0], foePos.y + offset[1]);
            if (!isPositionInCollision(candidate)) {
                return candidate;
            }
        }
        // Fallback: push down (even if in collision, better than staying on enemy)
        return new Vector2(foePos.x, foePos.y - pushDistance);
    }

    /**
     * Check if a unit-scaled position would collide with the map collision layer.
     */
    private boolean isPositionInCollision(Vector2 unitScaledPos) {
        com.badlogic.gdx.maps.MapLayer collisionLayer = mapManager.getCollisionLayer();
        if (collisionLayer == null) return false;

        // Convert to map pixel coordinates for collision check
        float mapX = unitScaledPos.x / com.gdx.game.map.Map.UNIT_SCALE;
        float mapY = unitScaledPos.y / com.gdx.game.map.Map.UNIT_SCALE;
        // Player bounding box dimensions (approx)
        float bboxW = Entity.FRAME_WIDTH * 0.7f;
        float bboxH = Entity.FRAME_HEIGHT * 0.5f;

        com.badlogic.gdx.math.Rectangle playerRect = new com.badlogic.gdx.math.Rectangle(mapX, mapY, bboxW, bboxH);

        for (com.badlogic.gdx.maps.MapObject obj : collisionLayer.getObjects()) {
            if (obj instanceof com.badlogic.gdx.maps.objects.RectangleMapObject) {
                com.badlogic.gdx.math.Rectangle rect = ((com.badlogic.gdx.maps.objects.RectangleMapObject) obj).getRectangle();
                if (playerRect.overlaps(rect)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void setupGameOver() {
        battleHUD.getDmgOpponentValLabel().setVisible(false);
        battleHUD.getDmgPlayerValLabel().setVisible(false);
        battleHUD.getBattleUI().setVisible(false);
        battleHUD.getBattleStatusUI().setVisible(false);
        battleStage.draw();

        ArrayList<TransitionEffect> effects = new ArrayList<>();
        effects.add(new FadeOutTransitionEffect(1f));
        setScreenWithTransition((BaseScreen) gdxGame.getScreen(), new GameOverScreen(gdxGame, mapManager, resourceManager), effects);
    }

    private void refreshStatus() {
        playerHUD.getStatusUI().setHPValue(battleHUD.getBattleStatusUI().getHPValue());
        playerHUD.getStatusUI().setMPValue(battleHUD.getBattleStatusUI().getMPValue());
    }

    private void refreshInventory() {
        Array<InventoryItemLocation> inventory = BattleInventoryUI.getInventory(battleHUD.getBattleInventoryUI().getInventorySlotTable());
        InventoryUI.populateInventory(playerHUD.getInventoryUI().getInventorySlotTable(), inventory, playerHUD.getInventoryUI().getDragAndDrop(), InventoryUI.PLAYER_INVENTORY, false);
    }

    private void refreshStats() {
        Array<EntityBonus> bonusClass = ProfileManager.getInstance().getProperty("bonusClass", Array.class);
        if (bonusClass != null && bonusClass.size > 0) {
            registerBonusClass();
        }
        Array<InventoryItemLocation> equipInventory = ProfileManager.getInstance().getProperty("playerEquipInventory", Array.class);
        playerHUD.getInventoryUI().resetEquipSlots();
        if (equipInventory != null && equipInventory.size > 0) {
            InventoryUI.populateInventory(playerHUD.getInventoryUI().getEquipSlotTable(), equipInventory, playerHUD.getInventoryUI().getDragAndDrop(), InventoryUI.PLAYER_INVENTORY, false);
        }
    }

    @Override
    public void onNotify(Entity entity, BattleEvent event) {
        switch (event) {
            case RESUME_OVER -> {
                boolean finalBossDefeated = isFinalBoss(entity);

                refreshStatus();
                refreshInventory();
                refreshStats();

                // Remove defeated enemy from map
                syncMapEntityAfterBattle(entity, true);

                mapManager.getPlayer().setEntityEncounteredType(null);
                ProfileManager.getInstance().saveProfile();

                if (finalBossDefeated) {
                    setupWinScreen();
                } else {
                    setScreenWithTransition(
                            (BaseScreen) gdxGame.getScreen(),
                            gdxGame.getGameScreen(),
                            new ArrayList<>()
                    );
                }
            }
            case OPPONENT_TURN_DONE -> {
                if (GameScreen.getGameState() == GameScreen.GameState.GAME_OVER) {
                    setupGameOver();
                }
            }
            case PLAYER_RUNNING -> {
                refreshStatus();
                refreshInventory();

                // Sync map entity HP with battle result (enemy still alive)
                syncMapEntityAfterBattle(entity, false);

                // Compute pushed position BEFORE clearing entityEncounteredType
                Vector2 pushedPos = computePushedPosition();

                mapManager.getPlayer().setEntityEncounteredType(null);

                // Set override position so SaveProfile writes pushed pos, not old graphics pos
                if (pushedPos != null) {
                    mapManager.setOverridePlayerPosition(pushedPos);
                    mapManager.getPlayer().sendMessage(Component.MESSAGE.INIT_START_POSITION,
                            new com.badlogic.gdx.utils.Json().toJson(pushedPos));
                }
                ProfileManager.getInstance().saveProfile();

                // 2-second cooldown: pass-through ALL foes (covers dense clusters like CASTLE_FINAL)
                com.gdx.game.entities.player.PlayerPhysicsComponent.startEscapeCooldown(2000L);

                setScreenWithTransition((BaseScreen) gdxGame.getScreen(), gdxGame.getGameScreen(), new ArrayList<>());
            }
            default -> {
            }
        }
    }

    /**
     * Sync the map entity's HP with the battle result.
     * If defeated, remove the entity from the map.
     */
    private void syncMapEntityAfterBattle(Entity battleEntity, boolean defeated) {
        if (battleEntity == null) return;
        String entityID = battleEntity.getEntityConfig().getEntityID();
        if (entityID == null) return;

        Entity mapEntity = null;
        for (Entity e : mapManager.getCurrentMapEntities()) {
            if (entityID.equals(e.getEntityConfig().getEntityID())) {
                mapEntity = e;
                break;
            }
        }
        if (mapEntity == null) return;

        if (defeated) {
            // Remove defeated enemy from map
            mapManager.removeMapEntity(mapEntity);
        } else {
            // Sync HP from battle entity to map entity
            String hp = battleEntity.getEntityConfig().getPropertyValue(
                    EntityConfig.EntityProperties.ENTITY_HEALTH_POINTS.toString());
            mapEntity.getEntityConfig().setPropertyValue(
                    EntityConfig.EntityProperties.ENTITY_HEALTH_POINTS.toString(), hp);
        }
    }

    @Override
    public void onNotify(String drop, InventoryObserver.InventoryEvent event) {

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(multiplexer);

        notify(AudioObserver.AudioCommand.MUSIC_LOAD, BATTLE_THEME);
        notify(AudioObserver.AudioCommand.MUSIC_PLAY_LOOP, BATTLE_THEME);
    }

    @Override
    public void render(float delta) {
        gdxGame.getBatch().setProjectionMatrix(camera.combined);

        gdxGame.getBatch().begin();

        // Làm tối battle background, không dùng ShapeRenderer để tránh lỗi overlay đỏ/đen.
        gdxGame.getBatch().setColor(0.45f, 0.45f, 0.45f, 1f);
        gdxGame.getBatch().draw(
        resourceManager.battleBackgroundMeadow,
        0,
        0,
        Gdx.graphics.getWidth(),
        Gdx.graphics.getHeight()
);

        // Reset màu để HUD, player, enemy không bị tối theo.
        gdxGame.getBatch().setColor(1f, 1f, 1f, 1f);
        gdxGame.getBatch().end();

        battleStage.act(Gdx.graphics.getDeltaTime());
        battleStage.draw();

        battleHUD.render(delta);
    }

    private static final String FINAL_BOSS_ENTITY_ID = "RABITE20";

    private boolean isFinalBoss(Entity entity) {
    return entity != null
            && entity.getEntityConfig() != null
            && FINAL_BOSS_ENTITY_ID.equals(entity.getEntityConfig().getEntityID());
}

    @Override
    public void dispose() {
        super.dispose();
        battleStage.dispose();
        battleHUD.dispose();
        playerHUD.dispose();
    }
}
