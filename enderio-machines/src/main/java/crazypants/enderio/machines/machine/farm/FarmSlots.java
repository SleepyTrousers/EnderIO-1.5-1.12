package crazypants.enderio.machines.machine.farm;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import net.minecraft.item.ItemStack;

public enum FarmSlots {
  TOOL1(0),
  TOOL2(1),
  TOOL3(2),
  FERT1(3),
  FERT2(4),
  SEED1(5, 0b0001),
  SEED2(6, 0b0010),
  SEED3(7, 0b0100),
  SEED4(8, 0b1000),
  OUTPUT1(9) {
    @Override
    public boolean isValid(@Nonnull TileFarmStation arg0, @Nonnull ItemStack arg1) {
      return true;
    }
  },
  OUTPUT2(10) {
    @Override
    public boolean isValid(@Nonnull TileFarmStation arg0, @Nonnull ItemStack arg1) {
      return true;
    }
  },
  OUTPUT3(11) {
    @Override
    public boolean isValid(@Nonnull TileFarmStation arg0, @Nonnull ItemStack arg1) {
      return true;
    }
  },
  OUTPUT4(12) {
    @Override
    public boolean isValid(@Nonnull TileFarmStation arg0, @Nonnull ItemStack arg1) {
      return true;
    }
  },
  OUTPUT5(13) {
    @Override
    public boolean isValid(@Nonnull TileFarmStation arg0, @Nonnull ItemStack arg1) {
      return true;
    }
  },
  OUTPUT6(14) {
    @Override
    public boolean isValid(@Nonnull TileFarmStation arg0, @Nonnull ItemStack arg1) {
      return true;
    }
  };

  public static final NNList<FarmSlots> TOOLS = new NNList<>(TOOL1, TOOL2, TOOL3);
  public static final NNList<FarmSlots> SEEDS = new NNList<>(SEED1, SEED2, SEED3, SEED4);
  public static final NNList<FarmSlots> FERTS = new NNList<>(FERT1, FERT2);
  public static final NNList<FarmSlots> OUTPUTS = new NNList<>(OUTPUT1, OUTPUT2, OUTPUT3, OUTPUT4, OUTPUT5, OUTPUT6);

  // When switching to EnderInventory we'll drop the ID and use the enum values as keys directly
  private final int id;
  private final int bitmask;

  private FarmSlots(int id) {
    this(id, 0);
  }

  private FarmSlots(int id, int bitmask) {
    this.id = id;
    this.bitmask = bitmask;
  }

  public int getBitmask() {
    return bitmask;
  }

  public @Nonnull ItemStack get(@Nonnull TileFarmStation farm) {
    return farm.getStackInSlot(id);
  }

  public void set(@Nonnull TileFarmStation farm, @Nonnull ItemStack stack) {
    farm.setInventorySlotContents(id, stack);
  }

  public int getInventoryStackLimit(@Nonnull TileFarmStation farm) {
    return farm.getInventoryStackLimit(id);
  }

  public boolean isValid(@Nonnull TileFarmStation farm, @Nonnull ItemStack stack) {
    return farm.isMachineItemValidForSlot(id, stack);
  }

}