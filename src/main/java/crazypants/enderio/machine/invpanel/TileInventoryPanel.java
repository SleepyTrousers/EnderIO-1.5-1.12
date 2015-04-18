package crazypants.enderio.machine.invpanel;

import crazypants.enderio.machine.invpanel.client.InventoryDatabaseClient;
import crazypants.enderio.machine.invpanel.server.InventoryDatabaseServer;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.TileConduitBundle;
import crazypants.enderio.conduit.item.FilterRegister;
import crazypants.enderio.conduit.item.ItemConduit;
import crazypants.enderio.conduit.item.ItemConduitNetwork;
import crazypants.enderio.conduit.item.filter.IItemFilter;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.invpanel.client.ClientDatabaseManager;
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

  private int numSources;

  public Container eventHandler;
  private IItemFilter itemFilter;

  public TileInventoryPanel() {
    super(new SlotDefinition(0, 8, 11, 20, 21, 20));
  }

  public InventoryDatabaseServer getDatabaseServer() {
    return dbServer;
  }

  public InventoryDatabaseClient getDatabaseClient(int generation) {
    if(dbClient != null && dbClient.getGeneration() != generation) {
      ClientDatabaseManager.INSTANCE.destroyDatabase(dbClient.getGeneration());
      dbClient = null;
    }
    if(dbClient == null) {
      dbClient = ClientDatabaseManager.INSTANCE.getOrCreateDatabase(generation);
    }
    return dbClient;
  }

  public InventoryDatabaseClient getDatabaseClient() {
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

    if(shouldDoWorkThisTick(20)) {
      scanNetwork();
    }

    if(forceClientUpdate) {
      worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
      markDirty();
    }
  }

  private void scanNetwork() {
    ForgeDirection facingDir = getFacingDir();
    ForgeDirection backside = facingDir.getOpposite();

    ItemConduitNetwork icn = null;

    TileEntity te = worldObj.getTileEntity(xCoord+backside.offsetX, yCoord+backside.offsetY, zCoord+backside.offsetZ);
    if(te instanceof TileConduitBundle) {
      TileConduitBundle teCB = (TileConduitBundle) te;
      ItemConduit conduit = teCB.getConduit(ItemConduit.class);
      if(conduit != null) {
        icn = (ItemConduitNetwork) conduit.getNetwork();
      }
    }

    if(icn != null) {
      dbServer = icn.getDatabase();
      dbServer.sendChangeLogs();

      if(numSources != dbServer.getNumInventories()) {
        numSources = dbServer.getNumInventories();
        forceClientUpdate = true;
      }
    } else {
      dbServer = null;
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
