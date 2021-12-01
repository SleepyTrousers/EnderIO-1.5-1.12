package com.enderio.base.common.capability.owner;

import com.enderio.core.common.capability.INamedNBTSerializable;
import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.CompoundTag;

/**
 * A capability holding a game profile declaring the owner of something.
 */
public interface IOwner extends INamedNBTSerializable<CompoundTag> {

    @Override
    default String getSerializedName() {
        return "Owner";
    }

    /**
     * Get the owner's profile.
     */
    GameProfile getProfile();

    /**
     * Set the owner's profile.
     */
    default void setProfile(GameProfile profile) {
        setProfile(profile, prof -> {});
    }

    /**
     * Set the owner's profile.
     * Add a callback used once the profile has been set, allows you to update the client.
     */
    void setProfile(GameProfile profile, ProfileSetCallback callback);

    interface ProfileSetCallback {
        void profileSet(GameProfile profile);
    }

}
