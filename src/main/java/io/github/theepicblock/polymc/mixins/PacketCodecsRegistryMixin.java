package io.github.theepicblock.polymc.mixins;


/*import io.github.theepicblock.polymc.impl.Util;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import xyz.nucleoid.packettweaker.PacketContext;

@Mixin(targets = "net/minecraft/network/codec/ByteBufCodecs$29", priority = 500)
public abstract class PacketCodecsRegistryMixin {

    @Shadow @Final private ResourceKey val$registryKey;

    @SuppressWarnings({"rawtypes", "ShadowModifiers"})

    @ModifyVariable(method = "encode(Lnet/minecraft/network/RegistryFriendlyByteBuf;Ljava/lang/Object;)V", at = @At("HEAD"), argsOnly = true)
    private Object polymc$changeData(Object val, RegistryFriendlyByteBuf buf) {
        var player = PacketContext.get();
        var map = Util.tryGetPolyMap(player);

        if (val instanceof Holder<?> registryEntry) {
            var value = registryEntry.value();
            var out = map.tryRemapping(value, player);
            if (value != out) {
                if (!(buf instanceof RegistryFriendlyByteBuf)) throw new RuntimeException("The buffer captured by the PacketCodecsRegistryMixin of PolyMC was not actually a buffer!");
                return ((RegistryFriendlyByteBuf) buf).registryAccess().lookupOrThrow(this.val$registryKey).wrapAsHolder(out);
            }
            return val;
        }

        return map.tryRemapping(val, player);
    }
}*/ //TODO: I'm pretty sure this mixin is supposed to target the anonymous class created by the static <T> StreamCodec<RegistryFriendlyByteBuf, HolderSet<T>> holderSet(final ResourceKey<? extends Registry<T>> registryKey) method (the enocde() signature matches), but it's just no longer located at position $29 in the parent class. Now... Changing the ID is doable (if a bit trial-and-error-y - I'm sure there is some better way to find it, I'm just not versed well enough with Mixins to know it), but the bigger problem is that it seem like that class no longer has a simple ResourceKey val$registryKey field inside, but rather the ResourceKey registryKey it gets from the method that creates it, is instantly consumed and wrapped inside a StreamCodec<...> holderCodec field. There may be some way to capture the value as it's being passed into the overarching method (maybe that's what the val$ actually does, idk,I'm not well-versed with Mixins), or perhaps I can somehow unwrap a ResourceKey from the StreamCodec - either way, this is a more complicated fix than simply "update the $29 in the mixin signature" (and I didn't even get started on that whole "it relies on packettweaker.PacketContext which isn't a thing on 26.x, so I'll need to update that, too" bit), so I'm just disabling this Mixin for now.