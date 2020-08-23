package com.gladurbad.antimovehack.check.impl.jesus;

import com.gladurbad.antimovehack.check.Check;
import com.gladurbad.antimovehack.check.CheckInfo;
import com.gladurbad.antimovehack.network.Packet;
import com.gladurbad.antimovehack.playerdata.PlayerData;
import com.gladurbad.antimovehack.util.CollisionUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@CheckInfo(name = "Jesus", type = "B", dev = false)
public class JesusB extends Check {

    public JesusB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isReceiving() && isFlyingPacket(packet)) {
            final Player player = data.getPlayer();

            final boolean invalid = CollisionUtil.isOnChosenBlock(player, 0.1, Material.WATER, Material.STATIONARY_WATER) &&
                    !CollisionUtil.isOnSolid(player) && data.getDeltaY() == 0.0D;

            if (invalid) {
                increaseBuffer();
                if (buffer > 10) {
                    failAndSetback();
                }
            } else {
                decreaseBuffer();
                setLastLegitLocation(player.getLocation());
            }
        }
    }
}