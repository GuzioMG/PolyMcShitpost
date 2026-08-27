package io.github.theepicblock.polymc.impl.poly.wizard;

import io.github.theepicblock.polymc.PolyMc;
import io.github.theepicblock.polymc.mixins.wizards.EntityAccessor;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class EntityUtil {
    public static int getNewEntityId() {
        return EntityAccessor.getEntityIdCounter().incrementAndGet();
    }

    public static ClientboundTeleportEntityPacket createEntityPositionPacket(
            int id, double x, double y, double z, byte yaw, byte pitch, boolean onGround) {
        if (UnsafeEntityUtil.UNSAFE != null) {
            try {
                return UnsafeEntityUtil.createEntityPositionPacketUnsafe(id, x, y, z, yaw, pitch, onGround);
            } catch (InstantiationException | IllegalAccessException e) {
                PolyMc.LOGGER.warn("Exception whilst creating entity position packet. Attempting to recover");
                e.printStackTrace();
            }
        }

        PositionMoveRotation change = new PositionMoveRotation(
                new Vec3(x, y, z),
                Vec3.ZERO,
                yaw,
                pitch
        );

        Set<Relative> relatives = Collections.emptySet();

        return ClientboundTeleportEntityPacket.teleport(
                id,
                change,
                relatives,
                onGround
        );
    }

    public static ClientboundSetEntityMotionPacket createEntityVelocityUpdate(int id, int x, int y, int z) {
        if (UnsafeEntityUtil.UNSAFE != null) {
            try {
                return UnsafeEntityUtil.createEntityVelocityUpdateUnsafe(id, x, y, z);
            } catch (InstantiationException | IllegalAccessException e) {
                PolyMc.LOGGER.warn("Exception whilst creating entity velocity packet. Attempting to recover");
                e.printStackTrace();
            }
        }

        FriendlyByteBuf byteBuf = PacketByteBufs.create();
        byteBuf.writeVarInt(id);
        byteBuf.writeShort(x);
        byteBuf.writeShort(y);
        byteBuf.writeShort(z);

        return ClientboundSetEntityMotionPacket.STREAM_CODEC.decode(byteBuf);
    }

    public static <T> ClientboundSetEntityDataPacket createDataTrackerUpdate(int id, EntityDataAccessor<T> tracker, T value) {
        List<SynchedEntityData.DataValue<?>> list = new ArrayList<>(1);
        list.add(SynchedEntityData.DataValue.create(tracker, value));

        return new ClientboundSetEntityDataPacket(id, list);
    }

    public static ClientboundSetEntityDataPacket createDataTrackerUpdate(int id, List<SynchedEntityData.DataItem<?>> customEntries) {
        List<SynchedEntityData.DataValue<?>> list = new ArrayList<>(customEntries.size());
        for (var entry : customEntries) {
            list.add(entry.value());
        }
        return new ClientboundSetEntityDataPacket(id, list);
    }
}
