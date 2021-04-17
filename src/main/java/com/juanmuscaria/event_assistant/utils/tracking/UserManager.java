package com.juanmuscaria.event_assistant.utils.tracking;

import com.google.common.collect.MapMaker;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

/**
 * A class to manage User instances and save some memory, it will always try to keep one user instance per user.
 * Players the server never saw before and are offline will not be cached.
 *
 * @author juanmuscaria
 */
public final class UserManager {
    private static final User nobody = new User("[nobody]");
    private static final ConcurrentMap<UUID, User> userCache = new MapMaker()
            .weakValues()
            .makeMap();

    private UserManager() {
    }

    /**
     * Read an User from an NBTTagCompound.
     *
     * @param nbt - an instance of the NBTTagCompound to read.
     * @return An instance of User containing player data, if the given data is invalid, returns a Nobody User.
     */
    @NotNull
    public static User readFromNbt(@NotNull NBTTagCompound nbt) {
        String uuidString = nbt.getString("EventAssistantUser.uuid");
        String login = nbt.getString("EventAssistantUser.login");
        if (nobody().getPlayerName().equals(login))
            return nobody();
        try {
            UUID uuid = UUID.fromString(uuidString);
            User user = userCache.get(uuid);
            if (user == null) { //First time we see this user, put it in the cache
                user = login != null && !login.isEmpty() ? new User(uuid, login) : new User(uuid);
                userCache.put(user.getUUID(), user);
            }
            return user;
        } catch (IllegalArgumentException e) {
            if (login != null && !login.isEmpty()) {
                //Finding a player by name is slow, but let's hope nobody use it in an offline server
                User user = findByName(login);
                if (user == null) { //No user found, we will have to create an offline player
                    user = new User(login);
                    //Let's not cache an offline player.
                }
                return user;
            } else {
                return nobody();
            }
        }
    }

    /**
     * Checks if an NBTTagCompound contains a User.
     *
     * @param nbt - an instance of the NBTTagCompound to check.
     * @return True if the tag contains a User.
     */
    public static boolean existsInNbt(@NotNull NBTTagCompound nbt) {
        return nbt.hasKey("EventAssistantUser.uuid") || nbt.hasKey("EventAssistantUser.login");
    }

    /**
     * Gets the user of a player.
     *
     * @param player - the player to get the user from.
     * @return An user of the player.
     */
    public static User fromPlayer(EntityPlayer player) {
        return fromProfile(Objects.requireNonNull(player).getGameProfile());
    }

    /**
     * Gets the user from a complete game profile.
     *
     * @param profile - the game profile to get the user from.
     * @return An user of the game profile.
     * @throws IllegalArgumentException if the game profile is incomplete.
     */
    public static User fromProfile(GameProfile profile) {
        if (!profile.isComplete())
            throw new IllegalArgumentException("Cannot get a user from an incomplete game profile");
        User user = userCache.get(profile.getId());
        if (user == null) {
            user = new User(profile.getId(), profile.getName());
            userCache.put(user.getUUID(), user);
        }
        return user;
    }

    /**
     * Tries to find a user with that uuid in the cache
     *
     * @param uuid the uuid of the user
     * @return the user if found
     */
    @Nullable
    public static User findByUuid(UUID uuid) {
        return userCache.get(uuid);
    }

    /**
     * Tries to find a user with that name in the cache
     *
     * @param name the name of the user.
     * @return the user if found
     */
    @Nullable
    public static User findByName(String name) {
        for (User user : userCache.values()) {
            if (user.getPlayerName().equals(name))
                return user;
        }
        return null;
    }

    /**
     * A singleton instance of a user placeholder;
     *
     * @return the nobody
     */
    public static User nobody() {
        return nobody;
    }

}
