package io.github.theepicblock.polymc.mixins.component.transforms;


import io.github.theepicblock.polymc.impl.Util;
import io.github.theepicblock.polymc.impl.mixin.TransformingComponent;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.component.ItemAttributeModifiers;

@Mixin(ItemAttributeModifiers.class)
public abstract class AttributeModifiersComponentMixin implements TransformingComponent {

    @Shadow @Final private List<ItemAttributeModifiers.Entry> modifiers;

    @Override
    public Object polymc$getTransformed(ServerPlayer player) {
        if (!polymc$requireModification(player)) {
            return this;
        }

        var list = new ArrayList<ItemAttributeModifiers.Entry>();
        var map = Util.tryGetPolyMap(player);
        for (var entry : this.modifiers) {
            if (map.canReceiveRegistryEntry(BuiltInRegistries.ATTRIBUTE, entry.attribute())) {
                list.add(entry);
            }
        }

        return new ItemAttributeModifiers(list);
    }

    @Override
    public boolean polymc$requireModification(ServerPlayer context) {
        var map = Util.tryGetPolyMap(context);
        for (var entry : this.modifiers) {
            if (!map.canReceiveRegistryEntry(BuiltInRegistries.ATTRIBUTE, entry.attribute())) {
                return true;
            }
        }
        return false;
    }
}
