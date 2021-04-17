package com.juanmuscaria.event_assistant;

import com.juanmuscaria.event_assistant.interfacing.SafeInterfacing;
import com.juanmuscaria.event_assistant.utils.Task;
import com.juanmuscaria.event_assistant.utils.TaskHandler;
import com.juanmuscaria.event_assistant.utils.fakeplayer.BetterFakePlayer;
import com.juanmuscaria.event_assistant.utils.tracking.ITrackableTileEntity;
import com.juanmuscaria.event_assistant.utils.tracking.User;
import com.juanmuscaria.event_assistant.utils.tracking.UserManager;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;

public class TileTrackingListener {
    static final TileTrackingListener INSTANCE = new TileTrackingListener();

    private TileTrackingListener() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void placeEvent(BlockEvent.PlaceEvent event) {
        if (event.isCanceled()) return;
        if (event.player instanceof BetterFakePlayer) {
            User owner = ((BetterFakePlayer) event.player).getOwner();
            EntityPlayer player = MinecraftServer.getServer().getConfigurationManager().func_152612_a(owner.getPlayerName());
            if (player != null) {
                TaskHandler.INSTANCE.addTask(new SetOwner(event.x, event.y, event.z, player, event.world));
            }
        } else if (!SafeInterfacing.$.isFakePlayer(event.player)) {
            TaskHandler.INSTANCE.addTask(new SetOwner(event.x, event.y, event.z, event.player, event.world));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void multiPlaceEvent(BlockEvent.MultiPlaceEvent event) {
        if (event.isCanceled()) return;
        if (event.player instanceof BetterFakePlayer) {
            User owner = ((BetterFakePlayer) event.player).getOwner();
            EntityPlayer player = MinecraftServer.getServer().getConfigurationManager().func_152612_a(owner.getPlayerName());
            if (player != null) {
                placeEvent(event);
                event.getReplacedBlockSnapshots().forEach(block -> {
                    TaskHandler.INSTANCE.addTask(new SetOwner(block.x, block.y, block.z, player, event.world));
                });
            }
        } else if (!SafeInterfacing.$.isFakePlayer(event.player)) {
            placeEvent(event);
            event.getReplacedBlockSnapshots().forEach(block -> {
                TaskHandler.INSTANCE.addTask(new SetOwner(block.x, block.y, block.z, event.player, event.world));
            });
        }
    }
}

class SetOwner extends Task {
    private final int x;
    private final int y;
    private final int z;
    private final EntityPlayer player;
    private final World world;

    SetOwner(int x, int y, int z, EntityPlayer player, World world) {
        super(1);
        this.x = x;
        this.y = y;
        this.z = z;
        this.player = player;
        this.world = world;
    }

    @Override
    public void run() {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile == null) return;
        ((ITrackableTileEntity) tile).setOwner(UserManager.fromPlayer(player));
    }
}
