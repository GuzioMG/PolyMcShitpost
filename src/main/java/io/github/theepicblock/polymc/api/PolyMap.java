/*
 * PolyMc
 * Copyright (C) 2020-2020 TheEpicBlock_TEB
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; If not, see <https://www.gnu.org/licenses>.
 */
package io.github.theepicblock.polymc.api;

import eu.pb4.polymer.rsm.api.RegistrySyncUtils;
import io.github.theepicblock.polymc.api.block.BlockPoly;
import io.github.theepicblock.polymc.api.entity.EntityPoly;
import io.github.theepicblock.polymc.api.gui.GuiPoly;
import io.github.theepicblock.polymc.api.item.ItemLocation;
import io.github.theepicblock.polymc.api.item.ItemPoly;
import io.github.theepicblock.polymc.api.resource.PolyMcResourcePack;
import io.github.theepicblock.polymc.impl.ConfigManager;
import io.github.theepicblock.polymc.impl.Util;
import io.github.theepicblock.polymc.impl.misc.logging.SimpleLogger;
import io.github.theepicblock.polymc.impl.mixin.ItemLocationStaticHack;
import io.github.theepicblock.polymc.mixins.entity.EntityAttributesFilteringMixin;
import io.github.theepicblock.polymc.mixins.gui.GuiPolyImplementation;
import io.github.theepicblock.polymc.mixins.item.CreativeItemStackFix;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.component.DataComponentType; //!PREVIOUSLY: ComponentType
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentLocationBasedEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute; //!PREVIOUSLY: EntityAttribute
import net.minecraft.world.effect.MobEffect; //!PREVIOUSLY: StatusEffect
import net.minecraft.world.effect.MobEffects; //!PREVIOUSLY: StatusEffects
import net.minecraft.fluid.Fluid; //!UNKNOWN
import net.minecraft.fluid.Fluids; //!UNKNOWN
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.network.packet.CustomPayload; //!UNKNOWN
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.registry.entry.RegistryEntry; //!UNKNOWN
import net.minecraft.screen.ScreenHandlerType; //!UNKNOWN
import net.minecraft.server.network.ServerCommonNetworkHandler; //!UNKNOWN
import net.minecraft.server.network.ServerConfigurationNetworkHandler; //!UNKNOWN
import net.minecraft.server.network.ServerPlayNetworkHandler; //!UNKNOWN
import net.minecraft.server.network.ServerPlayerEntity; //!UNKNOWN
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.math.Direction; //!UNKNOWN
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

public interface PolyMap {
    /**
     * Converts the serverside representation of an item into a clientside one that should be sent to the client.
     */
    ItemStack getClientItem(ItemStack serverItem, @Nullable ServerPlayerEntity player, @Nullable ItemLocation location);

    /**
     * Converts the serverside representation of a block into a clientside one that should be sent to the client.
     */
    default BlockState getClientState(BlockState serverBlock, @Nullable ServerPlayerEntity player) {
        BlockPoly poly = this.getBlockPoly(serverBlock.getBlock());
        if (poly == null) return serverBlock;

        return poly.getClientBlock(serverBlock);
    }

    /**
     * Get the raw id of the clientside blockstate.
     */
    @ApiStatus.Internal
    default int getClientStateRawId(BlockState state, ServerPlayerEntity playerEntity) {
        BlockState clientState = this.getClientState(state, playerEntity);

        if (clientState == null) {
            clientState = Blocks.STONE.getDefaultState();
        }

        return Block.STATE_IDS.getRawId(clientState);
    }

    /**
     * @return the {@link ItemPoly} that this PolyMap associates with this {@link Item}.
     */
    ItemPoly getItemPoly(Item item);

    /**
     * @return the {@link BlockPoly} that this PolyMap associates with this {@link Block}.
     */
    BlockPoly getBlockPoly(Block block);

    /**
     * @return the {@link GuiPoly} that this PolyMap associates with this {@link ScreenHandlerType}.
     */
    GuiPoly getGuiPoly(ScreenHandlerType<?> serverGuiType);

    /**
     * @return the {@link EntityPoly} that this PolyMap associates with this {@link EntityType}.
     */
    <T extends Entity> EntityPoly<T> getEntityPoly(EntityType<T> entity);

    /**
     * Reverts the clientside item into the serverside representation.
     * This should be the reverse of {@link #getClientItem(ItemStack, ServerPlayerEntity, ItemLocation)}.
     * For optimization reasons, this method only needs to be implemented for items gained by players in creative mode.
     * @see CreativeItemStackFix
     */
    ItemStack reverseClientItem(ItemStack clientItem, @Nullable ServerPlayerEntity player);

    /**
     * Specifies if this map is meant for vanilla-like clients
     * This is used to disable/enable miscellaneous patches
     * @see io.github.theepicblock.polymc.mixins.CustomPacketDisabler
     * @see io.github.theepicblock.polymc.mixins.block.ResyncImplementation
     * @see io.github.theepicblock.polymc.impl.mixin.CustomBlockBreakingCheck#needsCustomBreaking(ServerPlayerEntity, BlockState)
     * @see GuiPolyImplementation
     */
    boolean isVanillaLikeMap();

    boolean hasBlockWizards();

    /**
     * Specifies if the {@link BlockState} changes done around this block might require a resync.
     */
    boolean shouldForceBlockStateSync(BlockState sourceState, BlockState clientState, Direction direction);

    @Nullable PolyMcResourcePack generateResourcePack(SimpleLogger logger);

    String dumpDebugInfo();

    /**
     * Used for filtering out registry entries unsupported by client.
     * @see EntityAttributesFilteringMixin
     */
    default <T> boolean canReceiveRegistryEntry(Registry<T> registry, RegistryEntry<T> entry) {
        return Util.isVanillaAndRegistered(entry) || RegistrySyncUtils.isServerEntry(registry, entry.value());
    }

    default <T> boolean canReceiveEntry(Registry<T> registry, T entry) {
        return Util.isVanilla(registry.getId(entry)) || RegistrySyncUtils.isServerEntry(registry, entry);
    }

    default boolean canReceiveBlockEntity(BlockEntityType<?> e) {
        return Util.isVanilla(Registries.BLOCK_ENTITY_TYPE.getId(e));
    }

    default boolean canReceiveStatusEffect(RegistryEntry<StatusEffect> entry) {
        return Util.isVanillaAndRegistered(entry);
    }

    default boolean canReceiveEnchantment(RegistryEntry<Enchantment> entry) {
        return Util.isVanillaAndRegistered(entry);
    }

    default boolean canReceivePotion(RegistryEntry<Potion> entry) {
        return Util.isVanillaAndRegistered(entry);
    }

    default boolean canReceiveDataComponentType(ComponentType<?> type) {
        return Util.isVanilla(Registries.DATA_COMPONENT_TYPE.getId(type));
    }

    default boolean canReceiveEnchantmentComponentType(ComponentType<?> type) {
        return Util.isVanilla(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE.getId(type));
    }

    default boolean canReceiveCustomPayload(ServerCommonNetworkHandler handler, CustomPayload.Id<? extends CustomPayload> id) {
        return Util.isVanilla(id.id())
                || (handler instanceof ServerPlayNetworkHandler play && ServerPlayNetworking.canSend(play, id))
                || (handler instanceof ServerConfigurationNetworkHandler config && ServerConfigurationNetworking.canSend(config, id))
                || ConfigManager.getConfig().allowedPackets.contains(id.id().getNamespace());
    }

    default boolean canReceiveComponentType(ComponentType<?> key) {
        return canReceiveDataComponentType(key) || canReceiveEnchantmentComponentType(key);
    };

    default boolean canReceiveEnchantmentLocationBasedEffect(EnchantmentLocationBasedEffect effect) {
        return Util.isVanilla(Registries.ENCHANTMENT_LOCATION_BASED_EFFECT_TYPE.getId(effect.getCodec()));
    }

    default boolean canReceiveEnchantmentEntityEffect(EnchantmentEntityEffect effect) {
        return Util.isVanilla(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE.getId(effect.getCodec()));
    }

    default boolean canReceiveConsumeEffect(ConsumeEffect.Type<? extends ConsumeEffect> type) {
        return Util.isVanilla(Registries.CONSUME_EFFECT_TYPE.getId(type));
    };

    default Object tryRemapping(Object val, PacketContext player) {
        if (val instanceof Item entry) {
            var poly = this.getItemPoly(entry);
            if (poly != null) {
                return poly.getClientItem(new ItemStack(entry), player.getPlayer(), ItemLocationStaticHack.location.get()).getItem();
            }
        } else if (val instanceof Block entry) {
            var poly = this.getBlockPoly(entry);
            if (poly != null) {
                return poly.getClientBlock(entry.getDefaultState()).getBlock();
            }
        } else if (val instanceof SoundEvent entry && !this.canReceiveEntry(Registries.SOUND_EVENT, entry)) {
            return SoundEvents.INTENTIONALLY_EMPTY;
        } else if (val instanceof Fluid entry && !this.canReceiveEntry(Registries.FLUID, entry)) {
            return Fluids.EMPTY;
        } else if (val instanceof StatusEffect entry && !this.canReceiveStatusEffect(Registries.STATUS_EFFECT.getEntry(entry))) {
            return StatusEffects.LUCK;
        } else if (val instanceof EntityType<?> entry && !this.canReceiveEntry(Registries.ENTITY_TYPE, entry)) {
            return EntityType.ITEM_DISPLAY;
        } else if (val instanceof Potion entry && !this.canReceiveEntry(Registries.POTION, entry)) {
            return Potions.LUCK.value();
        } else if (val instanceof ParticleType<?> entry && !this.canReceiveEntry(Registries.PARTICLE_TYPE, entry)) {
            return ParticleTypes.SMOKE;
        }

        return val;
    }
}