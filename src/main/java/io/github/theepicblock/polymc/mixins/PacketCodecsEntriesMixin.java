package io.github.theepicblock.polymc.mixins;


import io.github.theepicblock.polymc.impl.Util;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import xyz.nucleoid.packettweaker.PacketContext;

// Targets anonymous class in PacketCodecs.collection() method (line ~611)
// In 1.21.6, this is class $17 (was $26 in refmap, but runtime shows $17)
@Mixin(targets = "net/minecraft/network/codec/PacketCodecs$17", priority = 800)
public abstract class PacketCodecsEntriesMixin<T> {
    
    // Use @ModifyVariable to modify the value before it's passed to the codec
    // This approach is more compatible with generic method signatures
    @ModifyVariable(method = "encode(Ljava/lang/Object;Ljava/lang/Object;)V", 
                    at = @At("HEAD"), 
                    argsOnly = true, 
                    index = 2)
    private Object polymer$changeData(Object val) {
        var player = PacketContext.get();
        var map = Util.tryGetPolyMap(player);
        return map.tryRemapping(val, player);
    }
}