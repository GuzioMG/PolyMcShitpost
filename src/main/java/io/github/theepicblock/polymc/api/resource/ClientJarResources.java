package io.github.theepicblock.polymc.api.resource;

import java.io.InputStream;
import java.util.Set;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.util.Tuple;

public interface ClientJarResources extends AutoCloseable, ResourceContainer {
    Set<Tuple<Identifier,IoSupplier<InputStream>>> locateLanguageFiles();
}
