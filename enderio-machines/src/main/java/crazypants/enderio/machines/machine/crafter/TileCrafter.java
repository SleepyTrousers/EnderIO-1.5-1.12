package crazypants.enderio.machines.machine.crafter;

import java.util.UUID;

import javax.annotation.Nonnull;

import com.enderio.core.common.inventory.EnderInventory.Type;
import com.enderio.core.common.inventory.Filters;
import com.enderio.core.common.inventory.InventorySlot;
import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;
import com.mojang.authlib.GameProfile;

import crazypants.enderio.base.capacitor.DefaultCapacitorData;
import crazypants.enderio.base.capacitor.ICapacitorKey;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.machine.base.te.AbstractCapabilityPoweredMachineEntity;
import crazypants.enderio.base.machine.fakeplayer.FakePlayerEIO;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.machines.capacitor.CapacitorKey;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.handlers.minecraft.HandleItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

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

  public static final @Nonnull String OUTPUT_SLOT = "OUTPUT";
  public static final @Nonnull String INPUT_SLOT = "INPUT";
  public static final int BASE_TICK_RATE = 10;

  @Store
  DummyCraftingGrid craftingGrid = new DummyCraftingGrid();

  @Store(handler = HandleItemStack.HandleItemStackNNList.class)
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
      getInventory().add(Type.INPUT, INPUT_SLOT + i, new InventorySlot(Filters.ALWAYS_TRUE, Filters.ALWAYS_TRUE));
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
    if (!redstoneCheck || !craftingGrid.hasValidRecipe() || !canMergeOutput() || !hasRequiredPower()) {
      return false;
    }
    int ticksPerCraft = getTicksPerCraft();
    if (ticksSinceLastCraft <= ticksPerCraft) {
      return false;
    }
    ticksSinceLastCraft = 0;

    // process buffered container items
    if (!containerItems.isEmpty()) {
      NNIterator<ItemStack> iter = containerItems.iterator();
      while (iter.hasNext()) {
        ItemStack stack = iter.next();
        InventorySlot outSlot = getInventory().getSlot(OUTPUT_SLOT);
        if (outSlot.get().isEmpty()) {
          outSlot.set(stack);
          iter.remove();
        }
      }
      return false;
    }

    if (craftRecipe()) {
      getEnergy().extractEnergy(getPowerUsePerCraft(), false);
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

  static boolean compareDamageable(@Nonnull ItemStack stack, @Nonnull ItemStack req) {
    if (stack.isItemEqual(req)) {
      return true;
    }
    if (stack.isItemStackDamageable() && stack.getItem() == req.getItem()) {
      return stack.getItemDamage() < stack.getMaxDamage();
    }
    return false;
  }

  private static final UUID uuid = UUID.fromString("9b381cae-3c95-4a64-b958-1e25b0a4c790");
  private static final GameProfile DUMMY_PROFILE = new GameProfile(uuid, "[EioCrafter]");

  private boolean craftRecipe() {

    // (1) Find the items to craft with and put a copy into a temp crafting grid;
    // also record what was used to destroy it later
    InventoryCrafting inv = new InventoryCrafting(new Container() {
      @Override
      public boolean canInteractWith(@Nonnull EntityPlayer var1) {
        return false;
      }
    }, 3, 3);

    int[] usedItems = new int[9];

    for (int j = 0; j < 9; j++) {
      ItemStack req = craftingGrid.getStackInSlot(j);
      if (!req.isEmpty()) {
        for (int i = 0; i < 9; i++) {
          ItemStack stack = getInventory().getSlot(INPUT_SLOT + i).get();
          if (!stack.isEmpty() && stack.getCount() > usedItems[i] && compareDamageable(stack, req)) {
            req = ItemStack.EMPTY;
            usedItems[i]++;
            ItemStack craftingItem = stack.copy();
            craftingItem.setCount(1);
            inv.setInventorySlotContents(j, craftingItem);
            break;
          }
        }
        if (!req.isEmpty()) {
          return false;
        }
      }
    }

    // (2) Try to craft with the temp grid
    ItemStack output = ItemStack.EMPTY;
    IRecipe recipe = CraftingManager.findMatchingRecipe(inv, world);
    if (recipe != null) {
      output = recipe.getRecipeOutput();
    }

    // (3) If we got a result, ...
    if (!output.isEmpty()) {
      if (playerInst == null) {
        playerInst = new FakePlayerEIO(world, getLocation(), DUMMY_PROFILE);
        playerInst.setOwner(getOwner());
      }
      MinecraftForge.EVENT_BUS.post(new ItemCraftedEvent(playerInst, output, inv));

      NonNullList<ItemStack> remaining = CraftingManager.getRemainingItems(inv, world);

      // (3a) ... remove the used up items and ...
      for (int i = 0; i < 9; i++) {
        ItemStack stack = getInventory().getSlot(INPUT_SLOT + i).get();
        for (int j = 0; j < usedItems[i] && !stack.isEmpty(); j++) {
          getInventory().getSlot(INPUT_SLOT + i).set(eatOneItemForCrafting(i, stack.copy(), remaining, usedItems[i]));
        }
      }

      for (ItemStack stack : remaining) {
        if (!stack.isEmpty()) {
          containerItems.add(stack.copy());
        }
      }

      // (3b) ... put the result into its slot
      ItemStack oldOutput = getInventory().getSlot(OUTPUT_SLOT).get();
      if (oldOutput.isEmpty()) {
        getInventory().getSlot(OUTPUT_SLOT).set(output);
      } else if (ItemUtil.areStackMergable(oldOutput, output)) {
        ItemStack cur = oldOutput.copy();
        cur.grow(output.getCount());
        if (cur.getCount() > cur.getMaxStackSize()) {
          // we check beforehand that there is enough free space, but some mod may return different
          // amounts based on the nbt of the input items (e.g. magical wood)
          ItemStack overflow = cur.copy();
          overflow.setCount(cur.getCount() - cur.getMaxStackSize());
          cur.setCount(cur.getMaxStackSize());
          containerItems.add(overflow);
        }
        getInventory().getSlot(OUTPUT_SLOT).set(cur);
      } else {
        // some mod may return different nbt based on the nbt of the input items (e.g. TE machines?)
        containerItems.add(output);
      }
    } else {
      // Crafting failed. This is not supposed to happen, but if a recipe is nbt-sensitive, it can.
      // To avoid being stuck in a dead loop, we flush the non-working input items.
      for (int j = 0; j < 9; j++) {
        ItemStack stack = getInventory().getSlot(INPUT_SLOT + j).get();
        if (usedItems[j] > 0 && !stack.isEmpty()) {
          ItemStack rejected = stack.copy();
          rejected.setCount(Math.min(stack.getCount(), usedItems[j]));
          containerItems.add(rejected);
          if (stack.getCount() <= usedItems[j]) {
            this.getInventory().insertItem(j, ItemStack.EMPTY, false);
          } else {
            stack.shrink(usedItems[j]);
          }
        }
      }
    }

    getEnergy().useEnergy(CapacitorKey.CRAFTER_POWER_CRAFT);
    return true;
  }

  @Nonnull
  private ItemStack eatOneItemForCrafting(int slot, @Nonnull ItemStack avail, NonNullList<ItemStack> remaining, int usedItems) {
    // if one of the remaining items is the container item for the input, place the remaining item in the same grid
    if (remaining != null && remaining.size() > 0 && avail.getItem().hasContainerItem(avail)) {
      ItemStack used = avail.getItem().getContainerItem(avail);
      if (!used.isEmpty()) {
        for (int i = 0; i < remaining.size(); i++) {
          ItemStack s = remaining.get(i);
          if (!s.isEmpty() && s.isItemEqualIgnoreDurability(used)) {
            remaining.set(i, ItemStack.EMPTY);
            return s;
          }
        }
      }
    }
    avail.shrink(usedItems);
    if (avail.getCount() == 0) {
      avail = ItemStack.EMPTY;
    }
    return avail;
  }

  private boolean canMergeOutput() {
    ItemStack oldOutput = getInventory().getStackInSlot(9);
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
    }
    craftingGrid.setInventorySlotContents(9, matches);
    markDirty();
  }

}
