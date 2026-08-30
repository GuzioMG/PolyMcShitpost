package io.github.theepicblock.polymc.mixins;


import io.github.theepicblock.polymc.impl.Util;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import static io.github.theepicblock.polymc.impl.Util.getPlayerStub;

@Mixin(targets = "net/minecraft/network/codec/ByteBufCodecs$31", priority = 500)
public abstract class PacketCodecsRegistryMixin {

    @SuppressWarnings({"rawtypes"}) @Shadow @Final ResourceKey val$registryKey;

    @ModifyVariable(method = "encode(Ljava/lang/Object;Ljava/lang/Object;)V", at = @At("HEAD"), argsOnly = true, name = "value")  // <-- This one makes the IDE happy (note, that by IDE, I actually mean the IDE itself - both compile just fine, but the bottom one lights up with errors as if it was not gonna compile). (btw, if you hit Go to ModifyVariable target with this one, IDE just throws an error lol)
    //@ModifyVariable(method = "encode(Lnet/minecraft/network/RegistryFriendlyByteBuf;Ljava/lang/Object;)V", at = @At("HEAD"), argsOnly = true, name = "value") // <-- This one makes sense (it tracks better with the actual method signature, both here inside the mixin, and in the target class (public void encode(final RegistryFriendlyByteBuf output, final R value)), if by "the target class" we mean the one that I manually counted with Ctrl+F'ing "new StreamCodec<" AND ALSO, more notably, the one that you get sent to if you hit "Go to target class"). (btw, Go to ModifyVariable target doesn't even show up with this one, probably because the "compile" error suppresses that button)
    private Object polymc$changeData(RegistryFriendlyByteBuf output, Object value) {
        var player = getPlayerStub();
        var map = Util.tryGetPolyMap(player);

        if (value instanceof Holder<?> registryEntry) {
            var entryValue = registryEntry.value();
            var out = map.tryRemapping(entryValue, player);
            if (entryValue != out) {
                if (!(output instanceof RegistryFriendlyByteBuf)) throw new RuntimeException("The buffer captured by the PacketCodecsRegistryMixin of PolyMC was not actually a buffer!");
                return output.registryAccess().lookupOrThrow(this.val$registryKey).wrapAsHolder(out);
            }
            return value;
        }

        return map.tryRemapping(value, player);
    }
}