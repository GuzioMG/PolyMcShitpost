package io.github.theepicblock.polymc.mixins.component.transforms;

import io.github.theepicblock.polymc.impl.Util;
import io.github.theepicblock.polymc.impl.mixin.TransformingComponent;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

@Mixin(ApplyStatusEffectsConsumeEffect.class)
public abstract class ApplyEffectsConsumeEffectMixin implements TransformingComponent {
    @Shadow @Final private List<MobEffectInstance> effects;

    @Override
    public Object polymc$getTransformed(ServerPlayer context) {
        if (!polymc$requireModification(context)) {
            return this;
        }

        return new ApplyStatusEffectsConsumeEffect(List.of());
    }

    @Override
    public boolean polymc$requireModification(ServerPlayer context) {
        var map = Util.tryGetPolyMap(context);
        for (var effect : this.effects) {
            if (!map.canReceiveStatusEffect(effect.getEffect())) {
                return true;
            }
        }
        return false;
    }
}
