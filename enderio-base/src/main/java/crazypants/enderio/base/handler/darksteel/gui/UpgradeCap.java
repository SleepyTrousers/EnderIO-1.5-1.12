package crazypants.enderio.base.handler.darksteel.gui;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.api.upgrades.IRule;
import crazypants.enderio.api.upgrades.IRule.CheckResult;
import crazypants.enderio.base.handler.darksteel.UpgradeRegistry;
import crazypants.enderio.base.init.ModObject;
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

  protected final @Nonnull NNList<IDarkSteelUpgrade> stacks = new NNList<>();
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
      UpgradeRegistry.getUpgrades().stream()
          .filter(upgrade -> upgrade.getRules().stream().filter(rule -> rule instanceof IRule.StaticRule).allMatch(rule -> rule.check(owner, item).passes()))
          .forEachOrdered(stacks::add);
    } else {
      stacks.clear(); // no stacks, no slots, nothing will happen
      this.item = (IDarkSteelItem) ModObject.itemDarkSteelChestplate.getItemNN(); // dummy value
    }
  }

  @Override
  public int getSlots() {
    return stacks.size();
  }

  @Override
  @Nonnull
  public ItemStack getStackInSlot(int slot) {
    validateSlotIndex(slot);
    return stacks.get(slot).hasUpgrade(getOwner()) ? UpgradeRegistry.getUpgradeItem(stacks.get(slot)) : Prep.getEmpty();
  }

  @Nonnull
  public ItemStack getUpgradeItem(int slot) {
    validateSlotIndex(slot);
    return UpgradeRegistry.getUpgradeItem(stacks.get(slot));
  }

  @Override
  @Nonnull
  public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
    validateSlotIndex(slot);

    IDarkSteelUpgrade upgrade = stacks.get(slot);

    if (!UpgradeRegistry.isUpgradeItem(upgrade, stack) || !upgrade.canAddToItem(getOwner(), item)) {
      return stack;
    }

    if (!simulate && !player.world.isRemote) {
      upgrade.addToItem(getOwner(), item);
      syncChangesToClient();
    }

    return ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - 1);
  }

  public boolean canInsert(int slot) {
    validateSlotIndex(slot);

    IDarkSteelUpgrade upgrade = stacks.get(slot);

    return upgrade.canAddToItem(getOwner(), item);
  }

  public @Nullable List<ITextComponent> checkInsert(int slot, @Nonnull ItemStack stack) {
    validateSlotIndex(slot);

    IDarkSteelUpgrade upgrade = stacks.get(slot);
    if (!UpgradeRegistry.isUpgradeItem(upgrade, stack) || upgrade.hasUpgrade(getOwner())) {
      return null;
    }

    return upgrade.getRules().stream().map(rule -> rule.check(getOwner(), item)).filter(CheckResult::hasResult).map(CheckResult::getResult)
        .collect(Collectors.toList());
  }

  @Override
  @Nonnull
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    if (amount == 0) {
      return ItemStack.EMPTY;
    }

    validateSlotIndex(slot);

    IDarkSteelUpgrade upgrade = stacks.get(slot);
    boolean existing = upgrade.hasUpgrade(getOwner());

    if (!existing
        || !stacks.stream().filter(up -> up != upgrade && up.hasUpgrade(getOwner())).allMatch(up -> up.canOtherBeRemoved(getOwner(), item, upgrade))) {
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

  protected void validateSlotIndex(int slot) {
    if (slot < 0 || slot >= stacks.size()) {
      throw new RuntimeException("Slot " + slot + " not in valid range - [0," + stacks.size() + ")");
    }
  }

  public boolean isStillConnectedToPlayer() {
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
    if (Prep.isValid(stack)) {
      insertItem(slot, stack.copy(), false);
    } else {
      extractItem(slot, 1, false);
    }

  }

  public @Nonnull EntityEquipmentSlot getEquipmentSlot() {
    return equipmentSlot;
  }

  public @Nonnull IDarkSteelUpgrade getUpgrade(int slot) {
    validateSlotIndex(slot);
    return stacks.get(slot);
  }

}
