package crazypants.enderio.machines.machine.fracker;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

public class FrackingData extends WorldSavedData {

  public static @Nonnull FrackingData get(World world) {
    return NullHelper.first(getIfExists(world), () -> {
      FrackingData instance = new FrackingData();
      world.getPerWorldStorage().setData(NAME, instance);
      return instance;
    });
  }

  public static @Nullable FrackingData getIfExists(World world) {
    return (FrackingData) world.getPerWorldStorage().getOrLoadData(FrackingData.class, NAME);
  }

  private static final @Nonnull String NAME = EnderIO.DOMAIN + "_fracking";

  /*
   * Note: As long as there are less than one or two hundred fracking deposits in a world, a list is plenty quick enough for this.
   */
  private final @Nonnull NNList<Deposit> data = new NNList<>();
  private long nextTick = 0L;

  public FrackingData(String name) {
    super(name);
  }

  public FrackingData() {
    this(NAME);
  }

  @Override
  public void readFromNBT(@Nonnull NBTTagCompound nbt) {
    data.clear();
    for (NBTBase nbtBase : nbt.getTagList(NAME, Constants.NBT.TAG_COMPOUND)) {
      if (nbtBase instanceof NBTTagCompound) {
        data.add(new Deposit((NBTTagCompound) nbtBase));
      }
    }
  }

  @Override
  public @Nonnull NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
    NBTTagList tagList = new NBTTagList();
    compound.setTag(NAME, tagList);
    for (Deposit deposit : data) {
      tagList.appendTag(deposit.save());
    }
    return compound;
  }

  public void add(@Nonnull Deposit deposit) {
    data.add(deposit);
    setDirty(true);
  }

  public void remove(@Nonnull Deposit deposit) {
    data.remove(deposit);
    setDirty(true);
  }

  public @Nonnull NNList<Deposit> getInRange(@Nonnull Deposit deposit) {
    NNList<Deposit> result = new NNList<>();
    for (Deposit d : data) {
      if (d != deposit && d.inRange(deposit)) {
        result.add(d);
      }
    }
    return result;
  }

  public @Nullable Deposit getInRange(@Nonnull BlockPos pos) {
    for (Deposit d : data) {
      if (d.inRange(pos)) {
        return d;
      }
    }
    return null;
  }

  public void add(@Nonnull BlockPos pos, long amount) {
    Deposit deposit = getInRange(pos);
    if (deposit == null) {
      deposit = new Deposit(pos);
      add(deposit);
    }
    deposit.add(amount);
    for (Deposit d : getInRange(deposit)) {
      remove(deposit);
      deposit = d.combine(deposit);
      add(deposit);
      remove(d);
    }
  }

  @Nonnull
  NNList<Deposit> getData() {
    return data;
  }

  public long getNextTick() {
    return nextTick;
  }

  public void setNextTick(long nextTick) {
    this.nextTick = nextTick;
  }

}