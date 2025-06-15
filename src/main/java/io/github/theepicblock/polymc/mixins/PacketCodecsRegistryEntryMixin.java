package io.github.theepicblock.polymc.mixins;


import io.github.theepicblock.polymc.impl.Util;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xyz.nucleoid.packettweaker.PacketContext;

@Mixin(targets = "net/minecraft/network/codec/PacketCodecs$33", priority = 500)
public abstract class PacketCodecsRegistryEntryMixin<T> {
    
    @ModifyArg(method = "encode(Lnet/minecraft/network/RegistryByteBuf;Lnet/minecraft/registry/entry/RegistryEntry;)V", 
               at = @At(value = "INVOKE", 
                        target = "Lnet/minecraft/network/codec/PacketCodec;encode(Lnet/minecraft/network/RegistryByteBuf;Ljava/lang/Object;)V"),
               index = 1)
    private Object polymer$changeData(Object val) {
        if (val instanceof RegistryEntry<?> entry) {
            var player = PacketContext.get();
            var map = Util.tryGetPolyMap(player);
            if (entry.value() instanceof SoundEvent soundEvent && !map.canReceiveEntry(Registries.SOUND_EVENT, soundEvent)) {
                // Return just the value to force direct encoding
                return entry.value();
            }
        }
        return val;
    }
}