package crazypants.enderio.machine.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.power.IPowerEmitter;
import buildcraft.api.power.PowerHandler.Type;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.power.Capacitors;
import crazypants.enderio.power.IPowerInterface;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.util.BlockCoord;

public class TileEntityStirlingGenerator extends AbstractMachineEntity implements ISidedInventory, IPowerEmitter {

  public static final float ENERGY_PER_TICK = 1;

  /** How many ticks left until the item is burnt. */
  private int burnTime = 0;
  private int totalBurnTime;

  private final List<Receptor> receptors = new ArrayList<Receptor>();
  private ListIterator<Receptor> receptorIterator = receptors.listIterator();
  private boolean receptorsDirty = true;

  public TileEntityStirlingGenerator() {
    super(new SlotDefinition(1, 0), Type.ENGINE);
    configurePowerHandler();
  }

  @Override
  public void setCapacitor(Capacitors capacitorType) {
    super.setCapacitor(capacitorType);
    configurePowerHandler();
  }

  void configurePowerHandler() {
    powerHandler.configure(0, 0, 0, capacitorType.capacitor.getMaxEnergyStored());
  }

  @Override
  public boolean canEmitPowerFrom(ForgeDirection side) {
    return true;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockStirlingGenerator.unlocalisedName;
  }

  @Override
  public String getInvName() {
    return "Stirling Generator";
  }

  @Override
  public boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    return TileEntityFurnace.isItemFuel(itemstack);
  }

  @Override
  public int[] getAccessibleSlotsFromSide(int var1) {
    return new int[] { 0 };
  }

  @Override
  public boolean canInsertItem(int i, ItemStack itemstack, int j) {
    return isItemValidForSlot(i, itemstack);
  }

  @Override
  public boolean canExtractItem(int i, ItemStack itemstack, int j) {
    return false;
  }

  @Override
  public boolean isActive() {
    return burnTime > 0 && redstoneCheckPassed;
  }

  @Override
  public float getProgress() {
    if(totalBurnTime <= 0) {
      return 0;
    }
    return (float) burnTime / (float) totalBurnTime;
  }

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot) {
    super.readFromNBT(nbtRoot);
    burnTime = nbtRoot.getInteger("burnTime");
    totalBurnTime = nbtRoot.getInteger("totalBurnTime");
  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);
    nbtRoot.setInteger("burnTime", burnTime);
    nbtRoot.setInteger("totalBurnTime", totalBurnTime);
  }

  @Override
  public void onNeighborBlockChange(int blockId) {
    super.onNeighborBlockChange(blockId);
    receptorsDirty = true;
  }

  @Override
  protected boolean processTasks(boolean redstoneCheckPassed) {
    boolean needsUpdate = false;

    if(burnTime > 0 && redstoneCheckPassed) {
      powerHandler.setEnergy(powerHandler.getEnergyStored() + ENERGY_PER_TICK);
      burnTime--;
      needsUpdate = true;
    }

    needsUpdate |= transmitEnergy();

    if(redstoneCheckPassed) {

      if(burnTime <= 0 && powerHandler.getEnergyStored() < powerHandler.getMaxEnergyStored()) {
        if(inventory[0] != null && inventory[0].stackSize > 0) {
          burnTime = TileEntityFurnace.getItemBurnTime(inventory[0]);
          if(burnTime > 0) {
            totalBurnTime = burnTime;
            ItemStack containedItem = inventory[0].getItem().getContainerItemStack(inventory[0]);
            if(containedItem != null) {
              inventory[0] = containedItem;
            } else {
              decrStackSize(0, 1);
            }
          }
          needsUpdate = true;
        }
      }
    }

    return needsUpdate;
  }

  private boolean transmitEnergy() {

    if(powerHandler.getEnergyStored() <= 0) {
      powerHandler.update();
      return false;
    }
    float canTransmit = Math.min(powerHandler.getEnergyStored(), capacitorType.capacitor.getMaxEnergyExtracted());
    float transmitted = 0;

    float stored = powerHandler.getEnergyStored();
    powerHandler.update();
    float storedAfter = powerHandler.getEnergyStored();

    checkReceptors();

    if(!receptors.isEmpty() && !receptorIterator.hasNext()) {
      receptorIterator = receptors.listIterator();
    }

    int appliedCount = 0;
    int numReceptors = receptors.size();
    while (receptorIterator.hasNext() && canTransmit > 0 && appliedCount < numReceptors) {
      Receptor receptor = receptorIterator.next();
      IPowerInterface pp = receptor.receptor;
      if(pp != null && pp.getMinEnergyReceived(receptor.fromDir.getOpposite()) <= canTransmit) {
        float used = pp.recieveEnergy(receptor.fromDir.getOpposite(), canTransmit);
        transmitted += used;
        canTransmit -= used;
      }
      if(canTransmit <= 0) {
        break;
      }

      if(!receptors.isEmpty() && !receptorIterator.hasNext()) {
        receptorIterator = receptors.listIterator();
      }
      appliedCount++;
    }

    powerHandler.setEnergy(powerHandler.getEnergyStored() - transmitted);

    return transmitted > 0;

  }

  private void checkReceptors() {
    if(!receptorsDirty) {
      return;
    }
    receptors.clear();

    BlockCoord bc = new BlockCoord(xCoord, yCoord, zCoord);
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      BlockCoord checkLoc = bc.getLocation(dir);
      TileEntity te = worldObj.getBlockTileEntity(checkLoc.x, checkLoc.y, checkLoc.z);
      IPowerInterface pi = PowerHandlerUtil.create(te);
      if(pi != null) {
        receptors.add(new Receptor(pi, dir));
      }
    }
    receptorIterator = receptors.listIterator();
    receptorsDirty = false;

  }

  static class Receptor {
    IPowerInterface receptor;
    ForgeDirection fromDir;

    private Receptor(IPowerInterface rec, ForgeDirection fromDir) {
      this.receptor = rec;
      this.fromDir = fromDir;
    }
  }

}
