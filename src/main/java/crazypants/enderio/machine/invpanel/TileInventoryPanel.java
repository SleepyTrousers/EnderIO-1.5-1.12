package crazypants.enderio.machine.invpanel;

import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.TileConduitBundle;
import crazypants.enderio.conduit.item.ItemConduit;
import crazypants.enderio.conduit.item.ItemConduitNetwork;
import crazypants.enderio.conduit.item.NetworkedInventory;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.util.DyeColor;
import java.util.List;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileInventoryPanel extends AbstractMachineEntity {

  private final InventoryDatabase database;

  private ItemConduitNetwork network;
  private int networkChangeCount;

  public Container eventHandler;

  public TileInventoryPanel() {
    super(new SlotDefinition(0, 8, 10, 19, 20, 19));
    this.database = new InventoryDatabase();
  }

  public InventoryDatabase getDatabase() {
    return database;
  }

  @Override
  public boolean canInsertItem(int slot, ItemStack var2, int side) {
    return false;
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    return true;
  }

  @Override
  public ItemStack decrStackSize(int fromSlot, int amount) {
    ItemStack res = super.decrStackSize(fromSlot, amount);
    if(res != null && fromSlot < 9 && eventHandler != null) {
      eventHandler.onCraftMatrixChanged(this);
    }
    return res;
  }

  @Override
  public void setInventorySlotContents(int slot, ItemStack contents) {
    super.setInventorySlotContents(slot, contents);
    if(slot < 9 && eventHandler != null) {
      eventHandler.onCraftMatrixChanged(this);
    }
  }

  @Override
  public boolean isActive() {
    return true;
  }

  @Override
  public void doUpdate() {
    if(worldObj.isRemote) return;

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

    if(icn == null) {
      network = null;
      database.setNetworkSources(null);
    } else if(icn != network || icn.getChangeCount() != networkChangeCount) {
      updateNetwork(icn, conduit, facingDir);
    }

    database.scanNextInventory();
  }

  private void updateNetwork(ItemConduitNetwork icn, ItemConduit conduit, ForgeDirection facingDir) {
    this.network = icn;
    this.networkChangeCount = icn.getChangeCount();

    ConnectionMode mode = conduit.getConnectionMode(facingDir);
    System.out.println("updateNetwork: mode="+mode);
    if(mode == ConnectionMode.OUTPUT || mode == ConnectionMode.IN_OUT) {
      DyeColor color = conduit.getOutputColor(facingDir);
      List<NetworkedInventory> sources = icn.getSourcesForColor(color);
      System.out.println("Color="+color+" sources="+sources.size());
      database.setNetworkSources(sources);
    } else {
      database.setNetworkSources(null);
    }
  }

  @Override
  protected boolean processTasks(boolean redstoneCheckPassed) {
    return false;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockInventoryPanel.unlocalisedName;
  }
}
