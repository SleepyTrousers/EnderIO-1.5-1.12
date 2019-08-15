package crazypants.enderio.base.handler.darksteel.gui;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.api.upgrades.IRule.CheckResult;
import crazypants.enderio.base.handler.darksteel.Rules;
import crazypants.enderio.base.handler.darksteel.UpgradeRegistry;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.util.NbtValue;
import crazypants.enderio.util.Prep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

public class UpgradeCap implements IItemHandlerModifiable {

  private final static class Holder {
    final @Nonnull IDarkSteelUpgrade upgrade;
    boolean hasNext = false;
    boolean isHead = true;

    Holder(@Nonnull IDarkSteelUpgrade upgrade) {
      this.upgrade = upgrade;
    }

    String getKey() {
      return upgrade.getSortKey().getKey();
    }

  }

  protected final static int INVSIZE = 9;

  protected final @Nonnull NNList<Holder> stacks = new NNList<>();
  protected final @Nonnull EntityEquipmentSlot equipmentSlot;
  protected final @Nonnull ItemStack owner;
  protected final @Nonnull IDarkSteelItem item;
  protected final @Nonnull EntityPlayer player;

  public UpgradeCap(@Nonnull EntityEquipmentSlot equipmentSlot, @Nonnull EntityPlayer player) {
    this.equipmentSlot = equipmentSlot;
    this.player = player;
    this.owner = player.getItemStackFromSlot(equipmentSlot);
    if (owner.getItem() instanceof IDarkSteelItem) {
      this.item = (IDarkSteelItem) owner.getItem();
      UpgradeRegistry.getUpgrades().stream().filter(upgrade -> upgrade.getRules().stream().filter(Rules::isStatic).allMatch(Rules.makeChecker(owner, item)))
          .map(Holder::new).forEachOrdered(this::addHolder);
    } else {
      this.stacks.clear(); // no stacks, no slots, nothing will happen
      this.item = (IDarkSteelItem) ModObject.itemDarkSteelChestplate.getItemNN(); // dummy value
    }
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
    return stacks.isEmpty() ? 0 : stacks.size() + INVSIZE;
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
    return holder.upgrade.hasUpgrade(getOwner()) || (holder.hasNext && showUpgrade(slot + 1));
  }

  @Override
  @Nonnull
  public ItemStack getStackInSlot(int slot) {
    if (isInventorySlot(slot)) {
      return NbtValue.DSUINV.getStack(getOwner(), slot - stacks.size());
    }
    return showUpgrade(slot) ? UpgradeRegistry.getUpgradeItem(stacks.get(slot).upgrade) : Prep.getEmpty();
  }

  @Nonnull
  public ItemStack getUpgradeItem(int slot) {
    if (isInventorySlot(slot)) {
      return new ItemStack(ModObject.itemDarkSteelUpgrade.getItemNN());
    }
    return UpgradeRegistry.getUpgradeItem(stacks.get(slot).upgrade);
  }

  @Override
  @Nonnull
  public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
    if (isInventorySlot(slot)) {
      if (Prep.isValid(NbtValue.DSUINV.getStack(getOwner(), slot - stacks.size()))) {
        return stack;
      }
      if (stack.getItem() != ModObject.itemDarkSteelUpgrade.getItemNN()) {
        return stack;
      }
      if (!simulate) {
        NbtValue.DSUINV.setStack(getOwner(), slot - stacks.size(), ItemHandlerHelper.copyStackWithSize(stack, 1));
        syncChangesToClient();
      }
      return ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - 1);
    }

    IDarkSteelUpgrade upgrade = stacks.get(slot).upgrade;

    if (showUpgrade(slot) || !UpgradeRegistry.isUpgradeItem(upgrade, stack) || !upgrade.canAddToItem(getOwner(), item)) {
      return stack;
    }

    if (!simulate && !player.world.isRemote) {
      upgrade.addToItem(getOwner(), item);
      syncChangesToClient();
    }

    return ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - 1);
  }

  /**
   * Checks if the slot should be drawn normal or should be drawn as "blocked".
   * <p>
   * Block slots represent upgrades that cannot be applied because either their prerequisites are not met or they conflict with another already applied upgrade.
   * <p>
   * Slots that already contain an upgrade are always "open".
   */
  public boolean isSlotBlocked(int slot) {
    return !isInventorySlot(slot) && !showUpgrade(slot) && !stacks.get(slot).upgrade.canAddToItem(getOwner(), item);
  }

  /**
   * Finds the reason a slot is not open.
   * <p>
   * Please note that the list may be empty if none of the rules provide a reason text (or if a slot is not blocked).
   */
  public @Nonnull List<ITextComponent> getSlotBlockedReason(int slot) {
    return NullHelper.first(
        isSlotBlocked(slot)
            ? stacks.get(slot).upgrade.getRules().stream().map(rule -> rule.check(getOwner(), item)).filter(CheckResult::hasResult).map(CheckResult::getResult)
                .collect(Collectors.toList())
            : null,
        NNList.emptyList());
  }

  @Override
  @Nonnull
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    if (amount == 0) {
      return ItemStack.EMPTY;
    }

    if (isInventorySlot(slot)) {
      ItemStack result = NbtValue.DSUINV.getStack(getOwner(), slot - stacks.size());
      if (!simulate) {
        NbtValue.DSUINV.setStack(getOwner(), slot - stacks.size(), Prep.getEmpty());
        syncChangesToClient();
      }
      return result;
    }

    IDarkSteelUpgrade upgrade = stacks.get(slot).upgrade;
    boolean existing = upgrade.hasUpgrade(getOwner());

    if (!existing || !stacks.stream().map(holder -> holder.upgrade).filter(up -> up != upgrade && up.hasUpgrade(getOwner()))
        .allMatch(up -> up.canOtherBeRemoved(getOwner(), item, upgrade))) {
      return ItemStack.EMPTY;
    }

    if (!simulate && !player.world.isRemote) {
      upgrade.removeFromItem(getOwner(), item);
      syncChangesToClient();
    }
    return UpgradeRegistry.getUpgradeItem(upgrade);
  }

  /**
   * The game doesn't sync player inventory slots that don't have a matching slot in the currently open container. We have to do that ourselves.
   */
  protected void syncChangesToClient() {
    if (player instanceof EntityPlayerMP) {
      for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
        if (player.inventory.getStackInSlot(i) == owner) {
          ((EntityPlayerMP) player).connection.sendPacket(new SPacketSetSlot(-2, i, owner));
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
    if (stacks.isEmpty()) {
      return true;
    }
    if (player.world.isRemote) {
      return player.getItemStackFromSlot(equipmentSlot).getItem() == owner.getItem();
    }
    return owner == player.getItemStackFromSlot(equipmentSlot);
  }

  protected @Nonnull ItemStack getOwner() {
    if (player.world.isRemote) {
      ItemStack current = player.getItemStackFromSlot(equipmentSlot);
      if (current.getItem() == owner.getItem()) {
        return current;
      }
    }
    return owner;
  }

  @Override
  public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
    if (isInventorySlot(slot)) {
      if (stack.getItem() != ModObject.itemDarkSteelUpgrade.getItemNN()) {
        return;
      }
      NbtValue.DSUINV.setStack(getOwner(), slot - stacks.size(), ItemHandlerHelper.copyStackWithSize(stack, 1));
      syncChangesToClient();
      return;
    }

    if (Prep.isValid(stack)) {
      insertItem(slot, stack.copy(), false);
    } else {
      extractItem(slot, 1, false);
    }

  }

  public @Nonnull EntityEquipmentSlot getEquipmentSlot() {
    return equipmentSlot;
  }

  public boolean isHead(int slot) {
    return isInventorySlot(slot) ? slot == stacks.size() : stacks.get(slot).isHead;
  }

}
