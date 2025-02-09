package io.github.theepicblock.polymc.mixins;


import io.github.theepicblock.polymc.impl.Util;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import xyz.nucleoid.packettweaker.PacketContext;

@Mixin(targets = "net/minecraft/network/codec/PacketCodecs$18", priority = 500)
public abstract class PacketCodecsRegistryMixin {
    @SuppressWarnings({"rawtypes", "ShadowModifiers"})
    @Shadow
    @Final
    private RegistryKey field_53746;

    @ModifyVariable(method = "encode(Lnet/minecraft/network/RegistryByteBuf;Ljava/lang/Object;)V", at = @At("HEAD"), argsOnly = true)
    private Object polymer$changeData(Object val, RegistryByteBuf buf) {
        var player = PacketContext.get();
        var map = Util.tryGetPolyMap(player);

        if (val instanceof RegistryEntry<?> registryEntry) {
            var value = registryEntry.value();
            var out = map.tryRemapping(value, player);
            if (value != out) {
                return buf.getRegistryManager().getOrThrow(this.field_53746).getEntry(out);
            }
            return val;
        }

        return map.tryRemapping(val, player);
    }
}