package io.github.theepicblock.polymc.mixins;


import io.github.theepicblock.polymc.impl.Util;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.nucleoid.packettweaker.PacketContext;

// AIDEV-TODO: Fix for 1.21.6 - anonymous class numbers have changed
// Was $18 in previous version, now appears to be $31 based on source analysis
// However, the mixin is failing to find the encode method - needs further investigation
@Mixin(targets = "net/minecraft/network/codec/PacketCodecs$31", priority = 800)
public abstract class PacketCodecsEntriesMixin<T> {
    
    @Redirect(method = "encode(Lio/netty/buffer/ByteBuf;Ljava/lang/Object;)V", 
              at = @At(value = "INVOKE", 
                       target = "Lnet/minecraft/network/codec/PacketCodec;encode(Lio/netty/buffer/ByteBuf;Ljava/lang/Object;)V"))
    private void polymer$changeData(PacketCodec<ByteBuf, T> codec, ByteBuf buf, T val) {
        var player = PacketContext.get();
        var map = Util.tryGetPolyMap(player);
        var remapped = map.tryRemapping(val, player);
        
        codec.encode(buf, (T) remapped);
    }
}