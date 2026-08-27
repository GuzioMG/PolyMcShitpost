package io.github.theepicblock.polymc.impl.poly.wizard;

import io.github.theepicblock.polymc.PolyMc;
import io.github.theepicblock.polymc.mixins.wizards.EntityAccessor;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.EntityPosition;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class EntityUtil {
    public static int getNewEntityId() {
        return EntityAccessor.getEntityIdCounter().incrementAndGet();
    }

    public static EntityPositionS2CPacket createEntityPositionPacket(
            int id, double x, double y, double z, byte yaw, byte pitch, boolean onGround) {
        if (UnsafeEntityUtil.UNSAFE != null) {
            try {
                return UnsafeEntityUtil.createEntityPositionPacketUnsafe(id, x, y, z, yaw, pitch, onGround);
            } catch (InstantiationException | IllegalAccessException e) {
                PolyMc.LOGGER.warn("Exception whilst creating entity position packet. Attempting to recover");
                e.printStackTrace();
            }
        }

        EntityPosition change = new EntityPosition(
                new Vec3d(x, y, z),
                Vec3d.ZERO,
                yaw,
                pitch
        );

        Set<PositionFlag> relatives = Collections.emptySet();

        return EntityPositionS2CPacket.create(
                id,
                change,
                relatives,
                onGround
        );
    }

    public static EntityVelocityUpdateS2CPacket createEntityVelocityUpdate(int id, int x, int y, int z) {
        if (UnsafeEntityUtil.UNSAFE != null) {
            try {
                return UnsafeEntityUtil.createEntityVelocityUpdateUnsafe(id, x, y, z);
            } catch (InstantiationException | IllegalAccessException e) {
                PolyMc.LOGGER.warn("Exception whilst creating entity velocity packet. Attempting to recover");
                e.printStackTrace();
            }
        }

        PacketByteBuf byteBuf = PacketByteBufs.create();
        byteBuf.writeVarInt(id);
        byteBuf.writeShort(x);
        byteBuf.writeShort(y);
        byteBuf.writeShort(z);

        return EntityVelocityUpdateS2CPacket.CODEC.decode(byteBuf);
    }

    public static <T> EntityTrackerUpdateS2CPacket createDataTrackerUpdate(int id, TrackedData<T> tracker, T value) {
        List<DataTracker.SerializedEntry<?>> list = new ArrayList<>(1);
        list.add(DataTracker.SerializedEntry.of(tracker, value));

        return new EntityTrackerUpdateS2CPacket(id, list);
    }

    public static EntityTrackerUpdateS2CPacket createDataTrackerUpdate(int id, List<DataTracker.Entry<?>> customEntries) {
        List<DataTracker.SerializedEntry<?>> list = new ArrayList<>(customEntries.size());
        for (var entry : customEntries) {
            list.add(entry.toSerialized());
        }
        return new EntityTrackerUpdateS2CPacket(id, list);
    }
}
