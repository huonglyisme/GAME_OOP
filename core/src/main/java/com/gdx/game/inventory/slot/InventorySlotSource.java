package com.gdx.game.inventory.slot;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.math.Vector2;
import com.gdx.game.inventory.item.InventoryItem;

public class InventorySlotSource extends Source {

    private DragAndDrop dragAndDrop;
    private InventorySlot sourceSlot;

    public InventorySlotSource(InventorySlot sourceSlot, DragAndDrop dragAndDrop) {
        super(sourceSlot.getTopInventoryItem());
        this.sourceSlot = sourceSlot;
        this.dragAndDrop = dragAndDrop;
    }

    @Override
    public Payload dragStart(InputEvent event, float x, float y, int pointer) {
        Payload payload = new Payload();

        Actor actor = getActor();
        if (!(actor instanceof InventoryItem sourceActor)) {
            return null;
        }

        InventorySlot source = (InventorySlot)actor.getParent();
        if (source == null) {
            return null;
        } else {
            sourceSlot = source;
        }

        sourceSlot.decrementItemCount(true);
        sourceActor.setVisible(false);

        // Keep the actual item in payload object; use a visual ghost while dragging.
        payload.setObject(sourceActor);

        Image dragGhost = new Image(sourceActor.getDrawable());
        dragGhost.setSize(sourceActor.getWidth(), sourceActor.getHeight());
        payload.setDragActor(dragGhost);

        // Use stage-space delta between pointer and item origin to avoid layout-space drift.
        if (event != null) {
            Vector2 itemOriginOnStage = sourceActor.localToStageCoordinates(new Vector2(0f, 0f));
            // Compensate one-cell horizontal drift observed in inventory layout.
            float offsetX = itemOriginOnStage.x - event.getStageX() + sourceSlot.getWidth();
            float offsetY = itemOriginOnStage.y - event.getStageY();
            dragAndDrop.setDragActorPosition(offsetX, offsetY);
        } else {
            dragAndDrop.setDragActorPosition(-x, -y);
        }

        return payload;
    }

    @Override
    public void dragStop (InputEvent event, float x, float y, int pointer, Payload payload, Target target) {
        InventoryItem sourceActor = (InventoryItem) payload.getObject();
        if (sourceActor == null) {
            return;
        }

        sourceActor.setVisible(true);

        if (target == null) {
            sourceSlot.add(sourceActor);
        }
    }

    public InventorySlot getSourceSlot() {
        return sourceSlot;
    }
}
