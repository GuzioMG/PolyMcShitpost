package io.github.theepicblock.polymc.mixins;


import io.github.theepicblock.polymc.impl.Util;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import xyz.nucleoid.packettweaker.PacketContext;

// Targets anonymous class in PacketCodecs.registry() method (line ~804)
// In 1.21.6, this is class $22 (was $31 in refmap, but runtime shows $22)
@Mixin(targets = "net/minecraft/network/codec/PacketCodecs$22", priority = 500)
public abstract class PacketCodecsRegistryMixin<T> {
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    // Due to generics erasure, the actual method signature uses Object parameters
    // Target the bridge method for consistency
    @ModifyVariable(method = "encode(Ljava/lang/Object;Ljava/lang/Object;)V", at = @At("HEAD"), argsOnly = true, index = 1)
    private Object polymer$changeData(Object val) {
        var player = PacketContext.get();
        if (player == null) {
            return val;
        }
        var map = Util.tryGetPolyMap(player);
        if (map == null) {
            return val;
        }

        // Handle RegistryEntry specially
        if (val instanceof RegistryEntry registryEntry) {
            var value = registryEntry.value();
            var out = map.tryRemapping(value, player);

            if (value == out) {
                // Value was not remapped, return the original entry.
                return val;
            }

            // Return a direct entry with the remapped value
            return RegistryEntry.of(out);
        }

        // For non-RegistryEntry values, just remap directly
        return map.tryRemapping(val, player);
    }
}