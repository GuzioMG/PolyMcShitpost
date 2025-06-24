package io.github.theepicblock.polymc.mixins.item.recipe;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.theepicblock.polymc.impl.Util;
import io.github.theepicblock.polymc.impl.misc.TransformingPacketCodec;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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

// AIDEV-NOTE: Transforms Ingredient packet codec to convert modded items to their vanilla representations
@Mixin(value = Ingredient.class, priority = 900)
public class IngredientMixin {
    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/codec/PacketCodecs;registryEntryList(Lnet/minecraft/registry/RegistryKey;)Lnet/minecraft/network/codec/PacketCodec;", ordinal = 0))
    private static PacketCodec<RegistryByteBuf, RegistryEntryList<Item>> transformIngredientCodec(PacketCodec<RegistryByteBuf, RegistryEntryList<Item>> original) {
        return TransformingPacketCodec.encodeOnly(original, (buf, entries) -> {
            var ctx = PacketContext.get();
            var map = Util.tryGetPolyMap(ctx.getClientConnection());
            var player = ctx.getPlayer();
            
            // Transform each entry to its vanilla representation
            var transformed = new ArrayList<RegistryEntry<Item>>();
            for (var entry : entries) {
                if (map.canReceiveRegistryEntry(Registries.ITEM, entry)) {
                    // Item can be received directly by client
                    transformed.add(entry);
                } else if (entry.value() != null) {
                    // Item needs transformation - create temporary stack and transform it
                    var tempStack = new ItemStack(entry.value());
                    var clientStack = map.getClientItem(tempStack, player, null);
                    
                    // Get the registry entry of the transformed item
                    var clientEntry = clientStack.getItem().getRegistryEntry();
                    if (!transformed.contains(clientEntry)) {
                        transformed.add(clientEntry);
                    }
                }
            }
            
            // If all items were somehow invalid, return a list with stick as fallback
            // This prevents "Ingredients can't be empty" error
            if (transformed.isEmpty()) {
                // AIDEV-NOTE: Using stick as fallback item when transformation fails for all ingredients
                return RegistryEntryList.of(net.minecraft.item.Items.STICK.getRegistryEntry());
            }
            
            // Return a new registry entry list with the transformed items
            return RegistryEntryList.of(transformed);
        });
    }
    
    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/codec/PacketCodecs;registryEntryList(Lnet/minecraft/registry/RegistryKey;)Lnet/minecraft/network/codec/PacketCodec;", ordinal = 1))
    private static PacketCodec<RegistryByteBuf, RegistryEntryList<Item>> transformOptionalIngredientCodec(PacketCodec<RegistryByteBuf, RegistryEntryList<Item>> original) {
        return TransformingPacketCodec.encodeOnly(original, (buf, entries) -> {
            var ctx = PacketContext.get();
            var map = Util.tryGetPolyMap(ctx.getClientConnection());
            var player = ctx.getPlayer();
            
            // Transform each entry to its vanilla representation
            var transformed = new ArrayList<RegistryEntry<Item>>();
            for (var entry : entries) {
                if (map.canReceiveRegistryEntry(Registries.ITEM, entry)) {
                    // Item can be received directly by client
                    transformed.add(entry);
                } else if (entry.value() != null) {
                    // Item needs transformation - create temporary stack and transform it
                    var tempStack = new ItemStack(entry.value());
                    var clientStack = map.getClientItem(tempStack, player, null);
                    
                    // Get the registry entry of the transformed item
                    var clientEntry = clientStack.getItem().getRegistryEntry();
                    if (!transformed.contains(clientEntry)) {
                        transformed.add(clientEntry);
                    }
                }
            }
            
            // Return a new registry entry list with the transformed items (can be empty for optional)
            return transformed.isEmpty() ? RegistryEntryList.of() : RegistryEntryList.of(transformed);
        });
    }
}