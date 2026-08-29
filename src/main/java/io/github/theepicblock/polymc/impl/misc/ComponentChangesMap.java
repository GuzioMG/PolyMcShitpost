package io.github.theepicblock.polymc.impl.misc;

import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import org.jspecify.annotations.NonNull;

public record ComponentChangesMap(DataComponentPatch changes) implements DataComponentMap {
    @Nullable
    @Override
    public <T> T get(@NonNull DataComponentType<? extends T> type) {
        return this.changes.get(this, type);
    }

    @Override
    public @NonNull Set<DataComponentType<?>> keySet() {
        var set = new HashSet<DataComponentType<?>>();
        for (var entry : this.changes.entrySet()) {
            if (entry.getValue().isPresent()) {
                set.add(entry.getKey());
            }
        }
        return set;
    }
}
