package io.github.theepicblock.polymc.mixins.item.recipe;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.theepicblock.polymc.impl.Util;
import io.github.theepicblock.polymc.impl.misc.TransformingPacketCodec;
import net.minecraft.item.Item;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

// AIDEV-NOTE: Transforms Ingredient packet codec to filter out modded items from recipe ingredients
@Mixin(value = Ingredient.class, priority = 900)
public class IngredientMixin {
    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/codec/PacketCodecs;registryEntryList(Lnet/minecraft/registry/RegistryKey;)Lnet/minecraft/network/codec/PacketCodec;", ordinal = 0))
    private static PacketCodec<RegistryByteBuf, RegistryEntryList<Item>> transformIngredientCodec(PacketCodec<RegistryByteBuf, RegistryEntryList<Item>> original) {
        return TransformingPacketCodec.encodeOnly(original, (buf, entries) -> {
            var map = Util.tryGetPolyMap(PacketContext.get());
            
            // Filter the registry entry list to only include items the client can receive
            var filtered = new ArrayList<RegistryEntry<Item>>();
            for (var entry : entries) {
                if (map.canReceiveRegistryEntry(Registries.ITEM, entry)) {
                    filtered.add(entry);
                }
            }
            
            // If all items were filtered out, return a list with stick as fallback
            // This prevents "Ingredients can't be empty" error
            if (filtered.isEmpty()) {
                // AIDEV-NOTE: Using stick as fallback item when all ingredients are modded
                return RegistryEntryList.of(net.minecraft.item.Items.STICK.getRegistryEntry());
            }
            
            // Return a new registry entry list with only the filtered items
            return RegistryEntryList.of(filtered);
        });
    }
    
    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/codec/PacketCodecs;registryEntryList(Lnet/minecraft/registry/RegistryKey;)Lnet/minecraft/network/codec/PacketCodec;", ordinal = 1))
    private static PacketCodec<RegistryByteBuf, RegistryEntryList<Item>> transformOptionalIngredientCodec(PacketCodec<RegistryByteBuf, RegistryEntryList<Item>> original) {
        return TransformingPacketCodec.encodeOnly(original, (buf, entries) -> {
            var map = Util.tryGetPolyMap(PacketContext.get());
            
            // Filter the registry entry list to only include items the client can receive
            var filtered = new ArrayList<RegistryEntry<Item>>();
            for (var entry : entries) {
                if (map.canReceiveRegistryEntry(Registries.ITEM, entry)) {
                    filtered.add(entry);
                }
            }
            
            // Return a new registry entry list with only the filtered items (can be empty for optional)
            return filtered.isEmpty() ? RegistryEntryList.of() : RegistryEntryList.of(filtered);
        });
    }
}