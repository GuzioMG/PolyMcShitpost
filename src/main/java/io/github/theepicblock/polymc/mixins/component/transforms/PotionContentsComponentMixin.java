package io.github.theepicblock.polymc.mixins.component.transforms;

import io.github.theepicblock.polymc.impl.Util;
import io.github.theepicblock.polymc.impl.mixin.TransformingComponent;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;

@Mixin(PotionContents.class)
public abstract class PotionContentsComponentMixin implements TransformingComponent {
    @Shadow @Final private Optional<Holder<Potion>> potion;

    @Shadow @Final private List<MobEffectInstance> customEffects;

    @Shadow public abstract int getColor();

    @Shadow @Final private Optional<String> customName;

    @Override
    public Object polymc$getTransformed(ServerPlayer player) {
        if (!polymc$requireModification(player)) {
            return this;
        }

        return new PotionContents(Optional.empty(), Optional.of(this.getColor()), List.of(), this.customName);
    }

    @Override
    public boolean polymc$requireModification(ServerPlayer player) {
        var map = Util.tryGetPolyMap(player);
        if (this.potion.isPresent() && !map.canReceivePotion(this.potion.get())) {
            return true;
        }

        for (MobEffectInstance statusEffectInstance : this.customEffects) {
            if (this.potion.isPresent() && !map.canReceiveStatusEffect(statusEffectInstance.getEffect())) {
                return true;
            }
        }
        return false;
    }
}
