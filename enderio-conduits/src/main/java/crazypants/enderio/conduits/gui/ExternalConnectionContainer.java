package crazypants.enderio.conduits.gui;

import java.awt.Point;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.ContainerEnderCap;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IExternalConnectionContainer;
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
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ExternalConnectionContainer extends ContainerEnderCap<InventoryUpgrades, TileConduitBundle>
    implements IExternalConnectionContainer, IOpenFilterRemoteExec.Container, IFilterContainer {

  private final @Nonnull Slot slotFunctionUpgrade;
  private final @Nonnull Slot slotInputFilter;
  private final @Nonnull Slot slotOutputFilter;

  private final @Nonnull EnumFacing dir;
  private final @Nonnull EntityPlayer player;

  private IConduit currentCon;

  public ExternalConnectionContainer(@Nonnull InventoryPlayer playerInv, @Nonnull EnumFacing dir, @Nonnull TileConduitBundle bundle) {
    super(playerInv, new InventoryUpgrades(dir), bundle);
    this.dir = dir;
    this.player = playerInv.player;
    this.slotInputFilter = new FilterSlot(getItemHandler(), 3, 23, 71);
    this.slotOutputFilter = new FilterSlot(getItemHandler(), 2, 113, 71);
    this.slotFunctionUpgrade = new SlotItemHandler(getItemHandler(), 0, 131, 71) {
      @Override
      public boolean isItemValid(@Nonnull ItemStack itemStack) {
        return ExternalConnectionContainer.this.getItemHandler().isItemValidForSlot(0, itemStack, currentCon);
      }

      @Override
      public int getSlotStackLimit() {
        return ExternalConnectionContainer.this.getItemHandler().getSlotLimit(0);
      }

      @Override
      public int getItemStackLimit(@Nonnull ItemStack stack) {
        return ExternalConnectionContainer.this.getItemHandler().getSlotLimit(0, stack);
      }
    };
  }

  @Override
  protected void addSlots() {
    addSlotToContainer(slotInputFilter);
    addSlotToContainer(slotOutputFilter);
    addSlotToContainer(slotFunctionUpgrade);
  }

  @Override
  public void createGhostSlots(@Nonnull NNList<GhostSlot> ghostSlots) {
    NNList<ItemStack> filtersAll = new NNList<>(new ItemStack(ModObject.itemBasicItemFilter.getItemNN()));
    NNList<ItemStack> upgrades = new NNList<>(new ItemStack(ConduitObject.item_extract_speed_upgrade.getItemNN()),
        new ItemStack(ConduitObject.item_extract_speed_downgrade.getItemNN()));
    createGhostSlots(ghostSlots, filtersAll, filtersAll, upgrades);
  }

  @Override
  public void createGhostSlots(@Nonnull NNList<GhostSlot> ghostSlots, @Nonnull NNList<ItemStack> filters, @Nonnull NNList<ItemStack> upgrades) {
    createGhostSlots(ghostSlots, filters, filters, upgrades);
  }

  @Override
  public void createGhostSlots(@Nonnull NNList<GhostSlot> ghostSlots, @Nonnull NNList<ItemStack> filtersIn, @Nonnull NNList<ItemStack> filtersOut,
      @Nonnull NNList<ItemStack> upgrades) {
    ghostSlots.add(new GhostBackgroundItemSlot(filtersIn, slotInputFilter));
    ghostSlots.add(new GhostBackgroundItemSlot(filtersOut, slotOutputFilter));
    ghostSlots.add(new GhostBackgroundItemSlot(upgrades, slotFunctionUpgrade));
  }

  @Override
  @Nonnull
  public Point getPlayerInventoryOffset() {
    return new Point(23, 113);
  }

  @Override
  public boolean hasFunctionUpgrade() {
    return slotFunctionUpgrade.getHasStack();
  }

  @Override
  public boolean hasFilter(boolean input) {
    return (input ? slotInputFilter : slotOutputFilter).getHasStack();
  }

  @Override
  public void setInOutSlotsVisible(boolean filterVisible, boolean upgradeVisible, @Nonnull IConduit conduit) {
    World world = getTileEntityNN().getBundleworld();

    boolean hasFilterHolder = false;
    boolean hasUpgradeHolder = false;
    currentCon = conduit;

    if (filterVisible || upgradeVisible) {

      hasFilterHolder = conduit.hasInternalCapability(CapabilityFilterHolder.FILTER_HOLDER_CAPABILITY, dir);
      hasUpgradeHolder = conduit.hasInternalCapability(CapabilityUpgradeHolder.UPGRADE_HOLDER_CAPABILITY, dir);

      if (hasFilterHolder) {
        getItemHandler().setFilterHolder(conduit.getInternalCapability(CapabilityFilterHolder.FILTER_HOLDER_CAPABILITY, dir));
      }
      if (hasUpgradeHolder) {
        getItemHandler().setUpgradeHolder(conduit.getInternalCapability(CapabilityUpgradeHolder.UPGRADE_HOLDER_CAPABILITY, dir));
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
    if (currentCon.hasInternalCapability(CapabilityFilterHolder.FILTER_HOLDER_CAPABILITY, dir)) {
      IFilterHolder<?> filterHolder = currentCon.getInternalCapability(CapabilityFilterHolder.FILTER_HOLDER_CAPABILITY, dir);
      int param1 = dir.ordinal();
      if (filterHolder != null) {
        filterHolder.getFilter(filterIndex, param1).openGui(player, filterHolder.getFilterStack(filterIndex, param1), getTileEntityNN().getBundleworld(),
            getTileEntityNN().getPos(), dir, filterIndex);
      }
    }
    return null;
  }

  @Override // FIXME nonnull? it's certainly used that way, so why can it return null in so many cases
  public @Nonnull IFilter getFilter(int filterIndex) {
    if (currentCon.hasInternalCapability(CapabilityFilterHolder.FILTER_HOLDER_CAPABILITY, dir)) {
      IFilterHolder<?> filterHolder = currentCon.getInternalCapability(CapabilityFilterHolder.FILTER_HOLDER_CAPABILITY, dir);
      int param1 = dir.ordinal();
      if (filterHolder != null) {
        return filterHolder.getFilter(filterIndex, param1); // Nullable
      }
    }
    return null; // Null
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
    public int getItemStackLimit(@Nonnull ItemStack stack) {
      return Math.min(super.getItemStackLimit(stack), getSlotStackLimit());
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
      return ExternalConnectionContainer.this.getItemHandler().isItemValidForSlot(getSlotIndex(), stack, currentCon);
    }

  }

  @Override
  @Nonnull
  public List<String> getFunctionUpgradeToolTipText() {
    return getItemHandler().getFunctionUpgradeToolTipText();
  }

}
