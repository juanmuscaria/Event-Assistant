package com.juanmuscaria.event_assistant.utils.tracking;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.function.Consumer;

public interface ITrackableTileEntity {

    User getOwner();

    void setOwner(User owner);

    EntityPlayer getFakePlayer();

    void withFakePlayer(Consumer<EntityPlayerMP> consumer);
}

