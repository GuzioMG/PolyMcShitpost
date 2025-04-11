package io.github.theepicblock.polymc.mixins;

import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.theepicblock.polymc.impl.Util;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xyz.nucleoid.packettweaker.PacketContext;

@Mixin(targets = "net/minecraft/network/packet/s2c/play/CommandTreeS2CPacket$ArgumentNode")
public class CommandTreeS2CPacketArgumentNodeMixin {
    @ModifyArg(method = "write(Lnet/minecraft/network/PacketByteBuf;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/CommandTreeS2CPacket$ArgumentNode;write(Lnet/minecraft/network/PacketByteBuf;Lnet/minecraft/command/argument/serialize/ArgumentSerializer$ArgumentTypeProperties;)V"))
    private ArgumentSerializer.ArgumentTypeProperties<?> replaceProperties(ArgumentSerializer.ArgumentTypeProperties<?> original) {
        var player = PacketContext.get();
        var map = Util.tryGetPolyMap(player);

        var id = Registries.COMMAND_ARGUMENT_TYPE.getId(original.getSerializer());
        var isBrigadier = id != null && id.getNamespace().equals("brigadier");
        if (!map.canReceiveEntry(Registries.COMMAND_ARGUMENT_TYPE, original.getSerializer()) && !isBrigadier) {
            return ArgumentTypes.getArgumentTypeProperties(StringArgumentType.word());
        }
        return original;
    }
}
