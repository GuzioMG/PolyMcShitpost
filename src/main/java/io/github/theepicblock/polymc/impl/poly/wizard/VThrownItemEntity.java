package io.github.theepicblock.polymc.impl.poly.wizard;

import io.github.theepicblock.polymc.api.wizard.PacketConsumer;
import io.github.theepicblock.polymc.mixins.wizards.ThrownItemEntityAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;

public abstract class VThrownItemEntity extends AbstractVirtualEntity {
    public VThrownItemEntity(Level idSource) {
        super(idSource);
    }

    public VThrownItemEntity(int id) {
        super(id);
    }

    public VThrownItemEntity(UUID uuid, Level idSource) {
        super(uuid, idSource);
    }

    public VThrownItemEntity(UUID uuid, int id) {
        super(uuid, id);
    }

    public void sendItem(PacketConsumer player, ItemStack item) {
        player.sendPacket(EntityUtil.createDataTrackerUpdate(
                this.id,
                ThrownItemEntityAccessor.polymc$getTrackedItem(),
                item
        ));
    }
}