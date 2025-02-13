package io.github.theepicblock.polymc.mixins.block.implementations;

import io.github.theepicblock.polymc.api.PolyMap;
import io.github.theepicblock.polymc.impl.Util;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.PackedIntegerArray;
import net.minecraft.util.collection.PaletteStorage;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.IdListPalette;
import net.minecraft.world.chunk.PalettedContainer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.stream.StreamSupport;

@Mixin(PalettedContainer.Data.class)
public class IdListImplementation {

    @Unique
    private static final int VANILLA_BIT_COUNT = MathHelper.ceilLog2((int) StreamSupport.stream(Block.STATE_IDS.spliterator(), false)
            .takeWhile(Util::isVanilla)
            .count());

    @Redirect(method = "writePacket", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/world/chunk/PalettedContainer$Data;storage:Lnet/minecraft/util/collection/PaletteStorage;"))
    private PaletteStorage getData(PalettedContainer.Data<?> container, PacketByteBuf buf)  {
        var originalStorage = container.storage();

        if (!(container.palette() instanceof IdListPalette<?>)) {
            return originalStorage;
        }

        var ctx = PacketContext.get();
        var polyMap = Util.tryGetPolyMap(ctx);

        if (!polyMap.isVanillaLikeMap()) {
            return originalStorage;
        }

        // Check if we're actually doing things with blocks
        if (!(container.palette().get(0) instanceof BlockState)) {
            return originalStorage;
        }

        var size = originalStorage.getSize();
        var data = new PackedIntegerArray(VANILLA_BIT_COUNT, size);
        var player = ctx.getPlayer();

        for (int i = 0; i < size; i++) {
            data.set(i, transform(originalStorage.get(i), polyMap, player));
        }

        return data;
    }

    @Unique
    private int transform(long in, PolyMap map, ServerPlayerEntity playerEntity) {

        // Assume the AIR blockstate will never be changed
        if (in == 0) {
            return 0;
        }

        return map.getClientStateRawId(Block.getStateFromRawId((int)in), playerEntity);
    }
}
