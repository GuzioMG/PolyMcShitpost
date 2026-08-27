package io.github.theepicblock.polymc.impl.poly.wizard;

import io.github.theepicblock.polymc.api.wizard.UpdateInfo;
import io.github.theepicblock.polymc.api.wizard.WizardInfo;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel; //PREVIOUSLY: ServerWorld
import net.minecraft.util.math.BlockPos; //!UNKNOWN
import net.minecraft.util.math.Vec3d; //!UNKNOWN
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntityWizardInfo implements WizardInfo {
    protected final Entity source;

    public EntityWizardInfo(Entity source) {
        this.source = source;
    }

    @Override
    public @NotNull Vec3d getPosition() {
        return source.getPos();
    }

    @Override
    public @NotNull Vec3d getPosition(UpdateInfo info) {
        return source.getLerpedPos(info.getTickDelta());
    }

    @Override
    public @Nullable BlockPos getBlockPos() {
        // Doesn't make sense for an entity
        return null;
    }

    @Override
    public @Nullable BlockState getBlockState() {
        // Doesn't make sense for an entity
        return null;
    }

    @Override
    public @Nullable BlockEntity getBlockEntity() {
        // Doesn't make sense for an entity
        return null;
    }

    @Override
    public @Nullable ServerWorld getWorld() {
        return (ServerWorld)source.getWorld();
    }
}
