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

import crazypants.enderio.base.item.darksteel.upgrade.storage.StorageCombinedCap;
import crazypants.enderio.base.material.upgrades.ItemUpgrades;
import crazypants.enderio.util.Prep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
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
    private final boolean isHead;

    AutoSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, boolean isHead) {
      super(itemHandler, index, xPosition, yPosition);
      this.isHead = isHead;
    }

    public boolean isBlocked() {
      return getHandler().isSlotBlocked(getHandlerSlot());
    }

    public @Nonnull List<ITextComponent> getSlotBlockedReason() {
      return getHandler().getSlotBlockedReason(getHandlerSlot());
    }

    public boolean isHead() {
      return isHead;
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
      return activeTab == getHandler().getEquipmentSlot();
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

  private final @Nonnull UpgradeCap main, head, body, legs, feet, offh;

  protected @Nonnull EntityEquipmentSlot activeTab = EntityEquipmentSlot.CHEST;

  public static DSUContainer create(@Nonnull InventoryPlayer playerInv, @Nonnull UpgradeCap feet, @Nonnull UpgradeCap legs, @Nonnull UpgradeCap body,
      @Nonnull UpgradeCap head, @Nonnull UpgradeCap main, @Nonnull UpgradeCap offh) {
    if (feet.getSlots() > 0 || legs.getSlots() > 0 || body.getSlots() > 0 || head.getSlots() > 0 || main.getSlots() > 0 || offh.getSlots() > 0) {
      return new DSUContainer(playerInv, feet, legs, body, head, main, offh);
    } else {
      return null;
    }
  }

  public DSUContainer(@Nonnull InventoryPlayer playerInv, @Nonnull UpgradeCap feet, @Nonnull UpgradeCap legs, @Nonnull UpgradeCap body,
      @Nonnull UpgradeCap head, @Nonnull UpgradeCap main, @Nonnull UpgradeCap offh) {
    super(playerInv, new StorageCombinedCap<>(main, feet, legs, body, head, offh), null);
    this.feet = feet;
    this.legs = legs;
    this.body = body;
    this.head = head;
    this.offh = offh;
    this.main = main;
  }

  @Override
  protected void addSlots() {
    int y = 0;
    int x = 0;
    EntityEquipmentSlot last = null;
    for (int i = 0; i < getItemHandler().getSlots(); i++) {
      boolean isHead = false;
      final UpgradeCap handler = getItemHandler().getHandlerFromSlot(i);
      EntityEquipmentSlot current = handler.getEquipmentSlot();
      int idx = getItemHandler().getIndexForHandler(i);
      if (current != last) {
        x = 0;
        y = 0;
        last = current;
      } else if (handler.isHead(idx)) {
        if (handler.isInventorySlot(idx)) {
          x = 0;
          y = 5 * 18; // row 6 is inventory
        } else {
          x += 6;
          isHead = true;
        }
      }
      if (x > (COLS - 1) * 18) {
        x = 0;
        y += 24;
        isHead = false;
      }
      addSlotToContainer(new AutoSlot(getItemHandler(), i, X0 + x, Y0 + y, isHead));
      x += 18;
    }
  }

  public void createGhostSlots(List<GhostSlot> slots) {
    for (Slot slot : getSlotLocations().keySet()) {
      if (slot instanceof AutoSlot) {
        slots.add(new UpgradeSlot(Prep.getEmpty(), (AutoSlot) slot));
      }
    }
    // for (int y = 5; y < 6; y++) {
    // for (int x = 0; x < 9; x++) {
    // slots.add(new GhostBackgroundItemSlot(ModObject.itemDarkSteelUpgrade.getItemNN(), X0 + 18 * x, Y0 + 18 * y));
    // }
    // }
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
  public IMessage setTab(@Nonnull EntityEquipmentSlot tab) {
    activeTab = tab;
    return null;
  }

  @Override
  public boolean canInteractWith(@Nonnull EntityPlayer player) {
    return feet.isStillConnectedToPlayer() && legs.isStillConnectedToPlayer() && body.isStillConnectedToPlayer() && head.isStillConnectedToPlayer()
        && main.isStillConnectedToPlayer() && offh.isStillConnectedToPlayer();
  }

}
