package io.github.theepicblock.polymc.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;

@Mixin(Level.class)
public interface WorldAccessor {
    @Mutable
    @Accessor("thread")
    void polymc$setThread(Thread thread);

    @Mutable
    @Accessor("isDebug")
    void polymc$setDebugWorld(boolean debugWorld);

    @Mutable
    @Accessor("levelData")
    void polymc$setProperties(WritableLevelData properties);

    @Mutable
    @Accessor("biomeManager")
    void polymc$setBiomeAccess(BiomeManager biomeAccess);

    @Mutable
    @Accessor("dimension")
    void polymc$setRegistryKey(ResourceKey<Level> registryKey);

    @Mutable
    @Accessor("dimensionTypeRegistration")
    void polymc$setDimensionEntry(Holder<DimensionType> dimensionEntry);

    @Mutable
    @Accessor("random")
    void polymc$setRandom(RandomSource random);

    @Mutable
    @Accessor("random")
    void polymc$setAsyncRandom(RandomSource random);
    //TODO Make double-triple-extra sure that the default random source REALLY is thread-safe (somewhere between 1.21.10 and 26.2, the safeRandom field got removed, so I'm ASSUMING that this means that every random is now safe, but there's  always the risk that... Idk, Mojang gave up on random-safety, or something, and it will not be safe)

    @Mutable
    @Accessor("blockEntityTickers")
    void polymc$setBlockEntityTickers(List<TickingBlockEntity> list);

    @Mutable
    @Accessor("pendingBlockEntityTickers")
    void polymc$setPendingBlockEntityTickers(List<TickingBlockEntity> list);


    @Mutable
    @Accessor("damageSources")
    void polymc$setDamageSources(DamageSources damageSources);
}
