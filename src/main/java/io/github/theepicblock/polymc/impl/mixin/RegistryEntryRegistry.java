package io.github.theepicblock.polymc.impl.mixin;

import net.minecraft.registry.Registry;

public interface RegistryEntryRegistry<T> {
    Registry<T> polymc$getRegistry();
}
