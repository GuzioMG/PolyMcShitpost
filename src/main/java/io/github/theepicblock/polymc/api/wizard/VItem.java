package io.github.theepicblock.polymc.api.wizard;

import io.github.theepicblock.polymc.impl.poly.wizard.AbstractVirtualEntity;
import io.github.theepicblock.polymc.impl.poly.wizard.EntityUtil;
import io.github.theepicblock.polymc.mixins.wizards.ItemEntityAccessor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.ItemStack;

public class VItem extends AbstractVirtualEntity {
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