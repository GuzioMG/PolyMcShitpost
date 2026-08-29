package io.github.theepicblock.polymc.api.wizard;

import io.github.theepicblock.polymc.impl.poly.wizard.VThrownItemEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;

public class VSnowball extends VThrownItemEntity {
    @Override
    public EntityType<?> getEntityType() {
        return EntityTypes.SNOWBALL;
    }
}