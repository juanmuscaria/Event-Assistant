package com.juanmuscaria.event_assistant.mixin.tracking;

import com.juanmuscaria.event_assistant.utils.fakeplayer.FakePlayerManager;
import com.juanmuscaria.event_assistant.utils.tracking.ITrackableTileEntity;
import com.juanmuscaria.event_assistant.utils.tracking.User;
import com.juanmuscaria.event_assistant.utils.tracking.UserManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(TileEntity.class)
public abstract class MixinTileEntity implements ITrackableTileEntity {
    @Shadow
    public int xCoord;
    @Shadow
    public int yCoord;
    @Shadow
    public int zCoord;
    User ownerEa = UserManager.nobody();

    @Shadow
    public abstract void markDirty();

    @Shadow
    public abstract World getWorldObj();

    @Override
    public User getOwner() {
        return ownerEa;
    }

    @Override
    public void setOwner(User owner) {
        ownerEa = owner;
        markDirty();
    }

    @Override
    public EntityPlayer getFakePlayer() {
        return FakePlayerManager.getOrConfigure((WorldServer) getWorldObj(),
                new ChunkCoordinates(xCoord, yCoord, zCoord),
                ownerEa);
    }

    @Override
    public void withFakePlayer(Consumer<EntityPlayerMP> consumer) {
        FakePlayerManager.withFakePlayer((WorldServer) getWorldObj(),
                new ChunkCoordinates(xCoord, yCoord, zCoord),
                ownerEa, consumer);
    }

    @Inject(method = "readFromNBT", at = @At("HEAD"))
    private void readInject(NBTTagCompound nbtTagCompound, CallbackInfo callback) {
        if (UserManager.existsInNbt(nbtTagCompound))
            ownerEa = UserManager.readFromNbt(nbtTagCompound);
    }

    @Inject(method = "writeToNBT", at = @At("HEAD"))
    private void writeInject(NBTTagCompound nbtTagCompound, CallbackInfo callback) {
        ownerEa.saveToNbt(nbtTagCompound);
    }
}
