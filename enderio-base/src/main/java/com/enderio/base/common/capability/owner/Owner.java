package com.enderio.base.common.capability.owner;

import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

public class Owner implements IOwner {
    private GameProfile owner;

    public Owner() {

    }

    @Override
    public GameProfile getProfile() {
        return owner;
    }

    @Override
    public void setProfile(GameProfile profile, ProfileSetCallback callback) {
        synchronized (this) {
            owner = profile;
        }

        // Perform update.
        SkullBlockEntity.updateGameprofile(owner, (p_155747_) -> {
            owner = p_155747_;
            callback.profileSet(owner);
        });
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        if (owner != null) {
            CompoundTag ownerTag = new CompoundTag();
            NbtUtils.writeGameProfile(ownerTag, owner);
            tag.put("Owner", ownerTag);
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("Owner")) {
            owner = NbtUtils.readGameProfile(nbt.getCompound("Owner"));
        }
    }
}
