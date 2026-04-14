package com.gdx.game.inventory.slot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.gdx.game.inventory.item.InventoryItem;
import com.gdx.game.manager.ResourceManager;
import com.gdx.game.GdxRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(GdxRunner.class)
public class InventorySlotTest {

    @BeforeEach
    void init() {
        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = mock(GL20.class);
        new ResourceManager();
    }

    @Test
    public void testInventorySlot_ShouldSucceed() {
        InventorySlot inventorySlot = new InventorySlot();

        assertThat(inventorySlot).isNotNull();
        assertThat(inventorySlot.hasItem()).isFalse();
    }

    @Test
    public void testInventorySlotSourceDragStart_ShouldKeepCursorGrabPoint() {
        InventorySlot inventorySlot = new InventorySlot();
        InventoryItem item = new InventoryItem();
        item.setSize(16f, 16f);
        inventorySlot.add(item);

        DragAndDrop dragAndDrop = spy(new DragAndDrop());
        InventorySlotSource source = new InventorySlotSource(inventorySlot, dragAndDrop);

        source.dragStart(null, 3f, 2f, 0);

        verify(dragAndDrop).setDragActorPosition(-3f, -2f);
    }

    @Test
    public void testInventorySlotDropBackToSameSlot_ShouldNotCreatePhantomStack() {
        InventorySlot inventorySlot = new InventorySlot();

        InventoryItem item = new InventoryItem();
        item.setSize(16f, 16f);
        inventorySlot.add(item);

        DragAndDrop dragAndDrop = new DragAndDrop();
        InventorySlotSource source = new InventorySlotSource(inventorySlot, dragAndDrop);
        DragAndDrop.Payload payload = source.dragStart(null, 8f, 8f, 0);

        InventorySlotTarget target = new InventorySlotTarget(inventorySlot);
        target.drop(source, payload, 8f, 8f, 0);

        assertThat(inventorySlot.getNumItems()).isEqualTo(1);
        assertThat(inventorySlot.hasItem()).isTrue();
    }

    @Test
    public void testInventorySlotDropToEquipmentSlot_ShouldMoveItem() {
        InventorySlot sourceSlot = new InventorySlot();
        InventoryItem weapon = new InventoryItem();
        weapon.setItemAttributes(2);
        weapon.setItemUseType(InventoryItem.ItemUseType.WEAPON_ONEHAND.getValue());
        sourceSlot.add(weapon);

        InventorySlot equipSlot = new InventorySlot(InventoryItem.ItemUseType.WEAPON_ONEHAND.getValue(), new Image());

        DragAndDrop dragAndDrop = new DragAndDrop();
        InventorySlotSource source = new InventorySlotSource(sourceSlot, dragAndDrop);
        DragAndDrop.Payload payload = source.dragStart(null, 4f, 4f, 0);

        InventorySlotTarget target = new InventorySlotTarget(equipSlot);
        target.drop(source, payload, 4f, 4f, 0);

        assertThat(sourceSlot.getNumItems()).isEqualTo(0);
        assertThat(equipSlot.getNumItems()).isEqualTo(1);
        assertThat(equipSlot.getTopInventoryItem()).isSameAs(weapon);
    }

    @Test
    public void testInventorySlotDropDifferentNonStackableItems_ShouldSwapWithoutDuplication() {
        InventorySlot sourceSlot = new InventorySlot();
        InventoryItem weapon = new InventoryItem();
        weapon.setItemAttributes(2);
        weapon.setItemTypeID(InventoryItem.ItemTypeID.WEAPON01);
        weapon.setItemUseType(InventoryItem.ItemUseType.WEAPON_ONEHAND.getValue());
        sourceSlot.add(weapon);

        InventorySlot targetSlot = new InventorySlot();
        InventoryItem shield = new InventoryItem();
        shield.setItemAttributes(2);
        shield.setItemTypeID(InventoryItem.ItemTypeID.SHIELD01);
        shield.setItemUseType(InventoryItem.ItemUseType.ARMOR_SHIELD.getValue());
        targetSlot.add(shield);

        DragAndDrop dragAndDrop = new DragAndDrop();
        InventorySlotSource source = new InventorySlotSource(sourceSlot, dragAndDrop);
        DragAndDrop.Payload payload = source.dragStart(null, 4f, 4f, 0);

        InventorySlotTarget target = new InventorySlotTarget(targetSlot);
        target.drop(source, payload, 4f, 4f, 0);

        assertThat(sourceSlot.getNumItems()).isEqualTo(1);
        assertThat(targetSlot.getNumItems()).isEqualTo(1);
        assertThat(sourceSlot.getTopInventoryItem().getItemTypeID()).isEqualTo(InventoryItem.ItemTypeID.SHIELD01);
        assertThat(targetSlot.getTopInventoryItem().getItemTypeID()).isEqualTo(InventoryItem.ItemTypeID.WEAPON01);
    }
}
