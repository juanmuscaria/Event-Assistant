package com.juanmuscaria.event_assistant.utils.tracking;

import net.minecraft.entity.player.EntityPlayer;

public interface ITrackableTileEntity {

    User getOwner();

    void setOwner(User owner);

    EntityPlayer getFakePlayer();
}

