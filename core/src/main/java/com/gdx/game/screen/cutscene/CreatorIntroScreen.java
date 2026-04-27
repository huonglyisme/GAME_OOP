package com.gdx.game.screen.cutscene;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.gdx.game.GdxGame;
import com.gdx.game.animation.AnimatedImage;
import com.gdx.game.entities.Entity;
import com.gdx.game.entities.EntityFactory;
import com.gdx.game.manager.ResourceManager;
import com.gdx.game.map.MapFactory;

public class CreatorIntroScreen extends CutSceneBaseScreen {
    private Action setupScene01;
    private AnimatedImage creator;

    public CreatorIntroScreen(GdxGame game, ResourceManager resourceManager) {
        super(game, resourceManager);

        creator = getAnimatedImage(EntityFactory.EntityType.THIEF);
        creator.setName("Storyteller");

        setupScene01 = new RunnableAction() {
            @Override
            public void run() {
                hideMessage();
                mapManager.loadMap(MapFactory.MapType.VILLAGE);
                mapManager.disableCurrentMapMusic();
                setCameraPosition(17, 10);

                creator.setCurrentAnimation(Entity.AnimationType.WALK_UP);
                creator.setVisible(true);
                creator.setPosition(17, 0);
            }
        };

        getStage().addActor(creator);
    }

    Action getCutsceneAction() {
        setupScene01.reset();
        getSwitchScreenAction().reset();

        return Actions.sequence(
                Actions.addAction(setupScene01),
                Actions.delay(1),
                Actions.addAction(Actions.moveTo(17, 10, 5, Interpolation.linear), creator),
                Actions.delay(Float.parseFloat("2.5")),
                Actions.addAction(Actions.run(() -> creator.setCurrentAnimation(Entity.AnimationType.IMMOBILE))),
                Actions.run(() -> showMessage(creator, "Long ago, the world of Aethermere was nurtured by the Mana Tree — the source of all life.")),
                Actions.delay(5),
                Actions.run(() -> showMessage(creator, "One day, the Tree shattered. The cataclysm known as 'Manafall' twisted countless creatures into demons.")),
                Actions.delay(5),
                Actions.run(() -> showMessage(creator, "Years later, a child was born with mana in their blood — a rare blessing in a broken world.")),
                Actions.delay(5),
                Actions.run(() -> showMessage(creator, "That child is you. Your village calls. Your journey begins now.")),
                Actions.delay(4),
                Actions.after(getSwitchScreenAction())
        );
    }
}
