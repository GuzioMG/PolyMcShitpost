package io.github.theepicblock.polymc.mixins;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerCommonNetworkHandler; //!UNKNOWN
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerCommonNetworkHandler.class)
public interface SCNetworkHandlerAccessor {
    @Accessor
    ClientConnection getConnection();
}
