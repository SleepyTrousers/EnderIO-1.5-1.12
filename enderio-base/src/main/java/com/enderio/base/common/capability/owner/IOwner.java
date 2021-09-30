package com.enderio.base.common.capability.owner;

import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

// A capability for the Grave BE
public interface IOwner extends INBTSerializable<CompoundTag>{
    
    UUID getUUID();
    void setUUID(UUID uuid);
    
   
}
