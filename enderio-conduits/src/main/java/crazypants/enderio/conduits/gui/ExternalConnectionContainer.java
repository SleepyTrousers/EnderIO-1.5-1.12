package crazypants.enderio.conduits.gui;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.ContainerEnderCap;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IExternalConnectionContainer;
import crazypants.enderio.base.conduit.IFilterChangeListener;
import crazypants.enderio.base.conduit.item.FunctionUpgrade;
import crazypants.enderio.base.conduit.item.ItemFunctionUpgrade;
import crazypants.enderio.base.filter.IFilter;
import crazypants.enderio.base.filter.IFilterContainer;
import crazypants.enderio.base.filter.capability.CapabilityFilterHolder;
import crazypants.enderio.base.filter.capability.IFilterHolder;
import crazypants.enderio.base.filter.network.IOpenFilterRemoteExec;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.conduits.capability.CapabilityUpgradeHolder;
import crazypants.enderio.conduits.conduit.TileConduitBundle;
import crazypants.enderio.conduits.init.ConduitObject;
import crazypants.enderio.conduits.network.PacketSlotVisibility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ExternalConnectionContainer extends ContainerEnderCap<InventoryUpgrades, TileConduitBundle>
    implements IExternalConnectionContainer, IOpenFilterRemoteExec.Container, IFilterContainer {

  private int speedUpgradeSlotLimit = 15;

  private static final int outputFilterSlot = 36;
  private static final int inputFilterSlot = 37;
  private static final int functionUpgradeSlot = 38;

  private Slot slotFunctionUpgrade;
  private Slot slotInputFilter;
  private Slot slotOutputFilter;

  private @Nonnull EnumFacing dir;
  private @Nonnull EntityPlayer player;

  private IConduit currentCon;

  final List<IFilterChangeListener> filterListeners = new ArrayList<IFilterChangeListener>();

  public ExternalConnectionContainer(@Nonnull InventoryPlayer playerInv, @Nonnull EnumFacing dir, @Nonnull TileConduitBundle bundle) {
    super(playerInv, new InventoryUpgrades(dir), bundle);
    this.dir = dir;
    this.player = playerInv.player;
  }

  @Override
  protected void addSlots() {
    addSlotToContainer(slotInputFilter = new FilterSlot(getItemHandler(), 3, 23, 71));
    addSlotToContainer(slotOutputFilter = new FilterSlot(getItemHandler(), 2, 113, 71));
    addSlotToContainer(slotFunctionUpgrade = new SlotItemHandler(getItemHandler(), 0, 131, 71) {
      @Override
      public boolean isItemValid(@Nonnull ItemStack itemStack) {
        return ExternalConnectionContainer.this.getItemHandler().isItemValidForSlot(0, itemStack, currentCon);
      }

      @Override
      public int getSlotStackLimit() {
        return speedUpgradeSlotLimit;
      }
    });
  }

  public void createGhostSlots(@Nonnull List<GhostSlot> ghostSlots) {
    ghostSlots.add(new GhostBackgroundItemSlot(ModObject.itemBasicItemFilter.getItemNN(), slotOutputFilter));
    ghostSlots.add(new GhostBackgroundItemSlot(ModObject.itemBasicItemFilter.getItemNN(), slotInputFilter));

    NNList<ItemStack> ghostSlotIcons = new NNList<>(new ItemStack(ConduitObject.item_extract_speed_upgrade.getItemNN()),
        new ItemStack(ConduitObject.item_extract_speed_downgrade.getItemNN()));
    ghostSlots.add(new GhostBackgroundItemSlot(ghostSlotIcons, slotFunctionUpgrade));
  }

  @Override
  @Nonnull
  public Point getPlayerInventoryOffset() {
    return new Point(23, 113);
  }

  @Override
  public void addFilterListener(@Nonnull IFilterChangeListener list) {
    filterListeners.add(list);
  }

  protected void filterChanged() {
    for (IFilterChangeListener list : filterListeners) {
      list.onFilterChanged();
    }
  }

  @Override
  public boolean hasFunctionUpgrade() {
    return slotFunctionUpgrade != null && slotFunctionUpgrade.getHasStack();
  }

  @Override
  public boolean hasFilter(boolean input) {
    Slot slot = input ? slotInputFilter : slotOutputFilter;
    return slot != null && slot.getHasStack();
  }

  @Override
  public void setInOutSlotsVisible(boolean filterVisible, boolean upgradeVisible, IConduit conduit) {
    if (conduit == null) {
      return;
    }

    World world = getTileEntity().getBundleworld();

    boolean hasFilterHolder = false;
    boolean hasUpgradeHolder = false;
    currentCon = conduit;

    if (filterVisible || upgradeVisible) {

      hasFilterHolder = conduit.hasCapability(CapabilityFilterHolder.FILTER_HOLDER_CAPABILITY, dir);
      hasUpgradeHolder = conduit.hasCapability(CapabilityUpgradeHolder.UPGRADE_HOLDER_CAPABILITY, dir);

      if (hasFilterHolder) {
        getItemHandler().setFilterHolder(conduit.getCapability(CapabilityFilterHolder.FILTER_HOLDER_CAPABILITY, dir));
      }
      if (hasUpgradeHolder) {
        getItemHandler().setUpgradeHolder(conduit.getCapability(CapabilityUpgradeHolder.UPGRADE_HOLDER_CAPABILITY, dir));
      }
    }

    setSlotsVisible(filterVisible && hasFilterHolder, upgradeVisible && hasUpgradeHolder);

    if (world.isRemote) {
      PacketHandler.INSTANCE.sendToServer(new PacketSlotVisibility(conduit, filterVisible, upgradeVisible));
    }
  }

  private void setSlotsVisible(boolean filterVisible, boolean upgradeVisible) {
    Slot inFilter = getSlot(1);
    Slot outFilter = getSlot(0);
    Slot funcUpgrade = getSlot(2);

    if (filterVisible) {
      slotInputFilter.xPos = slotLocations.get(slotInputFilter).x;
      slotInputFilter.yPos = slotLocations.get(slotInputFilter).y;
      slotOutputFilter.xPos = slotLocations.get(slotOutputFilter).x;
      slotOutputFilter.yPos = slotLocations.get(slotOutputFilter).y;
      inFilter.xPos = slotLocations.get(inFilter).x;
      inFilter.yPos = slotLocations.get(inFilter).y;
      outFilter.xPos = slotLocations.get(outFilter).x;
      outFilter.yPos = slotLocations.get(outFilter).y;
    } else {
      slotInputFilter.xPos = -3000;
      slotInputFilter.yPos = -3000;
      slotOutputFilter.xPos = -3000;
      slotOutputFilter.yPos = -3000;
      inFilter.xPos = -3000;
      inFilter.yPos = -3000;
      outFilter.xPos = -3000;
      outFilter.yPos = -3000;
    }

    if (upgradeVisible) {
      slotFunctionUpgrade.xPos = slotLocations.get(slotFunctionUpgrade).x;
      slotFunctionUpgrade.yPos = slotLocations.get(slotFunctionUpgrade).y;
      funcUpgrade.xPos = slotLocations.get(funcUpgrade).x;
      funcUpgrade.yPos = slotLocations.get(funcUpgrade).y;
    } else {
      slotFunctionUpgrade.xPos = -3000;
      slotFunctionUpgrade.yPos = -3000;
      funcUpgrade.xPos = -3000;
      funcUpgrade.yPos = -3000;
    }
  }

  @Override
  @Nonnull
  public ItemStack slotClick(int slotId, int dragType, @Nonnull ClickType clickTypeIn, @Nonnull EntityPlayer playerIn) {
    ItemStack st = playerIn.inventory.getItemStack();
    setFunctionUpgradeSlotLimit(st);
    try {
      return super.slotClick(slotId, dragType, clickTypeIn, playerIn);
    } catch (Exception e) {
      // TODO Horrible work around for a bug when double clicking on a stack in inventory which matches a filter item
      // This does does double clicking to fill a stack from working with this GUI open.
      return ItemStack.EMPTY;
    }
  }

  private void setFunctionUpgradeSlotLimit(@Nonnull ItemStack st) {
    if (!st.isEmpty() && st.getItem() instanceof ItemFunctionUpgrade) {
      FunctionUpgrade speedUpgrade = ItemFunctionUpgrade.getFunctionUpgrade(st);
      speedUpgradeSlotLimit = speedUpgrade.maxStackSize;
    }
  }

  private int guiId = -1;

  @Override
  public void setGuiID(int id) {
    guiId = id;
  }

  @Override
  public int getGuiID() {
    return guiId;
  }

  @Override
  public IMessage doOpenFilterGui(int filterIndex) {
    if (currentCon.hasCapability(CapabilityFilterHolder.FILTER_HOLDER_CAPABILITY, dir)) {
      IFilterHolder<?> filterHolder = currentCon.getCapability(CapabilityFilterHolder.FILTER_HOLDER_CAPABILITY, dir);
      int param1 = dir.ordinal();
      if (filterHolder != null) {
        filterHolder.getFilter(filterIndex, param1).openGui(player, filterHolder.getFilterStack(filterIndex, param1), getTileEntity().getBundleworld(),
            getTileEntity().getPos(), dir, filterIndex);
      }
    }
    return null;
  }

  @Override
  public IFilter getFilter(int filterIndex) {
    if (currentCon.hasCapability(CapabilityFilterHolder.FILTER_HOLDER_CAPABILITY, dir)) {
      IFilterHolder<?> filterHolder = currentCon.getCapability(CapabilityFilterHolder.FILTER_HOLDER_CAPABILITY, dir);
      int param1 = dir.ordinal();
      if (filterHolder != null) {
        return filterHolder.getFilter(filterIndex, param1);
      }
    }
    return null;
  }

  private class FilterSlot extends SlotItemHandler {
    public FilterSlot(IItemHandler handler, int index, int x, int y) {
      super(handler, index, x, y);
    }

    @Override
    public int getSlotStackLimit() {
      return 1;
    }

    @Override
    public void onSlotChanged() {
      filterChanged();
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
      return ExternalConnectionContainer.this.getItemHandler().isItemValidForSlot(getSlotIndex(), stack, currentCon);
    }

  }

}
