package io.github.theepicblock.polymc.api.wizard;

import io.github.theepicblock.polymc.impl.poly.wizard.AbstractVirtualEntity;
import io.github.theepicblock.polymc.impl.poly.wizard.EntityUtil;
import io.github.theepicblock.polymc.mixins.wizards.ItemEntityAccessor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class VItem extends AbstractVirtualEntity {
    public VItem(Level idSource) {
        super(idSource);
    }

    public VItem(int id) {
        super(id);
    }

    public VItem(UUID uuid, Level idSource) {
        super(uuid, idSource);
    }

    public VItem(UUID uuid, int id) {
        super(uuid, id);
    }

    @Override
    public EntityType<?> getEntityType() {
        return EntityTypes.ITEM;
    }

    public void sendItem(PacketConsumer player, ItemStack item) {
        player.sendPacket(EntityUtil.createDataTrackerUpdate(
                this.id,
                ItemEntityAccessor.getStackTracker(),
                item
        ));
    }
}