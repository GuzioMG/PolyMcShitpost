package io.github.theepicblock.polymc.mixins.item.codec;

import com.mojang.serialization.Codec;
import eu.pb4.polymer.common.api.PolymerCommonUtils;
import io.github.theepicblock.polymc.impl.Util;
import io.github.theepicblock.polymc.impl.mixin.ItemLocationStaticHack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import static io.github.theepicblock.polymc.impl.Util.getPlayerStub;

import java.util.function.Supplier;
import net.minecraft.world.item.ItemStack;


@Mixin(ItemStack.class)
public class ItemStackImplementationMixin {
    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/Codec;lazyInitialized(Ljava/util/function/Supplier;)Lcom/mojang/serialization/Codec;"))
    private static Supplier<Codec<ItemStack>> patchCodec(Supplier<Codec<ItemStack>> codec) {
        return () -> codec.get().xmap(
            /*  Decode  */
            content -> {
                if (PolymerCommonUtils.isServerNetworkingThread()) {
                    var player = getPlayerStub();
                    var map = Util.tryGetPolyMap(player);
                    return map.reverseClientItem(content, player);
                }
                return content;
            },

            /*  Encode  */
            content -> {
                if (/*PolymerCommonUtils.isServerNetworkingThreadWithContext()*/ PolymerCommonUtils.isServerNetworkingThread()) { //TODO isServerNetworkingThreadWithContext no longer exists in PolymerCommonUtils, presumably because it's related to packet-tweaker which itself no longer exists. Assuming PolymerCommonUtils.isServerNetworkingThread() is sufficient for now - requires further testing.
                    var player = getPlayerStub();
                    if (/*player.getPacketListener() == null*/ false) return content; //TODO Cannot do this check on an actual ServerPlayer - assuming it's unnecessary for now; gotta make sure later.
                    var map = Util.tryGetPolyMap(player);
                    return map.getClientItem(content, player, ItemLocationStaticHack.location.get());
                }
                return content;
            }
        );
    }
}
