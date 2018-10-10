package crazypants.enderio.machines.machine.crafter;

import java.util.UUID;

import javax.annotation.Nonnull;

import com.enderio.core.common.inventory.EnderInventory.Type;
import com.enderio.core.common.inventory.Filters;
import com.enderio.core.common.inventory.Filters.PredicateItemStack;
import com.enderio.core.common.inventory.InventorySlot;
import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;
import com.mojang.authlib.GameProfile;

import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.base.capacitor.DefaultCapacitorData;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.machine.base.te.AbstractCapabilityPoweredMachineEntity;
import crazypants.enderio.base.machine.fakeplayer.FakePlayerEIO;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.machines.capacitor.CapacitorKey;
import crazypants.enderio.util.Prep;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ForgeHooks;

@Storable
public class TileCrafter extends AbstractCapabilityPoweredMachineEntity implements IPaintable.IPaintableTileEntity {

  public static class Simple extends TileCrafter {

    public Simple() {
      super(CapacitorKey.SIMPLE_CRAFTER_POWER_INTAKE, CapacitorKey.SIMPLE_CRAFTER_POWER_BUFFER, CapacitorKey.SIMPLE_CRAFTER_POWER_USE);
      getInventory().getSlot(CAPSLOT).set(new ItemStack(ModObject.itemBasicCapacitor.getItemNN(), 1, DefaultCapacitorData.ENDER_CAPACITOR.ordinal()));
    }

    @Override
    public int getTicksPerCraft() {
      return 20;
    }

  }

  private class PredicateItemStackMatch extends PredicateItemStack {

    private final int slot;

    PredicateItemStackMatch(int slot) {
      this.slot = slot;
    }

    @Override
    public boolean doApply(@Nonnull ItemStack input) {
      return input.isItemEqualIgnoreDurability(craftingGrid.getStackInSlot(slot));
    }

  }

  public static final @Nonnull String OUTPUT_SLOT = "OUTPUT";
  public static final @Nonnull String INPUT_SLOT = "INPUT";
  public static final int BASE_TICK_RATE = 10;

  @Store
  DummyCraftingGrid craftingGrid = new DummyCraftingGrid();

  @Store
  private final NNList<ItemStack> containerItems;

  @Store
  private boolean bufferStacks = true;

  private long ticksSinceLastCraft = 0;

  private FakePlayerEIO playerInst;

  public TileCrafter() {
    this(CapacitorKey.CRAFTER_POWER_INTAKE, CapacitorKey.CRAFTER_POWER_BUFFER, CapacitorKey.CRAFTER_POWER_USE);
  }

  public TileCrafter(@Nonnull ICapacitorKey maxEnergyRecieved, @Nonnull ICapacitorKey maxEnergyStored, @Nonnull ICapacitorKey maxEnergyUsed) {
    super(maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);
    containerItems = new NNList<ItemStack>();

    for (int i = 0; i < 9; i++) {
      PredicateItemStackMatch predicate = new PredicateItemStackMatch(i);
      getInventory().add(Type.INPUT, INPUT_SLOT + i, new InventorySlot(predicate, Filters.ALWAYS_TRUE));
    }

    getInventory().add(Type.OUTPUT, OUTPUT_SLOT, new InventorySlot(Filters.ALWAYS_FALSE, Filters.ALWAYS_TRUE));
  }

  @Override
  public boolean isActive() {
    return false;
  }

  @Override
  protected boolean processTasks(boolean redstoneCheck) {
    ticksSinceLastCraft++;

    // process buffered container items
    if (!containerItems.isEmpty()) {
      for (NNIterator<ItemStack> iter = containerItems.iterator(); iter.hasNext();) {
        if (mergeOutput(iter.next())) {
          iter.remove();
        }
      }
      ticksSinceLastCraft = 0;
    } else if (ticksSinceLastCraft > getTicksPerCraft()) {
      if (!craftingGrid.hasValidRecipe()) {
        for (int i = 0; i < 9; i++) {
          ItemStack stack = getInventory().getSlot(INPUT_SLOT + i).get();
          if (Prep.isValid(stack)) {
            containerItems.add(stack);
            getInventory().getSlot(INPUT_SLOT + i).clear();
          }
        }
      } else if (redstoneCheck && hasRequiredPower() && canMergeOutput() && canCraft() && craftRecipe()) {
        ticksSinceLastCraft = 0;
        getEnergy().extractEnergy(getPowerUsePerCraft(), false);
      }
    }
    return false;
  }

  private boolean hasRequiredPower() {
    return getEnergy().getEnergyStored() >= getPowerUsePerCraft();
  }

  protected int getPowerUsePerCraft() {
    return CapacitorKey.CRAFTER_POWER_CRAFT.get(getCapacitorData());
  }

  public int getTicksPerCraft() {
    int impulseHopperSpeedScaled = CapacitorKey.CRAFTER_SPEED.get(getCapacitorData());
    if (impulseHopperSpeedScaled > 0) {
      return BASE_TICK_RATE / impulseHopperSpeedScaled;
    }
    return BASE_TICK_RATE;
  }

  private static final UUID uuid = UUID.fromString("9b381cae-3c95-4a64-b958-1e25b0a4c790");
  private static final GameProfile DUMMY_PROFILE = new GameProfile(uuid, "[EioCrafter]");

  private boolean canCraft() {
    int[] used = new int[9];
    int found = 0, required = 0;
    for (int j = 0; j < 9; j++) {
      ItemStack req = craftingGrid.getStackInSlot(j);
      if (!req.isEmpty()) {
        required++;
        for (int i = 0; i < 9; i++) {
          ItemStack stack = getInventory().getSlot(INPUT_SLOT + i).get();
          if (stack.getCount() > used[i] && stack.isItemEqualIgnoreDurability(req)) {
            found++;
            used[i]++;
            break;
          }
        }
      }
    }
    return found == required;
  }

  private boolean craftRecipe() {

    // (1) Find the items to craft with and put a copy into a temp crafting grid;
    // also record what was used to destroy it later
    InventoryCrafting inv = new InventoryCrafting(new Container() {
      @Override
      public boolean canInteractWith(@Nonnull EntityPlayer var1) {
        return false;
      }
    }, 3, 3);

    for (int j = 0; j < 9; j++) {
      ItemStack req = craftingGrid.getStackInSlot(j);
      if (req.isEmpty()) {
        inv.setInventorySlotContents(j, req);
      } else {
        for (int i = 0; i < 9; i++) {
          ItemStack stack = getInventory().getSlot(INPUT_SLOT + i).get();
          if (stack.isItemEqualIgnoreDurability(req)) {
            inv.setInventorySlotContents(j, stack.splitStack(1));
            break;
          }
        }
      }
      // Note: This needs to be protected by canCraft() always!
    }

    // (2) Find a recipe
    IRecipe recipe = CraftingManager.findMatchingRecipe(inv, world);
    if (recipe == null) {
      for (int j = 0; j < 9; j++) {
        if (!inv.getStackInSlot(j).isEmpty()) {
          containerItems.add(inv.getStackInSlot(j));
        }
      }
      return false;
    }

    // (3) Craft
    ForgeHooks.setCraftingPlayer(getFakePlayer());
    ItemStack output = recipe.getCraftingResult(inv);
    output.onCrafting(world, getFakePlayer(), 1);
    NonNullList<ItemStack> remaining = CraftingManager.getRemainingItems(inv, world);
    ForgeHooks.setCraftingPlayer(null);

    // (4a) ... remove the used up items and ...
    for (int j = 0; j < 9; j++) {
      inv.getStackInSlot(j).shrink(1);
      if (!inv.getStackInSlot(j).isEmpty()) {
        containerItems.add(inv.getStackInSlot(j));
      }
    }
    // (4b) ... and the remains and ...
    for (ItemStack stack : remaining) {
      if (!stack.isEmpty()) {
        containerItems.add(stack.copy());
      }
    }

    // (5) ... put the result into its slot
    if (!mergeOutput(output)) {
      containerItems.add(output);
    }

    return true;
  }

  private @Nonnull FakePlayerEIO getFakePlayer() {
    return playerInst != null ? playerInst : (playerInst = new FakePlayerEIO(world, getLocation(), DUMMY_PROFILE).setOwner(getOwner()));
  }

  private boolean mergeOutput(@Nonnull ItemStack stack) {
    ItemStack oldOutput = getInventory().getSlot(OUTPUT_SLOT).get();
    if (oldOutput.isEmpty()) {
      getInventory().getSlot(OUTPUT_SLOT).set(stack);
      return true;
    } else if (ItemUtil.areStackMergable(oldOutput, stack)) {
      oldOutput.grow(stack.splitStack(Math.min(oldOutput.getMaxStackSize() - oldOutput.getCount(), stack.getCount())).getCount());
      getInventory().getSlot(OUTPUT_SLOT).set(oldOutput);
      return stack.isEmpty();
    }
    return false;
  }

  private boolean canMergeOutput() {
    ItemStack oldOutput = getInventory().getSlot(OUTPUT_SLOT).get();
    if (oldOutput.isEmpty()) {
      return true;
    }
    ItemStack output = craftingGrid.getOutput();
    if (!ItemUtil.areStackMergable(oldOutput, output)) {
      return false;
    }
    return output.getMaxStackSize() >= (oldOutput.getCount() + output.getCount());
  }

  public boolean isBufferStacks() {
    return bufferStacks;
  }

  public void setBufferStacks(boolean bufferStacks) {
    this.bufferStacks = bufferStacks;
  }

  public void updateCraftingOutput() {
    InventoryCrafting inv = new InventoryCrafting(new Container() {

      @Override
      public boolean canInteractWith(@Nonnull EntityPlayer var1) {
        return false;
      }
    }, 3, 3);

    for (int i = 0; i < 9; i++) {
      inv.setInventorySlotContents(i, craftingGrid.getStackInSlot(i));
    }

    ItemStack matches = ItemStack.EMPTY;
    IRecipe recipe = CraftingManager.findMatchingRecipe(inv, world);
    if (recipe != null) {
      matches = recipe.getRecipeOutput();
      if (Prep.isInvalid(matches)) {
        ForgeHooks.setCraftingPlayer(getFakePlayer());
        matches = recipe.getCraftingResult(inv);
        ForgeHooks.setCraftingPlayer(null);
      }
    }
    craftingGrid.setInventorySlotContents(9, matches);
    markDirty();
  }

}
