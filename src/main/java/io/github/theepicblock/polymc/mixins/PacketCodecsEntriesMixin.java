package io.github.theepicblock.polymc.mixins;


import io.github.theepicblock.polymc.impl.Util;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.nucleoid.packettweaker.PacketContext;

@Mixin(targets = "net/minecraft/network/codec/PacketCodecs$18", priority = 800)
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