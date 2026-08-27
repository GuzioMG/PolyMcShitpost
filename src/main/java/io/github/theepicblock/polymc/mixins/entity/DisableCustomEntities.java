package io.github.theepicblock.polymc.mixins.entity;

import io.github.theepicblock.polymc.api.misc.PolyMapProvider;
import io.github.theepicblock.polymc.impl.mixin.EntityTrackerEntryDuck;
import net.minecraft.network.listener.ClientPlayPacketListener; //!UNKNOWN
import net.minecraft.network.packet.Packet; //!UNKNOWN
import net.minecraft.server.network.EntityTrackerEntry; //!UNKNOWN
import net.minecraft.server.network.ServerPlayerEntity; //!UNKNOWN
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;

@Mixin(EntityTrackerEntry.class)
public class DisableCustomEntities {
    @Redirect(method = "startTracking", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/EntityTrackerEntry;sendPackets(Lnet/minecraft/server/network/ServerPlayerEntity;Ljava/util/function/Consumer;)V"))
    public void polymc$maybeBlockSpawnPacket(EntityTrackerEntry instance, ServerPlayerEntity player, Consumer<Packet<ClientPlayPacketListener>> sender) {

        var map = PolyMapProvider.getPolyMap(player);
        if (map == null) return;

        if (((EntityTrackerEntryDuck)this).polymc$getWizards().get(map) == null) {
            instance.sendPackets(player, sender);
        }
    }
}
