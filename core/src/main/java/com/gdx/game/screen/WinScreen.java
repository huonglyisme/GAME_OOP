package com.gdx.game.screen;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.gdx.game.GdxGame;
import com.gdx.game.audio.AudioObserver;
import static com.gdx.game.audio.AudioObserver.AudioTypeEvent.MENU_THEME;
import com.gdx.game.component.Component;
import com.gdx.game.manager.ResourceManager;
import com.gdx.game.map.MapManager;
import com.gdx.game.profile.ProfileManager;
import com.gdx.game.screen.transition.effects.FadeOutTransitionEffect;
import com.gdx.game.screen.transition.effects.TransitionEffect;

public class WinScreen extends BaseScreen {

    private static final Color BACKGROUND_COLOR = new Color(0.02f, 0.03f, 0.08f, 1f);

    private final MapManager mapManager;
    private final Stage winStage;
    private final Table rootTable;

    public WinScreen(GdxGame gdxGame, ResourceManager resourceManager) {
        this(gdxGame, null, resourceManager);
    }

    public WinScreen(GdxGame gdxGame, MapManager mapManager, ResourceManager resourceManager) {
        super(gdxGame, resourceManager);
        super.musicTheme = MENU_THEME;
        this.mapManager = mapManager;

        winStage = new Stage(new ScreenViewport(), gdxGame.getBatch());
        rootTable = createTable();
        rootTable.setFillParent(true);
        buildWinUI();
    }

    private void buildWinUI() {
        Label titleLabel = new Label("VICTORY", ResourceManager.skin);
        titleLabel.setAlignment(Align.center);
        titleLabel.setFontScale(2.2f);
        titleLabel.setColor(Color.GOLD);

        Label messageLabel = new Label("You cleared the final battle!", ResourceManager.skin);
        messageLabel.setAlignment(Align.center);
        messageLabel.setFontScale(1.1f);
        messageLabel.setColor(Color.WHITE);

        TextButton backToMenuButton = new TextButton("Back to Menu", ResourceManager.skin);
        backToMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                prepareReturnToMenu();

                ArrayList<TransitionEffect> effects = new ArrayList<>();
                effects.add(new FadeOutTransitionEffect(1f));
                setScreenWithTransition((BaseScreen) gdxGame.getScreen(), gdxGame.getMenuScreen(), effects);
            }
        });

        TextButton exitButton = new TextButton("Exit", ResourceManager.skin);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        Table buttonTable = new Table();
        buttonTable.add(backToMenuButton).width(220).height(55).padRight(20);
        buttonTable.add(exitButton).width(140).height(55);

        rootTable.center();
        rootTable.add(titleLabel).padBottom(20);
        rootTable.row();
        rootTable.add(messageLabel).padBottom(35);
        rootTable.row();
        rootTable.add(buttonTable);
    }

    private void prepareReturnToMenu() {
        if (mapManager != null && mapManager.getPlayer() != null) {
            mapManager.getPlayer().setEntityEncounteredType(null);
            mapManager.getPlayer().sendMessage(Component.MESSAGE.RESET_POSITION);
        }

        ProfileManager.getInstance().saveProfile();
    }

    @Override
    public void show() {
        if (rootTable.getStage() == null) {
            winStage.addActor(rootTable);
        }
        Gdx.input.setInputProcessor(winStage);

        notify(AudioObserver.AudioCommand.MUSIC_LOAD, MENU_THEME);
        notify(AudioObserver.AudioCommand.MUSIC_PLAY_LOOP, MENU_THEME);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(BACKGROUND_COLOR.r, BACKGROUND_COLOR.g, BACKGROUND_COLOR.b, BACKGROUND_COLOR.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        winStage.act(delta);
        winStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        winStage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        super.dispose();
        winStage.dispose();
    }
}
