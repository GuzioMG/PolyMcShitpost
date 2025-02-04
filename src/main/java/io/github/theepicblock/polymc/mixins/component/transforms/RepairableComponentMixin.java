package io.github.theepicblock.polymc.mixins.component.transforms;

import io.github.theepicblock.polymc.impl.Util;
import io.github.theepicblock.polymc.impl.mixin.TransformingComponent;
import net.minecraft.component.type.RepairableComponent;
import net.minecraft.item.Item;
import net.minecraft.registry.entry.RegistryEntryList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.nucleoid.packettweaker.PacketContext;

@Mixin(RepairableComponent.class)
public abstract class RepairableComponentMixin implements TransformingComponent {

    @Shadow @Final
    private RegistryEntryList<Item> items;

    @Override
    public Object polymc$getTransformed(PacketContext player) {

        if (!polymc$requireModification(player)) {
            return this;
        }

        return new RepairableComponent(Util.transformRegistryEntryList(this.items, player));
    }

    @Override
    public boolean polymc$requireModification(PacketContext player) {
        return !Util.canReceiveList(this.items, player);
    }
}
