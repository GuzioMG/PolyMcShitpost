package io.github.theepicblock.polymc.api.wizard;

import io.github.theepicblock.polymc.impl.poly.wizard.VThrownItemEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class VSnowball extends VThrownItemEntity {
    public VSnowball(Level idSource) {
        super(idSource);
    }

    public VSnowball(int id) {
        super(id);
    }

    public VSnowball(UUID uuid, Level idSource) {
        super(uuid, idSource);
    }

    public VSnowball(UUID uuid, int id) {
        super(uuid, id);
    }

    @Override
    public EntityType<?> getEntityType() {
        return EntityTypes.SNOWBALL;
    }
}