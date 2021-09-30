package com.enderio.base.common.capability.owner;

import java.util.UUID;

import net.minecraft.nbt.CompoundTag;

public class Owner implements IOwner{
    private UUID uuid;
    
    public Owner() {
        
    }
    
    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }


    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putUUID("owner", uuid);
        return nbt;
    }
    
    @Override
    public void deserializeNBT(CompoundTag nbt) {
        try {
            this.uuid = nbt.getUUID("owner");
        }catch (Exception e) {
            //null uuid
        }
    }
}
