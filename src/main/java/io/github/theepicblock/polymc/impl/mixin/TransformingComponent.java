package io.github.theepicblock.polymc.impl.mixin;

import net.minecraft.server.level.ServerPlayer;

public interface TransformingComponent {
    static boolean requireTransform(Object object, ServerPlayer player) {
        return object instanceof TransformingComponent t && t.polymc$requireModification(player);
    }

    static boolean requireTransformForTooltip(Object object, ServerPlayer player) {
        return object instanceof TransformingComponent t && t.polymc$requireModification(player);
    }

    Object polymc$getTransformed(ServerPlayer context);
    boolean polymc$requireModification(ServerPlayer context);
}