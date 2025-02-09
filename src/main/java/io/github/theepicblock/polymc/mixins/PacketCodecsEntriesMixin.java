package io.github.theepicblock.polymc.mixins;


import io.github.theepicblock.polymc.impl.Util;
import io.netty.buffer.ByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import xyz.nucleoid.packettweaker.PacketContext;

@Mixin(targets = "net/minecraft/network/codec/PacketCodecs$17", priority = 800)
public abstract class PacketCodecsEntriesMixin {

    @ModifyVariable(method = "encode(Lio/netty/buffer/ByteBuf;Ljava/lang/Object;)V", at = @At("HEAD"), argsOnly = true)
    private Object polymer$changeData(Object val, ByteBuf buf) {
        var player = PacketContext.get();
        var map = Util.tryGetPolyMap(player);
        return map.tryRemapping(val, player);
    }
}