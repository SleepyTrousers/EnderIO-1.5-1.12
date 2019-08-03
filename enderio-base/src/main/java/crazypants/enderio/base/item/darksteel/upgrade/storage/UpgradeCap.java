package crazypants.enderio.base.item.darksteel.upgrade.storage;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade.IRule;
import crazypants.enderio.base.handler.darksteel.UpgradeRegistry;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.util.Prep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class UpgradeCap implements IItemHandler {

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
      this.item = (IDarkSteelItem) getOwner().getItem();
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
    return stacks.get(slot).hasUpgrade(getOwner()) ? stacks.get(slot).getUpgradeItem() : Prep.getEmpty();
  }

  @Nonnull
  public ItemStack getUpgradeItem(int slot) {
    validateSlotIndex(slot);
    return stacks.get(slot).getUpgradeItem();
  }

  @Override
  @Nonnull
  public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
    if (stack.isEmpty()) {
      return stack;
    }

    validateSlotIndex(slot);

    IDarkSteelUpgrade upgrade = stacks.get(slot);
    boolean existing = upgrade.hasUpgrade(getOwner());

    if (existing || !upgrade.isUpgradeItem(stack) || upgrade.getRules().stream().allMatch(rule -> rule.check(getOwner(), item).passes())) {
      return stack;
    }

    if (!simulate && !player.world.isRemote) {
      upgrade.addToItem(getOwner(), item);
    }

    return ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - 1);
  }

  public boolean canInsert(int slot) {
    validateSlotIndex(slot);

    IDarkSteelUpgrade upgrade = stacks.get(slot);
    if (upgrade.hasUpgrade(getOwner())) {
      return false;
    }

    return upgrade.getRules().stream().map(rule -> rule.check(getOwner(), item)).allMatch(rule -> rule.passes());
  }

  public @Nullable List<ITextComponent> checkInsert(int slot, @Nonnull ItemStack stack) {
    if (stack.isEmpty()) {
      return null;
    }

    validateSlotIndex(slot);

    IDarkSteelUpgrade upgrade = stacks.get(slot);
    if (upgrade.hasUpgrade(getOwner()) || !upgrade.isUpgradeItem(stack)) {
      return null;
    }

    return upgrade.getRules().stream().map(rule -> rule.check(getOwner(), item)).filter(result -> result.hasResult()).map(result -> result.getResult())
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
    }
    return upgrade.getUpgradeItem();
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

}
