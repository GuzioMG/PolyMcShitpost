package io.github.theepicblock.polymc.mixins;


import io.github.theepicblock.polymc.impl.Util;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.nucleoid.packettweaker.PacketContext;

// Targets anonymous class in PacketCodecs.collection() method (line ~611)
// In 1.21.6, this is class $26 (was $18 in previous version)
@Mixin(targets = "net/minecraft/network/codec/PacketCodecs$26", priority = 800)
public abstract class PacketCodecsEntriesMixin<T> {
    
    // Due to generics erasure, the actual method signature uses Object parameters
    // Target the bridge method since we're redirecting a generic call
    @Redirect(method = "encode(Ljava/lang/Object;Ljava/lang/Object;)V", 
              at = @At(value = "INVOKE", 
                       target = "Lnet/minecraft/network/codec/PacketCodec;encode(Ljava/lang/Object;Ljava/lang/Object;)V"))
    private void polymer$changeData(PacketCodec codec, Object buf, Object val) {
        var player = PacketContext.get();
        var map = Util.tryGetPolyMap(player);
        var remapped = map.tryRemapping(val, player);
        
        codec.encode(buf, remapped);
    }
}