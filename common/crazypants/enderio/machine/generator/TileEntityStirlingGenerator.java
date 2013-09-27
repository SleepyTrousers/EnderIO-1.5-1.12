package crazypants.enderio.machine.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.power.IPowerEmitter;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.power.Capacitors;
import crazypants.enderio.power.IInternalPowerReceptor;
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
    return burnTime > 0;
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
    receptorsDirty = true;
  }

  @Override
  protected boolean processTasks(boolean redstoneCheckPassed) {
    boolean needsUpdate = false;

    if(burnTime > 0) {
      // powerProvider.getPowerReceiver().receiveEnergy(Type.ENGINE,
      // ENERGY_PER_TICK, ForgeDirection.DOWN);
      powerHandler.setEnergy(powerHandler.getEnergyStored() + ENERGY_PER_TICK);
      burnTime--;
      needsUpdate = true;
    }

    needsUpdate |= transmitEnergy();

    if(burnTime <= 0 && powerHandler.getEnergyStored() < powerHandler.getMaxEnergyStored() && redstoneCheckPassed) {
      if(inventory[0] != null && inventory[0].stackSize > 0) {
        burnTime = TileEntityFurnace.getItemBurnTime(inventory[0]);
        if(burnTime > 0) {
          totalBurnTime = burnTime;
          if(inventory[0].itemID == Item.bucketLava.itemID) {
            inventory[0] = new ItemStack(Item.bucketEmpty);
          } else {
            decrStackSize(0, 1);
          }
        }
        needsUpdate = true;
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
      PowerReceiver pp = receptor.receptor.getPowerReceiver(receptor.fromDir.getOpposite());
      if(pp != null && pp.getMinEnergyReceived() <= canTransmit && pp.getType() != Type.ENGINE) {
        float used;
        if(receptor.receptor instanceof IInternalPowerReceptor) {
          used = PowerHandlerUtil.transmitInternal((IInternalPowerReceptor) receptor.receptor, pp, canTransmit, Type.ENGINE, receptor.fromDir.getOpposite());
        } else {
          used = pp.receiveEnergy(Type.ENGINE, canTransmit, receptor.fromDir.getOpposite());
        }
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
      if(te instanceof IPowerReceptor) {
        IPowerReceptor rec = (IPowerReceptor) te;
        receptors.add(new Receptor((IPowerReceptor) te, dir));
      }
    }
    receptorIterator = receptors.listIterator();
    receptorsDirty = false;

  }

  static class Receptor {
    IPowerReceptor receptor;
    ForgeDirection fromDir;

    private Receptor(IPowerReceptor rec, ForgeDirection fromDir) {
      super();
      this.receptor = rec;
      this.fromDir = fromDir;
    }
  }

}
