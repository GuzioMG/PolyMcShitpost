package io.github.theepicblock.polymc.mixins.wizards.block;

import io.github.theepicblock.polymc.PolyMc;
import io.github.theepicblock.polymc.impl.misc.WatchListener;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkHolder.class)
public abstract class ChunkHolderMixin {
    @Shadow public abstract LevelChunk getTickingChunk();
    @Shadow private int ticketLevel;

    // Workaround for Minecraft 1.21.10 vanilla bug where player chunks get unloaded shortly after login
    // Root cause: New PrepareSpawnTask system uses PLAYER_SPAWN tickets (20 tick expiry) but doesn't create
    // permanent tickets. There's a race condition where PLAYER_SPAWN expires before PLAYER_SIMULATION is added.
    // In 1.21.6, method_72079() created UNKNOWN tickets during login. In 1.21.10, loadChunks() doesn't create any tickets.
    // We check if players are actually in the chunk before removing wizards to prevent the symptom.
    @Inject(method = "setTicketLevel", at = @At("HEAD"))
    private void onLevelSet(int newLevel, CallbackInfo ci) {
        // Only remove wizards when chunk transitions from accessible to inaccessible
        if (ChunkLevel.isLoaded(this.ticketLevel) && !ChunkLevel.isLoaded(newLevel)) {
            LevelChunk chunk = this.getTickingChunk();
            if (chunk != null && chunk.getLevel() instanceof ServerLevel serverWorld) {
                var chunkPos = chunk.getPos();

                // Check if any players are currently in this chunk
                boolean hasPlayersInChunk = serverWorld.players().stream()
                    .anyMatch(player -> player.chunkPosition().equals(chunkPos));

                if (hasPlayersInChunk) {
                    PolyMc.LOGGER.warn("ChunkHolder.setLevel: NOT removing wizards because players are in chunk: old=" + this.ticketLevel + " new=" + newLevel + " chunk=" + chunkPos);
                    return;
                }

                ((WatchListener)chunk).polymc$removeAllPlayers();
            }
        }
    }
}
