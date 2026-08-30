package nl.theepicblock.polymc.testmod;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public class TestFlyingWaxedWeatheredCutCopperStairs extends ThrowableItemProjectile implements ItemSupplier {
    public TestFlyingWaxedWeatheredCutCopperStairs(EntityType<? extends ThrowableItemProjectile> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected @NonNull Item getDefaultItem() {
        return Items.CUT_COPPER_STAIRS.waxed().weathered();
    }
}
