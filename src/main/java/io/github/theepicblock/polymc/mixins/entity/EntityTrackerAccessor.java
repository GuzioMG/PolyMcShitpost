package io.github.theepicblock.polymc.mixins.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.server.network.EntityTrackerEntry; //!UNKNOWN
import net.minecraft.server.network.PlayerAssociatedNetworkHandler; //!UNKNOWN
import net.minecraft.server.level.world.ServerChunkLoadingManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(ServerChunkLoadingManager.EntityTracker.class)
public interface EntityTrackerAccessor {
    @Accessor
    EntityTrackerEntry getEntry();

    @Accessor
    Set<PlayerAssociatedNetworkHandler> getListeners();

    @Accessor
    Entity getEntity();
}
