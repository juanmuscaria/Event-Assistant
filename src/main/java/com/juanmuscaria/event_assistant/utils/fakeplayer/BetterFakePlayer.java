package com.juanmuscaria.event_assistant.utils.fakeplayer;

import com.juanmuscaria.event_assistant.EventAssistant;
import com.juanmuscaria.event_assistant.utils.tracking.User;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;

import java.util.Objects;
import java.util.UUID;

/**
 * A custom fake player implementation which has better tracking methods for who is the owner of it.
 */
@SuppressWarnings("EntityConstructor")
public class BetterFakePlayer extends FakePlayer {
    private final User owner;
    private ChunkCoordinates fakePos = new ChunkCoordinates(0,0,0);

    BetterFakePlayer(WorldServer world, GameProfile name, User owner) {
        super(world, name);
        this.owner = owner;
    }

    public void setFakePosition(int x, int y, int z) {
        setFakePosition(new ChunkCoordinates(x,y,z));
    }

    public void setFakePosition(ChunkCoordinates pos) {
        fakePos = Objects.requireNonNull(pos);
        setPosition(fakePos.posX, fakePos.posY, fakePos.posZ);
    }

    @Override
    public ChunkCoordinates getPlayerCoordinates() {
        return fakePos;
    }

    @Override
    public void addChatComponentMessage(IChatComponent message){
        if (EventAssistant.mod.getConfigs().isPlayerNotifyEnabled()) {
            EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().func_152612_a(owner.getPlayerName());
            if (player != null) {
                ChatComponentText toSend = new ChatComponentText(
                        String.format("§e%s at §bx:%s y:%s z:%s§r ",
                                getCommandSenderName(),
                                fakePos.posX,
                                fakePos.posY,
                                fakePos.posZ));
                toSend.appendSibling(message);
            }
        }
    }

    /**
     * Gets the user that originated this fake player, useful for tracking and logging
     * @return the owner of this fake player
     */
    public User getOwner() {
        return owner;
    }
}
