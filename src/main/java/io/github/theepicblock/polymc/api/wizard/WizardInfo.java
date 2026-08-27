package io.github.theepicblock.polymc.api.wizard;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.server.level.ServerLevel; //PREVIOUSLY: ServerWorld
import net.minecraft.util.math.BlockPos; //!UNKNOWN
import net.minecraft.util.math.Vec3d; //!UNKNOWN
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface WizardInfo {
    @NotNull Vec3d getPosition();

    @NotNull Vec3d getPosition(UpdateInfo info);

    @Nullable BlockPos getBlockPos();

    @Nullable BlockState getBlockState();

    @Nullable BlockEntity getBlockEntity();

    @Nullable ServerWorld getWorld();
}
