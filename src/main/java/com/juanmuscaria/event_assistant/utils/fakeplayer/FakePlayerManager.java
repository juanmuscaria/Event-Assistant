package com.juanmuscaria.event_assistant.utils.fakeplayer;

import com.google.common.collect.MapMaker;
import com.juanmuscaria.event_assistant.utils.UuidUtils;
import com.juanmuscaria.event_assistant.utils.tracking.User;
import com.juanmuscaria.event_assistant.utils.tracking.UserManager;
import com.mojang.authlib.GameProfile;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldServer;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentMap;

/**
 * A class to help creating and configuring fake players without using much resources.
 * Do not hold any fake player returned by this, they will be reused and modified at any time.
 */
public class FakePlayerManager {
    private static final GameProfile featherModProfile = new GameProfile(UuidUtils.offlineUUID("[Fake]"), "[Fake]");
    private static final ConcurrentMap<String, BetterFakePlayer> cache = new MapMaker()
            .weakValues()
            .makeMap();

    private static SoftReference<BetterFakePlayer> featherModFakePlayer = new SoftReference<>(null);

    /**
     * Get or create a fake player.
     * Please do not keep or hold the instance of the fake player, it's information may change after use including the world and position.
     *
     * @param world the world the fake player will be
     * @param pos the position the fake player will be
     * @param name the name of the fake player
     * @return a configured fake player, the object may be reused and reconfigured by other calls, prefer to call this method again when a fake player is needed.
     */
    @NotNull
    public static BetterFakePlayer get(WorldServer world, ChunkCoordinates pos, String name) {
        BetterFakePlayer fakePlayer = cache.get(name);
        if (fakePlayer == null) {
            String fName = "[" + name + "]";
            GameProfile profile = new GameProfile(UuidUtils.offlineUUID(fName), fName);
            fakePlayer = new BetterFakePlayer(world, profile, UserManager.nobody());
            cache.put(name, fakePlayer);
        }
        fakePlayer.worldObj = world;
        fakePlayer.setFakePosition(pos);
        return fakePlayer;
    }

    /**
     * Get or create a fake player.
     * Please do not keep or hold the instance of the fake player, it's information may change after use including the world and position.
     *
     * @param world the world the fake player will be
     * @param pos the position the fake player will be
     * @param owner the user owning this fake player with may receive proxied chat messages and help with tracking.
     *              The resulting fake player will have the same name of the owner wrapped around "[]"
     * @return a configured fake player, the object may be reused and reconfigured by other calls, prefer to call this method again when a fake player is needed.
     */
    public static BetterFakePlayer get(WorldServer world, ChunkCoordinates pos, User owner) {
        if (owner.equals(UserManager.nobody())) {
            return get(world, pos);
        }
        BetterFakePlayer fakePlayer = cache.get(owner.getPlayerName());
        if (fakePlayer == null) {
            String fName = "[" + owner.getPlayerName() + "]";
            GameProfile profile = new GameProfile(UuidUtils.offlineUUID(fName), fName);
            fakePlayer = new BetterFakePlayer(world, profile, owner);
            cache.put(owner.getPlayerName(), fakePlayer);
        }
        fakePlayer.worldObj = world;
        fakePlayer.setFakePosition(pos);
        return fakePlayer;
    }

    /**
     * Get or create a fake player.
     * Please do not keep or hold the instance of the fake player, it's information may change after use including the world and position.
     *
     * @param world the world the fake player will be
     * @param pos the position the fake player will be
     * @return a configured fake player, the object may be reused and reconfigured by other calls, prefer to call this method again when a fake player is needed.
     * Fake players returned by this method will always have the name "[Fake]" and will always be owned by nobody
     */
    public static BetterFakePlayer get(WorldServer world, ChunkCoordinates pos) {
        BetterFakePlayer player = featherModFakePlayer.get();
        if (player == null) {
            player = new BetterFakePlayer(world, featherModProfile, UserManager.nobody());
            featherModFakePlayer = new SoftReference<>(player);
        }
        player.worldObj = world;
        player.setFakePosition(pos);
        return player;
    }

}
