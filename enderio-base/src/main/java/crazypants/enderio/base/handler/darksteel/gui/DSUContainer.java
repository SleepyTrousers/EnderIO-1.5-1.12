package crazypants.enderio.base.handler.darksteel.gui;

import java.awt.Point;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.gui.GuiContainerBase;
import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.ContainerEnderCap;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.base.block.darksteel.anvil.BlockDarkSteelAnvil;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.item.darksteel.upgrade.anvil.AnvilUpgrade;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.material.upgrades.ItemUpgrades;
import crazypants.enderio.util.EIOCombinedInvWrapper;
import crazypants.enderio.util.Prep;
import crazypants.enderio.util.WorldTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;

public class DSUContainer extends ContainerEnderCap<EIOCombinedInvWrapper<UpgradeCap>, TileEntity> implements DSURemoteExec.Container {

  static final class UpgradeSlot extends GhostBackgroundItemSlot {
    private final AutoSlot slot;

    UpgradeSlot(ItemStack stack, AutoSlot parent) {
      super(stack, parent);
      this.slot = parent;
    }

    @Override
    public @Nonnull ItemStack getStack() {
      return ItemUpgrades.setEnabled(slot.getUpgradeItem(), false);
    }

    @Override
    public boolean isVisible() {
      return slot.isEnabled() && super.isVisible();
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
        if (slot.isBlocked()) {
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

  final class AutoSlot extends BaseSlotItemHandler {
    boolean noHead;

    AutoSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, boolean noHead) {
      super(itemHandler, index, xPosition, yPosition);
      this.noHead = noHead;
    }

    public boolean isBlocked() {
      return getHandler().isSlotBlocked(getHandlerSlot());
    }

    public List<ITextComponent> getSlotBlockedReason() {
      return getHandler().getSlotBlockedReason(getHandlerSlot());
    }

    public boolean isHead() {
      return getHandler().isHead(getHandlerSlot()) && !noHead;
    }

    public boolean isAddOnly() {
      return getHandler().isAddOnly();
    }

    @SuppressWarnings("unchecked")
    private int getHandlerSlot() {
      return ((EIOCombinedInvWrapper<UpgradeCap>) getItemHandler()).getIndexForHandler(getSlotIndex());
    }

    @SuppressWarnings("unchecked")
    private UpgradeCap getHandler() {
      return ((EIOCombinedInvWrapper<UpgradeCap>) getItemHandler()).getHandlerFromSlot(getSlotIndex());
    }

    @Override
    public boolean isEnabled() {
      return activeTab == getHandler().getSlotSelector() && getHandler().isVisible(getHandlerSlot());
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
      // stops shift-clicking items in. at least while activeTab is in sync between client and server
      return isEnabled() && getHandler().isItemValid(getHandlerSlot(), stack);
    }

    @Override
    public int getItemStackLimit(net.minecraft.item.ItemStack stack) {
      return getHandler().getSlotLimit(getHandlerSlot());
    }

    ItemStack getUpgradeItem() {
      return getHandler().getUpgradeItem(getHandlerSlot());
    }

    boolean isInventorySlot() {
      return getHandler().isInventorySlot(getHandlerSlot());
    }

    public int getX() {
      return xPos;
    }

    public int getY() {
      return yPos;
    }

  }

  private static final int X0 = 8;
  private static final int Y0 = 10;
  private static final int COLS = 9;

  protected ISlotSelector activeTab = SlotSelector.CHEST;
  protected final SlotInventory slotInventory = new SlotInventory();
  protected final AnvilSubContainer anvil;
  protected final NNList<UpgradeCap> caps;
  protected final WorldTarget target;
  protected final @Nullable BlockDarkSteelAnvil block;

  public static @Nullable DSUContainer create(EntityPlayer player, World world, BlockPos pos, @Nullable EnumFacing facing, int param1,
      @Nullable BlockDarkSteelAnvil block) {
    NNList<UpgradeCap> caps = new NNList<>();
    WorldTarget target = WorldTarget.TRUE;
    if (pos.getY() >= 0 && world.isBlockLoaded(pos) && world.getBlockState(pos).getBlock() == block) {
      // this case is the GUI for the Dark Steel Anvil
      target = WorldTarget.ofBlock(world, pos, block);
      caps.add(new UpgradeCap(SlotSelector.ANVIL, player, false));
      NNList.of(SlotSelector.class).apply(ss -> {
        caps.add(new UpgradeCap(ss, player, false));
      });
      caps.add(new UpgradeCap(new SlotSelector.SlotItem(), player, false));
    } else {
      // Check what items the player has equipped
      AnvilUpgrade highestEquipped = AnvilUpgrade.getHighestEquippedUpgrade(player);
      if (highestEquipped != null && highestEquipped.allowsAnvilRecipes()) {
        caps.add(new UpgradeCap(SlotSelector.ANVIL, player, false));
      }
      if (highestEquipped != null && highestEquipped.allowsEditingOtherEquippedItems()) {
        NNList.of(SlotSelector.class).apply(ss -> {
          caps.add(new UpgradeCap(ss, player, false));
        });
      } else if (player.getHeldItemMainhand().getItem() == ModObject.itemDarkSteelUpgrade.getItemNN()) {
        // DSU item gives limited access to add it all equipped items.
        // Items that have the lowest tier Anvil Upgrade still allow full editing.
        // And filter out the tab with the DSU item
        NNList.of(SlotSelector.class).forEach(ss -> {
          if (ss.getSlot() != EntityEquipmentSlot.MAINHAND) {
            caps.add(new UpgradeCap(ss, player, AnvilUpgrade.loadAnyFromItem(ss.getItem(player)) == null));
          }
        });
      } else if (player.getHeldItemOffhand().getItem() == ModObject.itemDarkSteelUpgrade.getItemNN()) {
        NNList.of(SlotSelector.class).forEach(ss -> {
          if (ss.getSlot() != EntityEquipmentSlot.OFFHAND) {
            caps.add(new UpgradeCap(ss, player, AnvilUpgrade.loadAnyFromItem(ss.getItem(player)) == null));
          }
        });
      } else {
        // No high level upgrades, no anvil, no DSU item---show only the items that have the low level upgrade.
        NNList.of(SlotSelector.class).apply(ss -> {
          if (AnvilUpgrade.loadAnyFromItem(ss.getItem(player)) != null) {
            caps.add(new UpgradeCap(ss, player, false));
          }
        });
      }
      if (highestEquipped != null && highestEquipped.allowsEditingSlotItems()) {
        caps.add(new UpgradeCap(new SlotSelector.SlotItem(), player, false));
      }
    }
    for (UpgradeCap cap : caps) {
      if (!cap.getSlotSelector().isSlot() || cap.isAvailable()) {
        // There's at least one tab to be shown, so open the GUI
        return new DSUContainer(player, caps, target, block).init();
      }
    }
    if (!player.world.isRemote) {
      player.sendStatusMessage(Lang.DSU_GUI_NO_ITEMS.toChatServer(), true);
    }
    return null;
  }

  public DSUContainer(EntityPlayer player, NNList<UpgradeCap> caps, WorldTarget target, @Nullable BlockDarkSteelAnvil block) {
    super(player.inventory, new EIOCombinedInvWrapper<>(caps.toArray(new UpgradeCap[0])), null, true);
    this.caps = caps;
    this.anvil = new AnvilSubContainer(this, player);
    this.target = target;
    this.block = block;
  }

  private final NNList<AutoSlot> autoSlots = new NNList<>();

  @Override
  protected void addSlots() {
    anvil.addSlots(); // Note: Anvil GUI hardcodes these as the first 3 slots
    autoSlots.clear();
    for (int i = 0; i < getItemHandler().getSlots(); i++) {
      autoSlots.add((AutoSlot) addSlotToContainer(new AutoSlot(getItemHandler(), i, 0, 0, false)));
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
        AutoSlot slot = autoSlots.get(i);
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
    return target.isValid(player, 64) && caps.stream().anyMatch(UpgradeCap::isStillConnectedToPlayer);
  }

  @Override
  public void onContainerClosed(@Nonnull EntityPlayer playerIn) {
    super.onContainerClosed(playerIn);
    anvil.onContainerClosed(playerIn);

    if (!playerIn.world.isRemote) {
      clearContainer(playerIn, playerIn.world, slotInventory);
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

  @Override // opened for sub container
  protected @Nonnull Slot addSlotToContainer(@Nonnull Slot slotIn) {
    return super.addSlotToContainer(slotIn);
  }

  @Override // opened for sub container
  protected void clearContainer(@Nonnull EntityPlayer playerIn, @Nonnull World worldIn, @Nonnull IInventory inventoryIn) {
    super.clearContainer(playerIn, worldIn, inventoryIn);
  }

  @Override
  public void addListener(@Nonnull IContainerListener listener) {
    super.addListener(listener);
    anvil.addListener(listener);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void updateProgressBar(int id, int data) {
    anvil.updateProgressBar(id, data);
  }

  @Override
  public void updateItemName(@Nonnull String newName) {
    anvil.updateItemName(newName);
  }

}
