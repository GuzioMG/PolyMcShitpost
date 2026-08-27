package io.github.theepicblock.polymc.mixins;


import eu.pb4.polymer.rsm.api.RegistrySyncUtils;
import io.github.theepicblock.polymc.impl.Util;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.core.registries.Registries;
import net.minecraft.registry.entry.RegistryEntry; //!UNKNOWN
import net.minecraft.sounds.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import xyz.nucleoid.packettweaker.PacketContext;

@Mixin(targets = "net/minecraft/network/codec/PacketCodecs$24", priority = 500)
public abstract class PacketCodecsRegistryEntryMixin {
    @ModifyVariable(method = "encode(Lnet/minecraft/network/RegistryByteBuf;Lnet/minecraft/registry/entry/RegistryEntry;)V", at = @At("HEAD"), argsOnly = true)
    private RegistryEntry<?> polymer$changeData(RegistryEntry<?> val, RegistryByteBuf buf) {
        var player = PacketContext.get();
        var map = Util.tryGetPolyMap(player);
        if (val.value() instanceof SoundEvent soundEvent && !map.canReceiveEntry(Registries.SOUND_EVENT, soundEvent)) {
            return RegistryEntry.of(val.value());
        }

        return val;
    }

}