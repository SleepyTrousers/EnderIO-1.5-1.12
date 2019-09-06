package crazypants.enderio.base.handler.darksteel.gui;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.api.upgrades.IRule.CheckResult;
import crazypants.enderio.base.handler.darksteel.Rules;
import crazypants.enderio.base.handler.darksteel.UpgradeRegistry;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.util.NNPair;
import crazypants.enderio.util.NbtValue;
import crazypants.enderio.util.Prep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

public class UpgradeCap implements IItemHandlerModifiable {

  private final static class Holder {
    final IDarkSteelUpgrade upgrade;
    boolean hasNext = false;
    boolean isHead = true;

    Holder(IDarkSteelUpgrade upgrade) {
      this.upgrade = upgrade;
    }

    String getKey() {
      return NullHelper.first(upgrade.getSortKey().getKey(), "");
    }

  }

  protected final static int INVSIZE = 9;

  protected final NNList<Holder> stacks = new NNList<>();
  protected final ISlotSelector ss;
  protected final EntityPlayer player;
  protected final boolean addOnly;

  public UpgradeCap(ISlotSelector ss, EntityPlayer player, boolean addOnly) {
    this.ss = ss;
    this.player = player;
    this.addOnly = addOnly;
    UpgradeRegistry.getUpgrades().stream().map(Holder::new).forEachOrdered(this::addHolder);
  }

  /**
   * see {@link #showUpgrade(int)}
   */
  private void addHolder(Holder holder) {
    if (!stacks.isEmpty()) {
      final Holder last = stacks.get(stacks.size() - 1);
      if (last.getKey().equals(holder.getKey())) {
        holder.isHead = false;
        last.hasNext = true;
      }
    }
    stacks.add(holder);
  }

  @Override
  public int getSlots() {
    return ss.isAnvil() ? 0 : (stacks.size() + INVSIZE);
  }

  /**
   * Upgrades with multiple levels only have the highest applied upgrade on the item. In this case, the lower tier upgrades need to be shown in the GUI
   * artificially. This method checks if an upgrade either is applied itself or is the lower level of an applied higher tier.
   * <p>
   * This relies on tiered upgrades being in order.
   * <p>
   * Note that this method should not be used on the "extract" path. Only upgrades that are actually present may be removed. Extraction will automatically fail
   * for "fake" upgrades as the extraction logic will see those upgrades as "not present".
   */
  private boolean showUpgrade(int slot) {
    Holder holder = stacks.get(slot);
    return holder.upgrade.hasUpgrade(getOwner().getLeft()) || (holder.hasNext && showUpgrade(slot + 1));
  }

  @Override
  @Nonnull
  public ItemStack getStackInSlot(int slot) {
    if (isInventorySlot(slot)) {
      return NbtValue.DSUINV.getStack(getOwner().getLeft(), slot - stacks.size());
    }
    return showUpgrade(slot) ? UpgradeRegistry.getUpgradeItem(stacks.get(slot).upgrade) : Prep.getEmpty();
  }

  public ItemStack getUpgradeItem(int slot) {
    if (isInventorySlot(slot)) {
      return new ItemStack(ModObject.itemDarkSteelUpgrade.getItemNN());
    }
    return UpgradeRegistry.getUpgradeItem(stacks.get(slot).upgrade);
  }

  @Override
  @Nonnull
  public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
    final NNPair<ItemStack, IDarkSteelItem> owner = getOwner();
    if (isInventorySlot(slot)) {
      if (Prep.isValid(NbtValue.DSUINV.getStack(owner.getLeft(), slot - stacks.size()))) {
        return stack;
      }
      if (stack.getItem() != ModObject.itemDarkSteelUpgrade.getItemNN()) {
        return stack;
      }
      if (!simulate) {
        NbtValue.DSUINV.setStack(owner.getLeft(), slot - stacks.size(), ItemHandlerHelper.copyStackWithSize(stack, 1));
        syncChangesToClient();
      }
      return ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - 1);
    }

    IDarkSteelUpgrade upgrade = stacks.get(slot).upgrade;

    if (showUpgrade(slot) || !UpgradeRegistry.isUpgradeItem(upgrade, stack) || !upgrade.canAddToItem(owner.getLeft(), owner.getRight())) {
      return stack;
    }

    if (!simulate && !player.world.isRemote) {
      upgrade.addToItem(owner.getLeft(), owner.getRight());
      syncChangesToClient();
    }

    return ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - 1);
  }

  @Override
  public boolean isItemValid(int slot, ItemStack stack) {
    return insertItem(slot, stack, true).getCount() != stack.getCount();
  }

  /**
   * Checks if the slot should be drawn normal or should be drawn as "blocked".
   * <p>
   * Block slots represent upgrades that cannot be applied because either their prerequisites are not met or they conflict with another already applied upgrade.
   * <p>
   * Slots that already contain an upgrade are always "open".
   */
  public boolean isSlotBlocked(int slot) {
    return !isInventorySlot(slot) && !showUpgrade(slot) && !stacks.get(slot).upgrade.canAddToItem(getOwner().getLeft(), getOwner().getRight());
  }

  /**
   * Finds the reason a slot is not open.
   * <p>
   * Please note that the list may be empty if none of the rules provide a reason text (or if a slot is not blocked).
   */
  public List<ITextComponent> getSlotBlockedReason(int slot) {
    final NNPair<ItemStack, IDarkSteelItem> owner = getOwner();
    return NullHelper
        .first(isSlotBlocked(slot)
            ? stacks.get(slot).upgrade.getRules().stream().map(rule -> rule.check(owner.getLeft(), owner.getRight())).filter(CheckResult::hasResult)
                .map(CheckResult::getResult).collect(Collectors.toList())
            : null, NNList.emptyList());
  }

  @Override
  @Nonnull
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    if (amount == 0 || addOnly) {
      return ItemStack.EMPTY;
    }

    final NNPair<ItemStack, IDarkSteelItem> owner = getOwner();
    if (isInventorySlot(slot)) {
      ItemStack result = NbtValue.DSUINV.getStack(owner.getLeft(), slot - stacks.size());
      if (!simulate) {
        NbtValue.DSUINV.setStack(owner.getLeft(), slot - stacks.size(), Prep.getEmpty());
        syncChangesToClient();
      }
      return result;
    }

    IDarkSteelUpgrade upgrade = stacks.get(slot).upgrade;
    boolean existing = upgrade.hasUpgrade(owner.getLeft());

    if (!existing
        || !stacks.stream().map(holder -> holder.upgrade).filter(up -> up != upgrade && up.hasUpgrade(owner.getLeft()))
            .allMatch(up -> up.canOtherBeRemoved(owner.getLeft(), owner.getRight(), upgrade))
        || !owner.getRight().canUpgradeBeRemoved(owner.getLeft(), upgrade)) {
      return ItemStack.EMPTY;
    }

    if (!simulate && !player.world.isRemote) {
      upgrade.removeFromItem(owner.getLeft(), owner.getRight());
      syncChangesToClient();
    }
    return UpgradeRegistry.getUpgradeItem(upgrade);
  }

  /**
   * The game doesn't sync player inventory slots that don't have a matching slot in the currently open container. We have to do that ourselves.
   */
  protected void syncChangesToClient() {
    if (player instanceof EntityPlayerMP) {
      if (ss.isItem()) {
        player.openContainer.detectAndSendChanges();
      } else {
        final ItemStack owner = getOwner().getLeft();
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
          if (player.inventory.getStackInSlot(i) == owner) {
            ((EntityPlayerMP) player).connection.sendPacket(new SPacketSetSlot(-2, i, player.inventory.getStackInSlot(i)));
          }
        }
      }
    }
  }

  @Override
  public int getSlotLimit(int slot) {
    return 1;
  }

  protected boolean isInventorySlot(int slot) {
    if (slot < 0 || slot >= stacks.size() + INVSIZE) {
      throw new RuntimeException("Slot " + slot + " not in valid range - [0," + stacks.size() + ")");
    }
    return slot >= stacks.size();
  }

  public boolean isStillConnectedToPlayer() {
    return ss.isAnvil() || ss.getItem(player).getItem() instanceof IDarkSteelItem;
  }

  protected NNPair<ItemStack, IDarkSteelItem> getOwner() {
    ItemStack current = ss.getItem(player);
    if (current.getItem() instanceof IDarkSteelItem) {
      return NNPair.of(current, (IDarkSteelItem) current.getItem());
    }
    return NNPair.of(new ItemStack(ModObject.itemDarkSteelPickaxe.getItemNN()), (IDarkSteelItem) ModObject.itemDarkSteelPickaxe.getItemNN()); // dummy
  }

  @Override
  public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
    if (isInventorySlot(slot)) {
      if (Prep.isValid(stack) && stack.getItem() != ModObject.itemDarkSteelUpgrade.getItemNN()) {
        return;
      }
      NbtValue.DSUINV.setStack(getOwner().getLeft(), slot - stacks.size(), ItemHandlerHelper.copyStackWithSize(stack, 1));
      syncChangesToClient();
      return;
    }

    if (Prep.isValid(stack)) {
      insertItem(slot, stack.copy(), false);
    } else {
      extractItem(slot, 1, false);
    }

  }

  public ISlotSelector getSlotSelector() {
    return ss;
  }

  public boolean isHead(int slot) {
    return isInventorySlot(slot) ? slot == stacks.size() : stacks.get(slot).isHead;
  }

  public boolean isVisible(int slot) {
    if (!isStillConnectedToPlayer()) {
      return false;
    }
    if (isInventorySlot(slot)) {
      return true;
    }
    return stacks.get(slot).upgrade.getRules().stream().filter(Rules::isStatic).allMatch(Rules.makeChecker(getOwner()));
  }

  public boolean isChanged() {
    if (ss.isItem()) {
      return true;
    }
    return false;
  }

  public boolean isAvailable() {
    if (ss.isAnvil()) {
      return true;
    }
    if (!isStillConnectedToPlayer()) {
      return false;
    }
    for (Holder holder : stacks) {
      if (holder.upgrade.getRules().stream().filter(Rules::isStatic).allMatch(Rules.makeChecker(getOwner()))) {
        return true;
      }
    }
    return false;
  }

  public boolean isAddOnly() {
    return addOnly;
  }

  public void dropAll(boolean wipeOriginal) {
    if (!player.world.isRemote) {
      final NNPair<ItemStack, IDarkSteelItem> owner = getOwner();
      for (int slot = 0; slot < getSlots(); slot++) {
        final ItemStack stack = getStackInSlot(slot);
        if (Prep.isValid(stack) && (isInventorySlot(slot) || owner.getRight().canUpgradeBeRemoved(owner.getLeft(), stacks.get(slot).upgrade))) {
          dropSubInventory(stack, stacks.get(slot).upgrade.getInventoryHandler(owner.getLeft()), wipeOriginal);
          if (!player.inventory.addItemStackToInventory(stack)) {
            player.dropItem(stack, true);
          }
        }
      }
      if (wipeOriginal) {
        owner.getLeft().setTagCompound(null);
      }
    }
  }

  private void dropSubInventory(final ItemStack stack, @Nullable IItemHandler handler, boolean wipeOriginal) {
    if (handler != null) {
      for (int slot = 0; slot < handler.getSlots(); slot++) {
        final ItemStack stack1 = handler.getStackInSlot(slot);
        if (Prep.isValid(stack)) {
          if (!player.inventory.addItemStackToInventory(stack1)) {
            player.dropItem(stack1, true);
          }
          if (wipeOriginal) {
            if (handler instanceof IItemHandlerModifiable) {
              ((IItemHandlerModifiable) handler).setStackInSlot(slot, Prep.getEmpty());
            } else {
              handler.extractItem(slot, stack1.getCount(), false);
            }
          }
        }
      }
    }
  }

}
