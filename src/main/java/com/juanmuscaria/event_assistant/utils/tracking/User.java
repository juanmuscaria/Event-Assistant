package com.juanmuscaria.event_assistant.utils.tracking;

import com.juanmuscaria.event_assistant.utils.UuidUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.UsernameCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

/**
 * A simple user implementation to keep track of a player
 */
public class User {
    @Nullable
    private final UUID uuid;
    @Nullable
    private final UUID offlineUuid;
    @NotNull
    private final String playerName;

    User(@NotNull UUID uuid, @NotNull String playerName) {
        this.uuid = Objects.requireNonNull(uuid);
        this.offlineUuid = UuidUtils.offlineUUID(Objects.requireNonNull(playerName));
        this.playerName = playerName;
    }

    User(@NotNull String playerName) {
        this.uuid = null;
        this.offlineUuid = UuidUtils.offlineUUID(Objects.requireNonNull(playerName));
        this.playerName = playerName;
    }

    User(@NotNull UUID uuid) {
        this.uuid = Objects.requireNonNull(uuid);
        this.offlineUuid = null;
        this.playerName = "[" + uuid + "]";
    }


    /**
     * Get the player's name stored in this object.
     *
     * @return The name of the player.
     */
    @NotNull
    public String getPlayerName() {
        if (uuid == null && offlineUuid == null)
            throw new IllegalStateException("Incomplete user object");
        if (uuid != null) {
            String lastKnownName = UsernameCache.getLastKnownUsername(uuid);
            if (lastKnownName != null) {
                return lastKnownName;
            }
        }
        return playerName;
    }

    /**
     * Get the player's UUID stored in this object.
     *
     * @return The UUID of the player.
     */
    @NotNull
    public UUID getUUID() {
        if (uuid == null && offlineUuid == null)
            throw new IllegalStateException("Incomplete user object");
        return uuid != null ? uuid : offlineUuid;
    }

    /**
     * Get the player's UUID stored in this object.
     *
     * @return The UUID of the player as String.
     */
    @NotNull
    public String getUUIDString() {
        if (uuid == null && offlineUuid == null)
            throw new IllegalStateException("Incomplete user object");
        return uuid != null ? uuid.toString() : offlineUuid.toString();
    }

    /**
     * Saves this object to an NBTTagCompound.
     *
     * @param nbt An instance of the NBTTagCompound to save.
     */
    public void saveToNbt(@NotNull NBTTagCompound nbt) {
        if (uuid != null && !this.equals(UserManager.nobody())) {
            nbt.setString("EventAssistantUser.uuid", uuid.toString());
        }
        nbt.setString("EventAssistantUser.name", playerName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getUUID(), user.getUUID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUUID());
    }
}
