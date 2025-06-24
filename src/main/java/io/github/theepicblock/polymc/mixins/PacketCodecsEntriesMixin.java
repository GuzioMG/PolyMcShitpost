package io.github.theepicblock.polymc.mixins;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.theepicblock.polymc.impl.Util;
import io.github.theepicblock.polymc.impl.misc.TransformingPacketCodec;
import io.github.theepicblock.polymc.impl.mixin.RegistryEntryRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.state.property.Property;
import net.minecraft.util.collection.IndexedIterable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import xyz.nucleoid.packettweaker.PacketContext;


@Mixin(targets = "net/minecraft/network/codec/PacketCodecs", priority = 800)
public interface PacketCodecsEntriesMixin {
    @ModifyExpressionValue(method = "entryOf", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/codec/PacketCodecs;indexed(Ljava/util/function/IntFunction;Ljava/util/function/ToIntFunction;)Lnet/minecraft/network/codec/PacketCodec;"))
    private static <T> PacketCodec<ByteBuf, T> polymer$changeData(PacketCodec<ByteBuf, T> original, @Local(argsOnly = true) IndexedIterable<T> iterable) {
        if (iterable instanceof Registry<T> registry) {
            return TransformingPacketCodec.encodeOnly(original, (byteBuf, val) -> {
                var player = PacketContext.get();
                var map = Util.tryGetPolyMap(player);

                return (T) map.tryRemapping(val, player);
            });
        }
        if (iterable instanceof RegistryEntryRegistry<?> tmp) {
            //noinspection unchecked
            var registry = (Registry<Object>) tmp.polymc$getRegistry();
            return TransformingPacketCodec.encodeOnly(original, (byteBuf, val) -> {
                var player = PacketContext.get();

                var map = Util.tryGetPolyMap(player);

                return (T) registry.getEntry(map.tryRemapping(((RegistryEntry) val).value(), player));
            });
        } else if (iterable == Block.STATE_IDS) {
            return TransformingPacketCodec.encodeOnly(original, (byteBuf, val) -> {
                var player = PacketContext.get();
                var map = Util.tryGetPolyMap(player);

                //noinspection unchecked
                return  (T) map.getClientState((BlockState) val, player.getPlayer());
            });
        }

        return original;
    }

    @Unique
    private static String stringify(Object o) {
        var builder = new StringBuilder();
        var state = (BlockState) o;
        builder.append(Registries.BLOCK.getId(state.getBlock()));

        if (!state.getBlock().getStateManager().getProperties().isEmpty()) {
            builder.append("[");
            var iterator = state.getBlock().getStateManager().getProperties().iterator();

            while (iterator.hasNext()) {
                var property = iterator.next();
                builder.append(property.getName());
                builder.append("=");
                builder.append(((Property) property).name(state.get(property)));

                if (iterator.hasNext()) {
                    builder.append(",");
                }
            }
            builder.append("]");
        }
        return builder.toString();
    }
}