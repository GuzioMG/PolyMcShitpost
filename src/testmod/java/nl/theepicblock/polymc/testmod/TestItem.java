package nl.theepicblock.polymc.testmod;

import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Consumer;

public class TestItem extends Item {
    public TestItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        textConsumer.accept(Text.literal("Normal tooltip"));
        textConsumer.accept(Text.literal("Red tooltip").formatted(Formatting.RED));
        super.appendTooltip(stack, context, displayComponent, textConsumer, type);
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        Testmod.debugSend(player, "[TestItem] onClicked");
        return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity playerEntity) {
            Testmod.debugSend(playerEntity, "[TestItem] onStoppedUsing");
        }
        return super.onStoppedUsing(stack, world, user, remainingUseTicks);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        Testmod.debugSend(user, "[TestItem] use");
        return super.use(world, user, hand);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        Testmod.debugSend(context.getPlayer(), "[TestItem] useOnBlock");
        return super.useOnBlock(context);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        Testmod.debugSend(user, "[TestItem] useOnEntity");
        return super.useOnEntity(stack, user, entity, hand);
    }
}
