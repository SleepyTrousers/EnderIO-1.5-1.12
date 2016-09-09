package crazypants.enderio.machine.generator.stirling;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.common.util.IProgressTile;
import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.ModObject;
import crazypants.enderio.capacitor.DefaultCapacitorData;
import crazypants.enderio.capacitor.ICapacitorData;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.generator.AbstractGeneratorEntity;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.power.PowerDistributor;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;

import static crazypants.enderio.capacitor.CapacitorKey.STIRLING_POWER_BUFFER;
import static crazypants.enderio.capacitor.CapacitorKey.STIRLING_POWER_GEN;
import static crazypants.enderio.capacitor.CapacitorKey.STIRLING_POWER_TIME;

@Storable
public class TileEntityStirlingGenerator extends AbstractGeneratorEntity implements IProgressTile, IPaintable.IPaintableTileEntity {

  // public for alloy smelter
  public static final String SOUND_NAME = "generator.stirling";

  /** How many ticks left until the item is burnt. */
  @Store
  public int burnTime = 0;
  @Store
  public int totalBurnTime;
  @Store
  public boolean isLavaFired;

  private PowerDistributor powerDis;

  public TileEntityStirlingGenerator() {
    super(new SlotDefinition(1, 0), null, STIRLING_POWER_BUFFER, STIRLING_POWER_GEN);
  }

  @Override
  public @Nonnull String getMachineName() {
    return ModObject.blockStirlingGenerator.getUnlocalisedName();
  }

  @Override
  public @Nonnull String getName() {
    return "Stirling Generator";
  }

  @Override
  public boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    return TileEntityFurnace.isItemFuel(itemstack);
  }

  @Override
  public @Nonnull int[] getSlotsForFace(EnumFacing var1) {
    return new int[] { 0 };
  }

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
  public void onNeighborBlockChange(Block blockId) {
    super.onNeighborBlockChange(blockId);
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
        if (inventory[0] != null && inventory[0].stackSize > 0) {
          burnTime = getBurnTime(inventory[0]);
          if (burnTime > 0) {
            totalBurnTime = burnTime;
            isLavaFired = inventory[0].getItem() == Items.LAVA_BUCKET;
            ItemStack containedItem = inventory[0].getItem().getContainerItem(inventory[0]);
            if (containedItem != null) {
              if (inventory[0].stackSize == 1) {
                inventory[0] = containedItem;
              } else {
                decrStackSize(0, 1);
                worldObj.spawnEntityInWorld(new EntityItem(worldObj, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, containedItem));
              }
            } else {
              decrStackSize(0, 1);
            }
            needsUpdate = true;
          }
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
    if (dir == null) {
      return false;
    }
    if (inventory[0] == null) {
      return false;
    }
    if (!shouldDoWorkThisTick(20)) {
      return false;
    }

    if (!canExtractItem(0, inventory[0], dir)) {
      return false;
    }
    return doPush(dir);
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
      powerDis = new PowerDistributor(new BlockCoord(this));
    }
    int canTransmit = Math.min(getEnergyStored(), getPowerUsePerTick() * 2);
    if (canTransmit <= 0) {
      return false;
    }
    int transmitted = powerDis.transmitEnergy(worldObj, canTransmit);
    setEnergyStored(getEnergyStored() - transmitted);
    return transmitted > 0;
  }

  @Override
  public boolean hasCustomName() {
    return false;
  }

}
