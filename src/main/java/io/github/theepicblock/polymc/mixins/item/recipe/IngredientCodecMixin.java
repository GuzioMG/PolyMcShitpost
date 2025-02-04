package io.github.theepicblock.polymc.mixins.item.recipe;

import eu.pb4.polymer.common.api.PolymerCommonUtils;
import io.github.theepicblock.polymc.impl.Util;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.entry.RegistryEntryList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nucleoid.packettweaker.PacketContext;

/**
 * The Ingredient codecs are no longer based on the ItemStack codecs,
 * so we need to replace them in order to prevent them sending modded item data to the client
 */
@Mixin(Ingredient.class)
public abstract class IngredientCodecMixin {

    @Inject(
        method = {
            "method_61677",
            "method_61673",
            "method_61680"
        },
        at = @At("RETURN"),
        cancellable = true
    )
    private static void polymc$convertRegistryEntries(Ingredient ingredient, CallbackInfoReturnable<RegistryEntryList<Item>> cir) {

        if (!PolymerCommonUtils.isServerNetworkingThread()) {
            return;
        }

        cir.setReturnValue(Util.transformRegistryEntryList(cir.getReturnValue(), PacketContext.get()));
    }
}
