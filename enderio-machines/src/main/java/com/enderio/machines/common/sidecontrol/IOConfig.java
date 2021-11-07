package com.enderio.machines.common.sidecontrol;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.EnumMap;
import java.util.Map;

public class IOConfig implements INBTSerializable<CompoundTag> {

    private final EnumMap<Direction, IOState> config = new EnumMap(Direction.class);

    public IOConfig() {
        for (Direction value : Direction.values()) {
            config.put(value, IOState.NONE);
        }
    }

    public IOState getIO(Direction direction) {
        return config.get(direction);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        ListTag listNbt = new ListTag();
        for (Map.Entry<Direction, IOState> entry : config.entrySet()) {
            CompoundTag entryNbt = new CompoundTag();
            entryNbt.putInt("direction", entry.getKey().ordinal());
            entryNbt.putInt("state", entry.getValue().ordinal());
            listNbt.add(entryNbt);
        }
        nbt.put("data", listNbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ListTag listNbt = nbt.getList("data", Constants.NBT.TAG_COMPOUND);
        for (Tag tag : listNbt) {
            CompoundTag entryNbt = (CompoundTag) tag;
            config.put(Direction.values()[entryNbt.getInt("direction")],
                IOState.values()[entryNbt.getInt("state")]);
        }
    }

    public enum IOState {
        NONE(true, true, false),
        PUSH(false, true, true),
        PULL(true, false, true),
        BOTH(true, true, true),
        DISABLED(false, false, false);

        private final boolean input, output, force;

        IOState(boolean input, boolean output, boolean force) {
            this.input = input;
            this.output = output;
            this.force = force;
        }

        public boolean canInput() {
            return input;
        }

        public boolean canOutput() {
            return output;
        }

        public boolean canPush() {
            return canOutput() && canForce();
        }

        public boolean canPull() {
            return canInput() && canForce();
        }

        public boolean canForce() {
            return force;
        }
    }
}
