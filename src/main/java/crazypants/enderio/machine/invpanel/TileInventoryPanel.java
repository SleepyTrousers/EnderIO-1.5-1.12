package crazypants.enderio.machine.invpanel;

import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.TileConduitBundle;
import crazypants.enderio.conduit.item.FilterRegister;
import crazypants.enderio.conduit.item.ItemConduit;
import crazypants.enderio.conduit.item.ItemConduitNetwork;
import crazypants.enderio.conduit.item.NetworkedInventory;
import crazypants.enderio.conduit.item.filter.IItemFilter;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.SlotDefinition;
import java.util.List;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileInventoryPanel extends AbstractMachineEntity {

  public static final int SLOT_CRAFTING_START  = 0;
  public static final int SLOT_CRAFTING_RESULT = 9;
  public static final int SLOT_VIEW_FILTER     = 10;
  public static final int SLOT_RETURN_START    = 11;

  private InventoryDatabaseServer dbServer;
  private InventoryDatabaseClient dbClient;

  private ItemConduitNetwork network;
  private int networkChangeCount;
  private int numSources;

  public Container eventHandler;
  private IItemFilter itemFilter;

  public TileInventoryPanel() {
    super(new SlotDefinition(0, 8, 11, 20, 21, 20));
  }

  public InventoryDatabaseServer getDatabaseServer() {
    if(dbServer == null) {
      dbServer = new InventoryDatabaseServer();
    }
    return dbServer;
  }

  public InventoryDatabaseClient getDatabaseClient() {
    if(dbClient == null) {
      dbClient = new InventoryDatabaseClient(this);
    }
    return dbClient;
  }

  @Override
  public boolean canInsertItem(int slot, ItemStack var2, int side) {
    return false;
  }

  @Override
  protected boolean isMachineItemValidForSlot(int slot, ItemStack stack) {
    if(slot == SLOT_VIEW_FILTER && stack != null) {
      return FilterRegister.isItemFilter(stack) && FilterRegister.isFilterSet(stack);
    }
    return true;
  }

  @Override
  public ItemStack decrStackSize(int fromSlot, int amount) {
    ItemStack res = super.decrStackSize(fromSlot, amount);
    if(res != null && fromSlot < SLOT_CRAFTING_RESULT && eventHandler != null) {
      eventHandler.onCraftMatrixChanged(this);
    }
    if(res != null && fromSlot == SLOT_VIEW_FILTER) {
      updateItemFilter();
    }
    return res;
  }

  @Override
  public void setInventorySlotContents(int slot, ItemStack contents) {
    super.setInventorySlotContents(slot, contents);
    if(slot < SLOT_CRAFTING_RESULT && eventHandler != null) {
      eventHandler.onCraftMatrixChanged(this);
    }
    if(slot == SLOT_VIEW_FILTER) {
      updateItemFilter();
    }
  }

  private void updateItemFilter() {
    itemFilter = FilterRegister.getFilterForUpgrade(inventory[SLOT_VIEW_FILTER]);
  }

  public IItemFilter getItemFilter() {
    return itemFilter;
  }

  @Override
  public boolean isActive() {
    return numSources > 0;
  }

  @Override
  public void doUpdate() {
    if(worldObj.isRemote) {
      updateEntityClient();
      return;
    }

    if(shouldDoWorkThisTick(2)) {
      scanNetwork();
    }

    if(dbServer != null && shouldDoWorkThisTick(20)) {
      dbServer.sendChangeLogs();
    }

    if(forceClientUpdate) {
      worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
      markDirty();
    }
  }

  private void scanNetwork() {
    ForgeDirection facingDir = getFacingDir();
    ForgeDirection backside = facingDir.getOpposite();

    ItemConduit conduit = null;
    ItemConduitNetwork icn = null;

    TileEntity te = worldObj.getTileEntity(xCoord+backside.offsetX, yCoord+backside.offsetY, zCoord+backside.offsetZ);
    if(te instanceof TileConduitBundle) {
      TileConduitBundle teCB = (TileConduitBundle) te;
      conduit = teCB.getConduit(ItemConduit.class);
      if(conduit != null) {
        icn = (ItemConduitNetwork) conduit.getNetwork();
      }
    }

    if(icn != network || (icn != null && icn.getChangeCount() != networkChangeCount)) {
      updateNetwork(icn, conduit, facingDir);
    }

    if(dbServer != null) {
      dbServer.scanNextInventory();
    }
  }

  private void updateNetwork(ItemConduitNetwork icn, ItemConduit conduit, ForgeDirection facingDir) {
    List<NetworkedInventory> sources = null;

    if(icn != null) {
      networkChangeCount = icn.getChangeCount();
      sources = icn.getInventoryPanelSources();

      if(numSources != sources.size()) {
        numSources = sources.size();
        forceClientUpdate = true;
      }
    }

    this.network = icn;

    if(dbServer != null || sources != null) {
      getDatabaseServer().setNetworkSources(sources);
    }
  }

  @Override
  protected boolean processTasks(boolean redstoneCheckPassed) {
    return false;
  }

  @Override
  public void readCustomNBT(NBTTagCompound nbtRoot) {
    super.readCustomNBT(nbtRoot);
    numSources = nbtRoot.getInteger("numSources");
    updateItemFilter();
  }

  @Override
  public void writeCustomNBT(NBTTagCompound nbtRoot) {
    super.writeCustomNBT(nbtRoot);
    nbtRoot.setInteger("numSources", numSources);
  }

  @Override
  public String getMachineName() {
    return ModObject.blockInventoryPanel.unlocalisedName;
  }
}
