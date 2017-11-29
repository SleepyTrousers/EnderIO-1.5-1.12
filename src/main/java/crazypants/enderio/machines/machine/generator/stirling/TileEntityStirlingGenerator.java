package crazypants.enderio.machines.machine.generator.stirling;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.common.util.IProgressTile;

import crazypants.enderio.capability.ItemTools;
import crazypants.enderio.capability.ItemTools.MoveResult;
import crazypants.enderio.capacitor.DefaultCapacitorData;
import crazypants.enderio.capacitor.ICapacitorData;
import crazypants.enderio.machine.baselegacy.SlotDefinition;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.machines.machine.generator.AbstractGeneratorEntity;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.power.PowerDistributor;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static crazypants.enderio.machines.capacitor.CapacitorKey.STIRLING_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.STIRLING_POWER_GEN;
import static crazypants.enderio.machines.capacitor.CapacitorKey.STIRLING_POWER_LOSS;
import static crazypants.enderio.machines.capacitor.CapacitorKey.STIRLING_POWER_TIME;

@Storable
public class TileEntityStirlingGenerator extends AbstractGeneratorEntity implements IProgressTile, IPaintable.IPaintableTileEntity {

  private static final String SOUND_NAME = "generator.stirling";

  /** How many ticks left until the item is burnt. */
  @Store
  public int burnTime = 0;
  @Store
  public int totalBurnTime;
  @Store
  public boolean isLavaFired;

  private PowerDistributor powerDis;

  public TileEntityStirlingGenerator() {
    super(new SlotDefinition(1, 0), STIRLING_POWER_LOSS, STIRLING_POWER_BUFFER, STIRLING_POWER_GEN);
  }

  @Override
  public @Nonnull String getMachineName() {
    return MachineObject.block_stirling_generator.getUnlocalisedName();
  }

  @Override
  public boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    return TileEntityFurnace.isItemFuel(itemstack);
  }

//  @Override
//  public @Nonnull int[] getSlotsForFace(EnumFacing var1) {
//    return new int[] { 0 };
//  }

  @Override
  public boolean canInsertItem(int i, ItemStack itemstack, EnumFacing j) {
    return isItemValidForSlot(i, itemstack);
  }

  @Override
  public boolean canExtractItem(int i, ItemStack itemstack, EnumFacing j) {
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
  public TileEntity getTileEntity() {
    return this;
  }

  @Override
  public String getSoundName() {
    return SOUND_NAME;
  }

  @Override
  public void onNeighborBlockChange(IBlockState state, World worldIn, BlockPos posIn, Block blockIn, BlockPos fromPos) {
    super.onNeighborBlockChange(state, worldIn, posIn, blockIn, fromPos);
    if (powerDis != null) {
      powerDis.neighboursChanged();
    }
  }

  public int getBurnTime(ItemStack item) {
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

    if (redstoneCheck) {

      if (burnTime <= 0 && getEnergyStored() < getMaxEnergyStored()) {
        if (inventory[0] != null && inventory[0].getCount() > 0) {
          burnTime = getBurnTime(inventory[0]);
          if (burnTime > 0) {
            totalBurnTime = burnTime;
            isLavaFired = inventory[0].getItem() == Items.LAVA_BUCKET;
            ItemStack containedItem = inventory[0].getItem().getContainerItem(inventory[0]);
            if (containedItem != null) {
              if (inventory[0].getCount() == 1) {
                inventory[0] = containedItem;
              } else {
                decrStackSize(0, 1);
                world.spawnEntity(new EntityItem(world, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, containedItem));
              }
            } else {
              decrStackSize(0, 1);
            }
            needsUpdate = true;
          }
        }
      }
      usePower(getPowerLossPerTick()); // power loss over time, defaults to 0
    }
    if (!needsUpdate && sendBurnTimePacket) {
      PacketHandler.sendToAllAround(new PacketBurnTime(this), this);
    }

    return needsUpdate;
  }

  @Override
  protected boolean doPush(@Nullable EnumFacing dir) {
    if (dir == null || inventory[0] == null || !shouldDoWorkThisTick(20)) {
      return false;
    }
    if (!canExtractItem(0, inventory[0], dir)) {
      return false;
    }
    MoveResult res = ItemTools.move(getPushLimit(), world, getPos(), dir, getPos().offset(dir), dir.getOpposite());
    if(res == MoveResult.MOVED) {
      markDirty();
      return true;
    }
    return false;
  }

  public static float getEnergyMultiplier(ICapacitorData capacitorType) {
    return STIRLING_POWER_GEN.get(capacitorType) / STIRLING_POWER_GEN.get(DefaultCapacitorData.BASIC_CAPACITOR);
  }

  public static float getBurnTimeMultiplier(ICapacitorData capacitorType) {
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
