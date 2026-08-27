package nl.theepicblock.polymc.testmod;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import java.util.List;
import java.util.function.Consumer;

public class TestItem extends Item {
    public TestItem(Properties settings) {
        super(settings);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
        textConsumer.accept(Component.literal("Normal tooltip"));
        textConsumer.accept(Component.literal("Red tooltip").withStyle(ChatFormatting.RED));
        super.appendHoverText(stack, context, displayComponent, textConsumer, type);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack otherStack, Slot slot, ClickAction clickType, Player player, SlotAccess cursorStackReference) {
        Testmod.debugSend(player, "[TestItem] onClicked");
        return super.overrideOtherStackedOnMe(stack, otherStack, slot, clickType, player, cursorStackReference);
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof Player playerEntity) {
            Testmod.debugSend(playerEntity, "[TestItem] onStoppedUsing");
        }
        return super.releaseUsing(stack, world, user, remainingUseTicks);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        Testmod.debugSend(user, "[TestItem] use");
        return super.use(world, user, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Testmod.debugSend(context.getPlayer(), "[TestItem] useOnBlock");
        return super.useOn(context);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity entity, InteractionHand hand) {
        Testmod.debugSend(user, "[TestItem] useOnEntity");
        return super.interactLivingEntity(stack, user, entity, hand);
    }
}
