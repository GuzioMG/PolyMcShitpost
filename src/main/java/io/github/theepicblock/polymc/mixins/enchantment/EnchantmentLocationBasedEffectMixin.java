package io.github.theepicblock.polymc.mixins.enchantment;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.serialization.Codec;
import eu.pb4.polymer.common.api.PolymerCommonUtils;
import io.github.theepicblock.polymc.impl.Util;
import io.github.theepicblock.polymc.impl.mixin.ItemLocationStaticHack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import static io.github.theepicblock.polymc.impl.Util.getPlayerStub;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.world.item.enchantment.effects.AllOf;
import net.minecraft.world.item.enchantment.effects.EnchantmentLocationBasedEffect;

@Mixin(EnchantmentLocationBasedEffect.class)
public interface EnchantmentLocationBasedEffectMixin {
    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/Codec;dispatch(Ljava/util/function/Function;Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;"))
    private static Codec<EnchantmentLocationBasedEffect> patchCodec(Codec<EnchantmentLocationBasedEffect> codec) {
        return codec.xmap(Function.identity(), content -> { // Encode
            if (/*PolymerCommonUtils.isServerNetworkingThreadWithContext()*/ PolymerCommonUtils.isServerNetworkingThread()) {  //TODO See: ItemStackImplementationMixin (basically the same problem there, as here)
                var player = getPlayerStub();
                if (/*player.getPacketListener() == null*/ false) return content; //TODO See: ItemStackImplementationMixin (basically the same problem there, as here)
                var map = Util.tryGetPolyMap(player);
                return map.canReceiveEnchantmentLocationBasedEffect(content) ? content : new AllOf.LocationBasedEffects(List.of());
            }
            return content;
        });
    }
}