package io.github.theepicblock.polymc.mixins;

import io.github.theepicblock.polymc.impl.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import static io.github.theepicblock.polymc.impl.Util.getPlayerStub;

@Mixin(RegistryFixedCodec.class)
public class RegistryFixedCodecMixin {
    @SuppressWarnings("unchecked")
    @ModifyVariable(
            method = "encode(Lnet/minecraft/core/Holder;Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;",
            at = @At("HEAD"),
            argsOnly = true
    )
    private Holder<?> swapEntry(Holder<?> input) {
        var player = getPlayerStub();
        try {
            var map = Util.tryGetPolyMap(player);
            if (input.value() instanceof Item item) return BuiltInRegistries.ITEM.wrapAsHolder(map.getClientItem(item.getDefaultInstance(), player, null).getItem());
            else if (input.value() instanceof Block item && map.getBlockPoly(item) != null) return BuiltInRegistries.BLOCK.wrapAsHolder(map.getBlockPoly(item).getClientBlock(item.defaultBlockState()).getBlock());
            else if (input.value() instanceof SoundEvent event && !Util.isVanilla(BuiltInRegistries.SOUND_EVENT.getKey(event))) return BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY);
            else if (input.value() instanceof EntityType<?> && !map.canReceiveRegistryEntry(BuiltInRegistries.ENTITY_TYPE, (Holder<EntityType<?>>) input)) return EntityTypes.MARKER.builtInRegistryHolder();
            else if (input.value() instanceof Attribute && !map.canReceiveRegistryEntry(BuiltInRegistries.ATTRIBUTE, (Holder<Attribute>) input)) return Attributes.SPAWN_REINFORCEMENTS_CHANCE;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return input;
    }
}