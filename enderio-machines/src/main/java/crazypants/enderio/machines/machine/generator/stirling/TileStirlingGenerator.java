package crazypants.enderio.machines.machine.generator.stirling;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.common.util.IProgressTile;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.capability.ItemTools;
import crazypants.enderio.base.capability.ItemTools.MoveResult;
import crazypants.enderio.base.capacitor.DefaultCapacitorData;
import crazypants.enderio.base.capacitor.ICapacitorData;
import crazypants.enderio.base.capacitor.ICapacitorKey;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.power.PowerDistributor;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.machines.machine.generator.AbstractGeneratorEntity;
import crazypants.enderio.machines.network.PacketHandler;
import crazypants.enderio.util.Prep;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static crazypants.enderio.machines.capacitor.CapacitorKey.SIMPLE_STIRLING_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.SIMPLE_STIRLING_POWER_GEN;
import static crazypants.enderio.machines.capacitor.CapacitorKey.SIMPLE_STIRLING_POWER_LOSS;
import static crazypants.enderio.machines.capacitor.CapacitorKey.STIRLING_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.STIRLING_POWER_GEN;
import static crazypants.enderio.machines.capacitor.CapacitorKey.STIRLING_POWER_LOSS;
import static crazypants.enderio.machines.capacitor.CapacitorKey.STIRLING_POWER_TIME;

@Storable
public class TileStirlingGenerator extends AbstractGeneratorEntity implements IProgressTile, IPaintable.IPaintableTileEntity {

  public static class Simple extends TileStirlingGenerator {

    public Simple() {
      super(new SlotDefinition(1, 0, 0), SIMPLE_STIRLING_POWER_LOSS, SIMPLE_STIRLING_POWER_BUFFER, SIMPLE_STIRLING_POWER_GEN);
    }

    @Override
    public boolean isMachineItemValidForSlot(int i, @Nonnull ItemStack itemstack) {
      return super.isMachineItemValidForSlot(i, itemstack) && Prep.isInvalid(itemstack.getItem().getContainerItem(itemstack));
    }

  }

  private static final @Nonnull ResourceLocation SOUND = new ResourceLocation(EnderIO.DOMAIN, "generator.stirling");

  /** How many ticks left until the item is burnt. */
  @Store
  public int burnTime = 0;
  @Store
  public int totalBurnTime;
  @Store
  public boolean isLavaFired;

  private PowerDistributor powerDis;

  public TileStirlingGenerator() {
    super(new SlotDefinition(1, 0, 1), STIRLING_POWER_LOSS, STIRLING_POWER_BUFFER, STIRLING_POWER_GEN);
  }

  protected TileStirlingGenerator(@Nonnull SlotDefinition slotDefinition, @Nonnull ICapacitorKey maxEnergyRecieved, @Nonnull ICapacitorKey maxEnergyStored,
      @Nonnull ICapacitorKey maxEnergyUsed) {
    super(slotDefinition, maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);
  }

  @Override
  public @Nonnull String getMachineName() {
    return MachineObject.block_stirling_generator.getUnlocalisedName();
  }

  @Override
  public boolean isMachineItemValidForSlot(int i, @Nonnull ItemStack itemstack) {
    return TileEntityFurnace.isItemFuel(itemstack);
  }

  // @Override
  // public @Nonnull int[] getSlotsForFace(EnumFacing var1) {
  // return new int[] { 0 };
  // }

  @Override
  public boolean canInsertItem(int i, @Nonnull ItemStack itemstack, @Nonnull EnumFacing j) {
    return isItemValidForSlot(i, itemstack);
  }

  @Override
  public boolean canExtractItem(int i, @Nonnull ItemStack itemstack, @Nonnull EnumFacing j) {
    return !TileEntityFurnace.isItemFuel(itemstack);
  }

  @Override
  public boolean isActive() {
    return burnTime > 0;
  }

  @Override
  public float getProgress() {
    if (totalBurnTime <= 0) {
      return 0;
    }
    return (float) burnTime / (float) totalBurnTime;
  }

  @Override
  public void setProgress(float progress) {
    burnTime = (int) (totalBurnTime * progress);
  }

  @Override
  public @Nonnull TileEntity getTileEntity() {
    return this;
  }

  @Override
  public ResourceLocation getSound() {
    return SOUND;
  }

  @Override
  public void onNeighborBlockChange(@Nonnull IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos posIn, @Nonnull Block blockIn,
      @Nonnull BlockPos fromPos) {
    super.onNeighborBlockChange(state, worldIn, posIn, blockIn, fromPos);
    if (powerDis != null) {
      powerDis.neighboursChanged();
    }
  }

  public static int getBurnTimeGeneric(@Nonnull ItemStack item) {
    return Math.round(TileEntityFurnace.getItemBurnTime(item));
  }

  public int getBurnTime(@Nonnull ItemStack item) {
    return Math.round(TileEntityFurnace.getItemBurnTime(item) * getBurnTimeMultiplier());
  }

  @Override
  protected boolean processTasks(boolean redstoneCheck) {
    boolean needsUpdate = false;
    boolean sendBurnTimePacket = false;

    if (burnTime > 0) {
      if (getEnergyStored() < getMaxEnergyStored()) {
        setEnergyStored(getEnergyStored() + getPowerUsePerTick());
      }
      burnTime--;
      sendBurnTimePacket = shouldDoWorkThisTick(20, -1) || burnTime == 0;
    }

    transmitEnergy();
    usePower(getPowerLossPerTick());

    if (redstoneCheck && burnTime <= 0 && getEnergyStored() < getMaxEnergyStored()) {
      final ItemStack fuelStack = inventory[0];
      if (fuelStack != null && Prep.isValid(fuelStack)) {
        burnTime = getBurnTime(fuelStack);
        if (burnTime > 0) {
          totalBurnTime = burnTime;
          isLavaFired = fuelStack.getItem() == Items.LAVA_BUCKET;
          ItemStack containedItem = fuelStack.getItem().getContainerItem(fuelStack.splitStack(1));
          if (Prep.isValid(containedItem)) {
            if (Prep.isInvalid(fuelStack)) {
              inventory[0] = containedItem;
            } else {
              world.spawnEntity(new EntityItem(world, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, containedItem));
            }
          }
          markDirty();
          needsUpdate = true;
        }
      }
    }

    if (!needsUpdate && sendBurnTimePacket) {
      PacketHandler.sendToAllAround(new PacketBurnTime(this), this);
    }

    return needsUpdate;
  }

  @Override
  protected boolean doPush(@Nullable EnumFacing dir) {
    final ItemStack fuelStack = inventory[0];
    if (dir == null || fuelStack == null || Prep.isInvalid(fuelStack) || !shouldDoWorkThisTick(20)) {
      return false;
    }
    if (!canExtractItem(0, fuelStack, dir)) {
      return false;
    }
    MoveResult res = ItemTools.move(getPushLimit(), world, getPos(), dir, getPos().offset(dir), dir.getOpposite());
    if (res == MoveResult.MOVED) {
      markDirty();
      return true;
    }
    return false;
  }

  public static float getEnergyMultiplier(@Nonnull ICapacitorData capacitorType) {
    return STIRLING_POWER_GEN.get(capacitorType) / STIRLING_POWER_GEN.get(DefaultCapacitorData.BASIC_CAPACITOR);
  }

  public static float getBurnTimeMultiplier(@Nonnull ICapacitorData capacitorType) {
    return STIRLING_POWER_TIME.getFloat(capacitorType);
  }

  public float getBurnTimeMultiplier() {
    return getBurnTimeMultiplier(getCapacitorData());
  }

  private boolean transmitEnergy() {
    if (powerDis == null) {
      powerDis = new PowerDistributor(getPos());
    }
    int canTransmit = Math.min(getEnergyStored(), getPowerUsePerTick() * 2);
    if (canTransmit <= 0) {
      return false;
    }
    int transmitted = powerDis.transmitEnergy(world, canTransmit);
    setEnergyStored(getEnergyStored() - transmitted);
    return transmitted > 0;
  }

}
