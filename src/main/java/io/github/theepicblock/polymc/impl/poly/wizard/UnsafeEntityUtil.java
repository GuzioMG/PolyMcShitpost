package io.github.theepicblock.polymc.impl.poly.wizard;

import io.github.theepicblock.polymc.PolyMc;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeEntityUtil {
    static final Unsafe UNSAFE;

    private static final Field EVOL_ID;
    private static final Field EVOL_X;
    private static final Field EVOL_Y;
    private static final Field EVOL_Z;

    static {
        Unsafe unsafe;

        Field evol_id;
        Field evol_x;
        Field evol_y;
        Field evol_z;
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe)f.get(null);

            evol_id = tryGet(EntityVelocityUpdateS2CPacket.class, "field_12564", "I");
            evol_x = tryGet(EntityVelocityUpdateS2CPacket.class, "field_12563", "I");
            evol_y = tryGet(EntityVelocityUpdateS2CPacket.class, "field_12562", "I");
            evol_z = tryGet(EntityVelocityUpdateS2CPacket.class, "field_12561", "I");

//            var test = createEntityPositionPacketUnsafe(123, 0.1, 0.2, 0.3, (byte)12, (byte)23, false);
//            assert test.getId() == 123;
//            assert test.getX() == 0.1;
//            assert test.getY() == 0.2;
//            assert test.getZ() == 0.3;
//            assert test.getYaw() == 12;
//            assert test.getPitch() == 23;
//            assert !test.isOnGround();
//
//            var test2 = createEntityVelocityUpdateUnsafe(124, 2, 1, 9);
//            assert test2.getId() == 124;
//            assert test2.getVelocityX() == 2;
//            assert test2.getVelocityY() == 1;
//            assert test2.getVelocityZ() == 9;
        } catch (Exception ex) {
            unsafe = null;
            evol_id = null;
            evol_x = null;
            evol_y = null;
            evol_z = null;
            PolyMc.LOGGER.info("Couldn't enable unsafe packet instantiation, will default to slower path");
            ex.printStackTrace();
        }

        UNSAFE = unsafe;
        EVOL_ID = evol_id;
        EVOL_X = evol_x;
        EVOL_Y = evol_y;
        EVOL_Z = evol_z;
    }

    private static Field tryGet(Class<?> clazz, String name, String descriptor) throws NoSuchFieldException {
        var resolver = FabricLoader.getInstance().getMappingResolver();
        var mappedName = resolver.mapFieldName("intermediary", resolver.unmapClassName("intermediary", clazz.getName()), name, descriptor);
        Field field = clazz.getDeclaredField(mappedName);
        field.setAccessible(true);
        return field;
    }

    static EntityVelocityUpdateS2CPacket createEntityVelocityUpdateUnsafe(int id, int x, int y, int z) throws InstantiationException, IllegalAccessException {
        var packet = (EntityVelocityUpdateS2CPacket)UNSAFE.allocateInstance(EntityVelocityUpdateS2CPacket.class);

        EVOL_ID.set(packet, id);
        EVOL_X.set(packet, x);
        EVOL_Y.set(packet, y);
        EVOL_Z.set(packet, z);
        return packet;
    }
}