package com.gladurbad.antimovehack.playerdata;

import com.gladurbad.antimovehack.check.Check;
import com.gladurbad.antimovehack.manager.CheckManager;
import com.gladurbad.antimovehack.network.Packet;

import io.github.retrooper.packetevents.event.PacketListener;
import io.github.retrooper.packetevents.packet.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.flying.WrappedPacketInFlying;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class PlayerData implements PacketListener {

    private final Player player;
    private final UUID playerUUID;
    private List<Check> checks;

    public PlayerData(UUID playerUUID, Player player) {
        this.playerUUID = playerUUID;
        this.player = player;
        this.location = this.getPlayer().getLocation();
        this.lastLocation = this.getPlayer().getLocation();
        this.checks = CheckManager.loadChecks(this);
    }

    //Movement data.
    private double deltaX, deltaY, deltaZ, deltaXZ, lastDeltaX, lastDeltaY, lastDeltaZ, lastDeltaXZ;
    private Location lastLocation, location;

    //Teleportation & setback data.
    private long lastSetbackTime;

    //Velocity ticks data.
    private int ticksSinceVelocity;

    //Miscellanious data
    private boolean alerts;

    public void processPacket(final Packet packet) {
        //Handle checks.
        checks.forEach(check -> check.handle(packet));
        //Process packet information.
        this.processInput(packet);

    }

    private void processInput(final Packet packet) {
        if(packet.isReceiving()) {
            if(packet.getPacketId() == PacketType.Client.POSITION || packet.getPacketId() == PacketType.Client.POSITION_LOOK) {
                WrappedPacketInFlying wrappedPacketInFlying = new WrappedPacketInFlying(packet.getRawPacket());

                final World world = this.getLocation().getWorld();
                final double x = wrappedPacketInFlying.getX();
                final double y = wrappedPacketInFlying.getY();
                final double z = wrappedPacketInFlying.getZ();
                final float yaw = wrappedPacketInFlying.getYaw();
                final float pitch = wrappedPacketInFlying.getPitch();

                Location location = new Location(world, x, y, z, yaw, pitch);
                Location lastLocation = this.getLocation() != null ? this.getLocation() : location;

                this.processLocation(location, lastLocation);
            } else if(packet.getPacketId() == PacketType.Client.LOOK) {
                WrappedPacketInFlying wrappedPacketInFlying = new WrappedPacketInFlying(packet.getRawPacket());

                final World world = this.getLocation().getWorld();
                final double x = this.getLocation().getX();
                final double y = this.getLocation().getY();
                final double z = this.getLocation().getZ();
                final float yaw = wrappedPacketInFlying.getYaw();
                final float pitch = wrappedPacketInFlying.getPitch();

                Location location = new Location(world, x, y, z, yaw, pitch);
                Location lastLocation = this.getLocation() != null ? this.getLocation() : location;

                this.processLocation(location, lastLocation);
            } else if(packet.getPacketId() == PacketType.Client.FLYING) {
                final World world = this.getLocation().getWorld();
                final double x = this.getLocation().getX();
                final double y = this.getLocation().getY();
                final double z = this.getLocation().getZ();
                final float yaw = this.getLocation().getYaw();
                final float pitch = this.getLocation().getPitch();

                Location location = new Location(world, x, y, z, yaw, pitch);
                Location lastLocation = this.getLocation() != null ? this.getLocation() : location;

                this.processLocation(location, lastLocation);
            }
        } else if(packet.isSending()) {
            if(packet.getPacketId() == PacketType.Server.ENTITY_VELOCITY) {
                this.ticksSinceVelocity = 0;
            }
        }
    }

    private void processLocation(Location location, Location lastLocation) {
        this.lastLocation = lastLocation;
        this.location = location;
        //Bukkit.broadcastMessage(this.lastLocation.getX() + "");

        double lastDeltaX = deltaX;
        double deltaX = location.getX() - lastLocation.getX();

        this.lastDeltaX = lastDeltaX;
        this.deltaX = deltaX;

        double lastDeltaY = deltaY;
        double deltaY = location.getY() - lastLocation.getY();

        this.lastDeltaY = lastDeltaY;
        this.deltaY = deltaY;

        double lastDeltaZ = deltaZ;
        double deltaZ = location.getZ() - lastLocation.getZ();

        this.lastDeltaZ = lastDeltaZ;
        this.deltaZ = deltaZ;

        double lastDeltaXZ = deltaXZ;
        double deltaXZ = location.clone().toVector().setY(0.0).distance(lastLocation.clone().toVector().setY(0.0));

        this.lastDeltaXZ = lastDeltaXZ;
        this.deltaXZ = deltaXZ;
    }
}
