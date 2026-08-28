package io.github.theepicblock.polymc.mixins.tag;

import io.github.theepicblock.polymc.impl.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagNetworkSerialization;

@Mixin(ClientboundUpdateTagsPacket.class)
public class SynchronizeTagsMixin {
    /*@ModifyArg(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;writeMap(Ljava/util/Map;Lnet/minecraft/network/codec/StreamEncoder;Lnet/minecraft/network/codec/StreamEncoder;)V"))
    public Map<ResourceKey<? extends Registry<?>>, TagNetworkSerialization.NetworkPayload> editTagMap(Map<ResourceKey<? extends Registry<?>>, TagNetworkSerialization.NetworkPayload> in) {
        if (Util.isPolyMapVanillaLike(PacketContext.get().getClientConnection())) {
            // Vanilla doesn't like it if it receives tags for registries that don't exist
            var newMap = new HashMap<ResourceKey<? extends Registry<?>>, TagNetworkSerialization.NetworkPayload>();
            in.forEach((key, tags) -> {
                if (Util.isVanilla(key.identifier())) {
                    newMap.put(key, tags);
                }
            });
            return newMap;
        } else {
            return in;
        }
    }*/ //TODO Method write doesn't seem to exist anymore?
}
