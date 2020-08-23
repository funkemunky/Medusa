package com.gladurbad.antimovehack.check.impl.fastclimb;

import com.gladurbad.antimovehack.check.Check;
import com.gladurbad.antimovehack.check.CheckInfo;
import com.gladurbad.antimovehack.network.Packet;
import com.gladurbad.antimovehack.playerdata.PlayerData;
import com.gladurbad.antimovehack.util.CollisionUtil;


@CheckInfo(name = "FastClimb", type = "A", dev = false)
public class FastClimbA extends Check {

    public FastClimbA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isReceiving() && isFlyingPacket(packet)) {
            if(CollisionUtil.isCollidingWithClimbable(data.getPlayer()) && data.getDeltaY() > 0.1177) {
                increaseBuffer();
                if (buffer > 5) {
                    failAndSetback();
                }
            } else {
                decreaseBuffer();
                setLastLegitLocation(data.getPlayer().getLocation());
            }
        }
    }
}