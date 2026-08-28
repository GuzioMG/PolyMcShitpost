package io.github.theepicblock.polymc.api.resource;

import java.io.InputStream;
import java.util.Set;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.IoSupplier;
import com.mojang.datafixers.util.Pair;

public interface ClientJarResources extends AutoCloseable, ResourceContainer {
    Set<Pair<Identifier,IoSupplier<InputStream>>> locateLanguageFiles();
}
