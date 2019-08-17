package crazypants.enderio.base.handler.darksteel.gui;

import java.awt.Point;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.GuiContainerBase;
import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.ContainerEnderCap;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.base.item.darksteel.upgrade.storage.StorageCombinedCap;
import crazypants.enderio.base.material.upgrades.ItemUpgrades;
import crazypants.enderio.util.Prep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class DSUContainer extends ContainerEnderCap<StorageCombinedCap<UpgradeCap>, TileEntity> implements DSURemoteExec.Container {

  static final class UpgradeSlot extends GhostBackgroundItemSlot {
    private final @Nonnull AutoSlot slot;

    UpgradeSlot(@Nonnull ItemStack stack, @Nonnull AutoSlot parent) {
      super(stack, parent);
      this.slot = parent;
    }

    @Override
    public @Nonnull ItemStack getStack() {
      return ItemUpgrades.setEnabled(slot.getUpgradeItem(), false);
    }

    @Override
    public boolean isVisible() {
      return slot.isEnabled();
    }

    public boolean isHead() {
      return slot.isHead();
    }

    public boolean isBlocked() {
      return slot.isBlocked();
    }

    @Override
    public boolean isMouseOver(int mx, int my) {
      return !slot.isInventorySlot() && mx >= getX() && mx < (getX() + 16) && my >= getY() && my < (getY() + 16);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean drawGhostSlotToolTip(@Nonnull GuiContainerBase gui, int mouseX, int mouseY) {
      if (gui.mc.player.inventory.getItemStack().isEmpty()) {
        final List<String> text = new NNList<>(getStack().getDisplayName());
        if (isBlocked()) {
          List<ITextComponent> reason = slot.getSlotBlockedReason();
          if (!reason.isEmpty()) {
            text.add("");
            text.addAll(reason.stream().peek(itc -> itc.getStyle().setColor(TextFormatting.DARK_RED)).map(ITextComponent::getFormattedText)
                .collect(Collectors.toList()));
          }
        }
        GuiUtils.drawHoveringText(Prep.getEmpty(), text, mouseX, mouseY, gui.width, gui.height, -1, gui.getFontRenderer());
        return true;
      }
      return false;
    }

  }

  final class AutoSlot extends SlotItemHandler {
    boolean noHead;

    AutoSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, boolean noHead) {
      super(itemHandler, index, xPosition, yPosition);
      this.noHead = noHead;
    }

    public boolean isBlocked() {
      return getHandler().isSlotBlocked(getHandlerSlot());
    }

    public @Nonnull List<ITextComponent> getSlotBlockedReason() {
      return getHandler().getSlotBlockedReason(getHandlerSlot());
    }

    public boolean isHead() {
      return getHandler().isHead(getHandlerSlot()) && !noHead;
    }

    @SuppressWarnings("unchecked")
    private int getHandlerSlot() {
      return ((StorageCombinedCap<UpgradeCap>) getItemHandler()).getIndexForHandler(getSlotIndex());
    }

    @SuppressWarnings("unchecked")
    private UpgradeCap getHandler() {
      return ((StorageCombinedCap<UpgradeCap>) getItemHandler()).getHandlerFromSlot(getSlotIndex());
    }

    @Override
    public boolean isEnabled() {
      return activeTab == getHandler().getSlotSelector() && getHandler().isVisible(getHandlerSlot());
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
      // stops shift-clicking items in. at least while activeTab is in sync between client and server
      return isEnabled() && super.isItemValid(stack);
    }

    @Nonnull
    ItemStack getUpgradeItem() {
      return getHandler().getUpgradeItem(getHandlerSlot());
    }

    boolean isInventorySlot() {
      return getHandler().isInventorySlot(getHandlerSlot());
    }
  }

  private static final int X0 = 8;
  private static final int Y0 = 10;
  private static final int COLS = 9;

  protected @Nonnull ISlotSelector activeTab = SlotSelector.CHEST;
  protected final @Nonnull SlotInventory slotInventory = new SlotInventory();

  public static DSUContainer create(@Nonnull EntityPlayer player, @Nonnull ISlotSelector... slotSelectors) {
    boolean hasAtleastOne = false;
    NNList<UpgradeCap> caps = new NNList<>();
    for (ISlotSelector iSlotSelector : slotSelectors) {
      if (iSlotSelector != null) {
        UpgradeCap upgradeCap = new UpgradeCap(iSlotSelector, player);
        if (upgradeCap.isAvailable()) {
          hasAtleastOne = true;
        }
        caps.add(upgradeCap);
      }
    }
    if (!hasAtleastOne) {
      return null;
    }
    caps.add(new UpgradeCap(new SlotSelector.SlotItem(null), player));
    return new DSUContainer(player, caps).init();
  }

  final @Nonnull NNList<UpgradeCap> caps;

  public DSUContainer(@Nonnull EntityPlayer player, @Nonnull NNList<UpgradeCap> caps) {
    super(player.inventory, new StorageCombinedCap<>(caps.toArray(new UpgradeCap[0])), null, true);
    this.caps = caps;
  }

  @Override
  protected void addSlots() {
    for (int i = 0; i < getItemHandler().getSlots(); i++) {
      addSlotToContainer(new AutoSlot(getItemHandler(), i, 0, 0, false));
    }
    int i = 0;
    for (UpgradeCap cap : caps) {
      if (cap.getSlotSelector().isItem()) {
        addSlotToContainer(cap.getSlotSelector().setContainerSlot(new Slot(slotInventory, i++, 0, 0) {
          @Override
          public boolean isItemValid(@Nonnull ItemStack stack) {
            return inventory.isItemValidForSlot(getSlotIndex(), stack);
          }
        }));
      }
    }
  }

  protected void calcSlots() {
    int y = 0;
    int x = 0;
    ISlotSelector last = null;
    for (int i = 0; i < getItemHandler().getSlots(); i++) {
      final UpgradeCap handler = getItemHandler().getHandlerFromSlot(i);
      int idx = getItemHandler().getIndexForHandler(i);
      if (handler.isVisible(idx)) {
        ISlotSelector current = handler.getSlotSelector();
        AutoSlot slot = (AutoSlot) inventorySlots.get(i); // FIXME: This is scary
        slot.noHead = false;
        if (current != last) {
          x = 0;
          y = 0;
          last = current;
          slot.noHead = true; // first slot on new tab
        } else if (handler.isHead(idx)) {
          if (handler.isInventorySlot(idx)) {
            x = 0;
            y = 5 * 18; // row 6 is inventory
            slot.noHead = true; // inventory slots never are head
          } else {
            x += 6;
          }
        }
        if (x > (COLS - 1) * 18) {
          x = 0;
          y += 24;
          slot.noHead = true; // first slot on new line
        }
        slot.xPos = X0 + x;
        slot.yPos = Y0 + y;
        x += 18;
      }
    }
  }

  public void createGhostSlots(List<GhostSlot> slots) {
    for (Slot slot : getSlotLocations().keySet()) {
      if (slot instanceof AutoSlot) {
        slots.add(new UpgradeSlot(Prep.getEmpty(), (AutoSlot) slot));
      }
    }
  }

  @Override
  public @Nonnull Point getPlayerInventoryOffset() {
    Point p = super.getPlayerInventoryOffset();
    p.translate(8, 70);
    return p;
  }

  private int guid = 0;

  @Override
  public void setGuiID(int id) {
    guid = id;
  }

  @Override
  public int getGuiID() {
    return guid;
  }

  @Override
  public @Nonnull ISlotSelector setTab(int tab) {
    for (UpgradeCap cap : caps) {
      if (cap.getSlotSelector().getTabOrder() == tab) {
        return activeTab = cap.getSlotSelector();
      }
    }
    return activeTab;
  }

  @Override
  public boolean canInteractWith(@Nonnull EntityPlayer player) {
    return caps.stream().anyMatch(UpgradeCap::isStillConnectedToPlayer);
  }

  @Override
  public void onContainerClosed(@Nonnull EntityPlayer playerIn) {
    super.onContainerClosed(playerIn);

    if (!playerIn.world.isRemote) {
      this.clearContainer(playerIn, playerIn.world, slotInventory);
    }
  }

  private class SlotInventory extends InventoryBasic {

    public SlotInventory() {
      super("", false, 7);
    }

    @Override
    public int getInventoryStackLimit() {
      return 1;
    }

    @Override
    public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
      return stack.getItem() instanceof IDarkSteelItem;
    }
  }
}
