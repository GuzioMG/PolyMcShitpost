package io.github.theepicblock.polymc.mixins.item.recipe;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import eu.pb4.polymer.common.impl.CompatStatus;
import io.github.theepicblock.polymc.api.PolyMap;
import io.github.theepicblock.polymc.impl.Util;
import io.github.theepicblock.polymc.impl.misc.SkipCheck;
import io.github.theepicblock.polymc.impl.misc.TransformingPacketCodec;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.ArrayList;

@Mixin(value = SlotDisplay.class, priority = 900)
public interface SlotDisplayMixin {
    @SuppressWarnings("DataFlowIssue")
    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/codec/PacketCodec;dispatch(Ljava/util/function/Function;Ljava/util/function/Function;)Lnet/minecraft/network/codec/PacketCodec;"))
    private static PacketCodec<RegistryByteBuf, SlotDisplay> transformDisplays(PacketCodec<RegistryByteBuf, SlotDisplay> original) {
        return TransformingPacketCodec.encodeOnly(original, (buf, display) -> {
            var ctx = PacketContext.get();
            var map = Util.tryGetPolyMap(ctx);
            return polymc$transformDisplaySlot(ctx, map, buf, display);
        });
    }

    @Unique
    private static SlotDisplay polymc$transformDisplaySlot(PacketContext ctx, PolyMap map, RegistryByteBuf buf, SlotDisplay original) {

        if (original == SlotDisplay.EmptySlotDisplay.INSTANCE) {
            return original;
        }

        return switch (original) {
            case SlotDisplay.ItemSlotDisplay itemSlot when !map.canReceiveRegistryEntry(Registries.ITEM, itemSlot.item()) -> {
                RegistryEntry<Item> itemRegistryEntry = itemSlot.item();
                Item item = itemRegistryEntry.value();
                ItemStack clientStack = polymc$getClientStack(map, ctx.getPlayer(), item.getDefaultStack());
                yield new SlotDisplay.StackSlotDisplay(clientStack);
            }
            case SlotDisplay.TagSlotDisplay tagSlot when !((SkipCheck) (Object) tagSlot).polymc$skipped() -> {

                var tag = buf.getRegistryManager().getOrThrow(RegistryKeys.ITEM).getOptional(tagSlot.tag());
                if (tag.isEmpty()) {
                    yield tagSlot;
                }

                var array = new ArrayList<SlotDisplay>();
                for (var entry : tag.get()) {
                    if (!map.canReceiveRegistryEntry(Registries.ITEM, entry)) {
                        Item item = entry.value();
                        ItemStack clientStack = polymc$getClientStack(map, ctx.getPlayer(), item.getDefaultStack());
                        array.add(new SlotDisplay.StackSlotDisplay(clientStack));
                    }
                }
                if (!array.isEmpty()) {
                    var out = new SlotDisplay.TagSlotDisplay(tagSlot.tag());
                    ((SkipCheck) (Object) out).polymc$setSkipped();

                    if (CompatStatus.POLYMER_CORE) {
                        if (((SkipCheck) (Object) tagSlot).polymer$skipped()) {
                            ((SkipCheck) (Object) out).polymer$setSkipped();
                        }
                    }
                    array.addFirst(out);
                    yield new SlotDisplay.CompositeSlotDisplay(array);
                }
                yield tagSlot;
            }
            case SlotDisplay.StackSlotDisplay stackSlot when !map.canReceiveRegistryEntry(Registries.ITEM, Registries.ITEM.getEntry(stackSlot.stack().getItem())) -> {
                ItemStack clientStack = polymc$getClientStack(map, ctx.getPlayer(), stackSlot.stack());
                yield new SlotDisplay.StackSlotDisplay(clientStack);
            }
            default -> original;
        };
    }

    /**
     * Get a client-side representation of the original ItemStack.
     * Remove some components that are not required for displaying in the recipe slot
     */
    @Unique
    private static ItemStack polymc$getClientStack(PolyMap map, @Nullable ServerPlayerEntity player, ItemStack original) {

        ItemStack clientStack = map.getClientItem(original, player, null);

        clientStack.remove(DataComponentTypes.REPAIRABLE);
        clientStack.remove(DataComponentTypes.CUSTOM_DATA);
        clientStack.remove(DataComponentTypes.TOOL);

        return clientStack;
    }
}
