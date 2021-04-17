package com.juanmuscaria.event_assistant.utils.tracking;

import com.juanmuscaria.event_assistant.utils.fakeplayer.FakePlayerManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldServer;
import org.jetbrains.annotations.NotNull;

public class TileTracker {
    private TileTracker() {
    }

    /**
     * Checks if tile tracking is available.
     *
     * @return True if tiles are trackable.
     */
    public static boolean isAvailable() {
        return ITrackableTileEntity.class.isAssignableFrom(TileEntity.class);
    }

    /**
     * Attempts to get the owner of a tile.
     * When tracking is unavailable it will always return nobody.
     *
     * @param tile - the tile to get the owner from.
     * @return The tile owner.
     */
    @NotNull
    public static User getOwner(@NotNull TileEntity tile) {
        if (tile instanceof ITrackableTileEntity) {
            return ((ITrackableTileEntity) tile).getOwner();
        } else {
            return UserManager.nobody();
        }
    }

    /**
     * Attempts to set the owner of a tile
     * Will have no operation if tracking is unavailable.
     *
     * @param tile  - the tile to set the owner.
     * @param owner - th new tile owner.
     */
    public static void setOwner(@NotNull TileEntity tile, @NotNull User owner) {
        if (tile instanceof ITrackableTileEntity) {
            ((ITrackableTileEntity) tile).setOwner(owner);
        }
    }

    /**
     * Gets the fake player of this tile.
     * Will return a generic fake player if the tile has no owner or if tracking is unavailable.
     *
     * @param tile - the tile to get a fake player from.
     * @return The fake player of the tile.
     */
    @NotNull
    public static EntityPlayer getFakePlayer(@NotNull TileEntity tile) {
        if (tile instanceof ITrackableTileEntity) {
            return ((ITrackableTileEntity) tile).getFakePlayer();
        } else {
            return FakePlayerManager.get((WorldServer) tile.getWorldObj(),
                    new ChunkCoordinates(tile.xCoord, tile.yCoord, tile.zCoord));
        }
    }
}
