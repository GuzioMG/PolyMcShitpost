package io.github.theepicblock.polymc.impl.generator;

import eu.pb4.polymer.common.impl.entity.InternalEntityHelpers;
import io.github.theepicblock.polymc.api.PolyRegistry;
import io.github.theepicblock.polymc.api.entity.EntityPoly;
import io.github.theepicblock.polymc.impl.Util;
import io.github.theepicblock.polymc.impl.poly.entity.DefaultedEntityPoly;
import io.github.theepicblock.polymc.impl.poly.entity.FlyingItemEntityPoly;
import io.github.theepicblock.polymc.impl.poly.entity.MissingEntityPoly;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.entity.animal.golem.AbstractGolem;
import net.minecraft.world.entity.animal.equine.AbstractChestedHorse;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class to automatically generate {@link EntityPoly}s for {@link EntityType}s
 */
public class EntityPolyGenerator {
    /**
     * Generates the most suitable {@link EntityPoly} for a given {@link EntityType}
     */
    public static <T extends Entity> EntityPoly<T> generatePoly(EntityType<T> entityType, PolyRegistry builder) {
        if (BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getNamespace().equals("taterzens")) {
            return (info, entity) -> null; // Compatibility with Taterzens
        }

        // Get the class of the entity
        var baseClass = InternalEntityHelpers.getEntityClass(entityType);

        if (baseClass == null) return new MissingEntityPoly<>();

        // Iterate over all vanilla entities to see if any are assignable
        var possible = new ArrayList<EntityType<?>>();
        for (var possibleType : BuiltInRegistries.ENTITY_TYPE) {
            var id = BuiltInRegistries.ENTITY_TYPE.getKey(possibleType);
            if (Util.isVanilla(id)) {
                Class<?> vanillaEntityClass = InternalEntityHelpers.getEntityClass(possibleType);

                if (vanillaEntityClass != null && vanillaEntityClass.isAssignableFrom(baseClass)) {
                    possible.add(possibleType);
                }
            }
        }

        // Players are blacklisted, we shouldn't spawn any players.
        possible.removeIf(clazz -> clazz == EntityTypes.PLAYER);

        // Sort the list of entities that match by the highest type
        // For example, if both ChestBoatEntity and BoatEntity matched, the boat will be first in the list
        possible.sort((a, b) -> {
            var classA = InternalEntityHelpers.getEntityClass(a);
            var classB = InternalEntityHelpers.getEntityClass(b);
            if (classA == classB) return 0;
            if (classA.isAssignableFrom(classB)) {
                // A is a super type of B, sort it higher
                return 1;
            } else {
                // B is a super type of A
                return -1;
            }
        });

        if (possible.size() > 0) {
            return new DefaultedEntityPoly<>(possible.get(0));
        }

        if (ItemSupplier.class.isAssignableFrom(baseClass)) {
            return new FlyingItemEntityPoly();
        }

        if (AbstractGolem.class.isAssignableFrom(baseClass)) {
            if (entityType.getWidth() > 1) {
                return new DefaultedEntityPoly<>(EntityTypes.IRON_GOLEM);
            } else {
                return new DefaultedEntityPoly<>(EntityTypes.SNOW_GOLEM);
            }
        }

        var otherCommonClasses = new HashMap<Class<?>, EntityType<?>>();
        otherCommonClasses.put(AbstractChestedHorse.class, EntityTypes.DONKEY);
        otherCommonClasses.put(AbstractHorse.class, EntityTypes.HORSE);
        otherCommonClasses.put(AbstractPiglin.class, EntityTypes.PIGLIN);
        otherCommonClasses.put(AbstractSkeleton.class, EntityTypes.SKELETON);
        otherCommonClasses.put(AbstractMinecart.class, EntityTypes.MINECART);
        otherCommonClasses.put(Projectile.class, EntityTypes.ARROW);
        otherCommonClasses.put(AbstractFish.class, EntityTypes.COD);

        for (var clazz : otherCommonClasses.keySet()) {
            if (clazz.isAssignableFrom(baseClass)) {
                return new DefaultedEntityPoly<>(otherCommonClasses.get(clazz));
            }
        }

        if (LivingEntity.class.isAssignableFrom(baseClass)) {
            if (entityType.getHeight() > 1.5) {
                if (Enemy.class.isAssignableFrom(baseClass)) {
                    return new DefaultedEntityPoly<>(EntityTypes.ZOMBIE);
                } else {
                    return new DefaultedEntityPoly<>(EntityTypes.ARMOR_STAND);
                }
            } else if (entityType.getHeight() > 0.5) {
                return new DefaultedEntityPoly<>(EntityTypes.PIG);
            } else {
                return new DefaultedEntityPoly<>(EntityTypes.SILVERFISH);
            }
        }

        return new MissingEntityPoly<>();
    }

    /**
     * Generates the most suitable {@link EntityPoly} and directly adds it to the {@link PolyRegistry}
     * @see #generatePoly(EntityType, PolyRegistry)
     */
    public static <T extends Entity> void addEntityToBuilder(EntityType<T> entityType, PolyRegistry builder) {
        builder.registerEntityPoly(entityType, generatePoly(entityType, builder));
    }
}