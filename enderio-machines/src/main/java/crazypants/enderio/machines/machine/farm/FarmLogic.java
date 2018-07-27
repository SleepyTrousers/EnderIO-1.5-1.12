package crazypants.enderio.machines.machine.farm;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;
import com.enderio.core.common.util.NNList.ShortCallback;
import com.enderio.core.common.util.NullHelper;
import com.mojang.authlib.GameProfile;

import crazypants.enderio.api.farm.FarmNotification;
import crazypants.enderio.api.farm.FarmingAction;
import crazypants.enderio.api.farm.IFarmer;
import crazypants.enderio.api.farm.IFarmingTool;
import crazypants.enderio.base.farming.FarmingTool;
import crazypants.enderio.base.farming.PickupWorld;
import crazypants.enderio.base.integration.tic.TicProxy;
import crazypants.enderio.base.machine.fakeplayer.FakePlayerEIO;
import crazypants.enderio.machines.config.config.FarmConfig;
import crazypants.enderio.machines.network.PacketHandler;
import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FarmLogic implements IFarmer {

  private class InsertCallback implements ShortCallback<FarmSlots> {
    private final @Nonnull ItemStack stack;
    private FarmSlots emptySlot = null;

    public InsertCallback(@Nonnull ItemStack stack) {
      this.stack = stack;
    }

    @Override
    public boolean apply(@Nonnull FarmSlots slot) {
      if (slot.isValid(owner, stack)) {
        ItemStack slotStack = slot.get(owner);
        if (Prep.isInvalid(slotStack)) {
          if (emptySlot == null) {
            emptySlot = slot;
          }
        } else if (ItemUtil.areStackMergable(stack, slotStack) && !ItemUtil.isStackFull(slotStack)) {
          int addable = Math.max(0, Math.min(slotStack.getMaxStackSize(), slot.getInventoryStackLimit(owner)) - slotStack.getCount());
          if (addable >= stack.getCount()) {
            slotStack.grow(stack.getCount());
            stack.setCount(0);
            owner.markDirty();
          } else {
            slotStack.grow(addable);
            stack.shrink(addable);
            owner.markDirty();
          }
        }
      }
      return Prep.isInvalid(stack);
    }

    @Override
    public boolean finish() {
      if (emptySlot != null) {
        emptySlot.set(owner, stack.copy());
        stack.setCount(0);
        owner.markDirty();
      }
      return Prep.isInvalid(stack);
    }
  }

  private static final @Nonnull GameProfile FARMER_PROFILE = new GameProfile(UUID.fromString("c1ddfd7f-120a-4437-8b64-38660d3ec62d"), "[EioFarmer]");

  private final @Nonnull TileFarmStation owner;
  private final @Nonnull FakePlayerEIO farmerJoe;

  public FarmLogic(@Nonnull TileFarmStation owner) {
    this.owner = owner;
    farmerJoe = new FakePlayerEIO(owner.getWorld(), owner.getLocation(), FARMER_PROFILE);
    farmerJoe.setOwner(owner.getOwner());
    farmerJoe.world = new PickupWorld(owner.getWorld(), farmerJoe);
  }

  // General data

  @Override
  @Nonnull
  public FakePlayerEIO getFakePlayer() {
    return farmerJoe;
  }

  @Override
  @Nonnull
  public World getWorld() {
    return owner.getWorld();
  }

  @Override
  @Nonnull
  public BlockPos getLocation() {
    return owner.getLocation();
  }

  @Override
  @Nonnull
  public IBlockState getBlockState(@Nonnull BlockPos pos) {
    if (getWorld().isBlockLoaded(pos)) {
      return getWorld().getBlockState(pos);
    } else {
      return Blocks.AIR.getDefaultState();
    }
  }

  @Override
  public int getFarmSize() {
    return owner.getFarmSize();
  }

  // Notifications

  @Override
  public void setNotification(@Nonnull FarmNotification notification) {
    owner.setNotification(notification);
  }

  // Seeds

  @Override
  @Nonnull
  public ItemStack getSeedTypeInSuppliesFor(@Nonnull BlockPos pos) {
    return mapBlockPosToSeedSlot(pos).get(owner);
  }

  @Override
  @Nonnull
  public ItemStack takeSeedFromSupplies(@Nonnull BlockPos pos, boolean simulate) {
    FarmSlots slot = mapBlockPosToSeedSlot(pos);
    ItemStack inv = slot.get(owner);
    if (inv.getCount() > 1 || !owner.isSlotLocked(slot)) {
      if (simulate) {
        return inv.copy().splitStack(1);
      } else {
        owner.markDirty();
        return inv.splitStack(1);
      }
    }
    return Prep.getEmpty();
  }

  private @Nonnull FarmSlots mapBlockPosToSeedSlot(@Nonnull BlockPos pos) {
    BlockPos offset = pos.subtract(getLocation());
    if (offset.getX() <= 0 && offset.getZ() > 0) {
      return FarmSlots.SEED1;
    } else if (offset.getX() > 0 && offset.getZ() >= 0) {
      return FarmSlots.SEED2;
    } else if (offset.getX() < 0 && offset.getZ() <= 0) {
      return FarmSlots.SEED3;
    }
    return FarmSlots.SEED4;
  }

  @Override
  @Nonnull
  public ItemStack takeSeedFromSupplies(@Nonnull ItemStack seeds, @Nonnull BlockPos pos) {
    FarmSlots slot = mapBlockPosToSeedSlot(pos);
    ItemStack inv = slot.get(owner);
    if (Prep.isValid(inv) && (Prep.isInvalid(seeds) || ItemUtil.areStacksEqual(seeds, inv))) {
      if (inv.getCount() > 1 || !owner.isSlotLocked(slot)) {
        owner.markDirty();
        return inv.splitStack(1);
      }
    }
    return Prep.getEmpty();
  }

  @Override
  public boolean hasSeed(@Nonnull ItemStack seeds, @Nonnull BlockPos pos) {
    return ItemUtil.areStacksEqual(seeds, mapBlockPosToSeedSlot(pos).get(owner));
  }

  @Override
  public int isLowOnSaplings(@Nonnull BlockPos pos) {
    return 90 * (FarmConfig.farmSaplingReserveAmount.get() - getSeedTypeInSuppliesFor(pos).getCount()) / FarmConfig.farmSaplingReserveAmount.get();
  }

  @Override
  public boolean isSlotLocked(@Nonnull BlockPos pos) {
    return owner.isSlotLocked(mapBlockPosToSeedSlot(pos));
  }

  // Tools

  @Override
  public boolean hasTool(@Nonnull IFarmingTool tool) {
    return Prep.isValid(getTool(tool));
  }

  @Override
  public int getLootingValue(@Nonnull IFarmingTool tool) {
    ItemStack stack = getTool(tool);
    return Math.max(EnchantmentHelper.getEnchantmentLevel(Enchantments.LOOTING, stack), EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack));
  }

  @Override
  @Nonnull
  public ItemStack getTool(@Nonnull IFarmingTool tool) {
    FarmSlots slot = owner.getSlotForTool(tool);
    if (slot != null) {
      ItemStack stack = slot.get(owner);
      if (TicProxy.isBroken(stack) || FarmingTool.isDryRfTool(stack)) {
        if (!joeInUse) {
          handleExtraItem(stack, null);
        }
      } else if (tool.itemMatches(stack)) {
        switch (tool(tool)) {
        case AXE:
          owner.removeNotification(FarmNotification.NO_AXE);
          break;
        case HOE:
          owner.removeNotification(FarmNotification.NO_HOE);
          break;
        case TREETAP:
          owner.removeNotification(FarmNotification.NO_TREETAP);
          break;
        default:
          break;
        }
        return stack;
      }
    }
    return Prep.getEmpty();
  }

  @Override
  public void handleExtraItem(@Nonnull ItemStack stack, @Nullable BlockPos drop) {
    InsertCallback insertCallback = new InsertCallback(stack);
    if (drop != null && Prep.isValid(stack)) {
      insertCallback.apply(mapBlockPosToSeedSlot(drop));
    }
    if (Prep.isValid(stack)) {
      FarmSlots.SEEDS.apply(insertCallback);
    }
    if (Prep.isValid(stack)) {
      FarmSlots.OUTPUTS.apply(insertCallback);
    }
    if (drop != null && Prep.isValid(stack)) {
      if (FarmConfig.useOutputQueue.get()) {
        owner.enQueueOverflow(stack.copy());
      } else {
        Block.spawnAsEntity(getWorld(), drop, stack.copy());
      }
      stack.setCount(0);
    }
  }

  // Item use

  private boolean joeInUse = false;
  private FarmSlots joeHasTool = null;

  @Override
  public @Nonnull FakePlayerEIO startUsingItem(@Nonnull ItemStack stack) {
    cleanJoe();
    joeInUse = true;
    farmerJoe.setHeldItem(EnumHand.MAIN_HAND, stack);
    return farmerJoe;
  }

  private void cleanJoe() {
    if (joeInUse) {
      removeJoesTool();
      endUsingItem(false).apply(new Callback<ItemStack>() {
        @Override
        public void apply(@Nonnull ItemStack istack) {
          handleExtraItem(istack, getLocation());
        }
      });
    }
  }

  private void removeJoesTool() {
    if (joeHasTool != null) {
      joeHasTool.set(owner, farmerJoe.getHeldItem(EnumHand.MAIN_HAND));
      farmerJoe.setHeldItem(EnumHand.MAIN_HAND, Prep.getEmpty());
      joeHasTool = null;
    }
  }

  @Override
  public @Nonnull FakePlayerEIO startUsingItem(@Nonnull IFarmingTool tool) {
    cleanJoe();
    joeInUse = true;
    ItemStack toolStack = getTool(tool);
    for (FarmSlots slot : FarmSlots.TOOLS) {
      if (slot.get(owner) == toolStack) { // sic! identity check
        joeHasTool = slot;
      }
    }
    farmerJoe.setHeldItem(EnumHand.MAIN_HAND, toolStack);
    return farmerJoe;
  }

  @Override
  @Nonnull
  public NNList<ItemStack> endUsingItem(boolean trashHandItem) {
    NNList<ItemStack> result = new NNList<>();
    for (int i = 0; i < farmerJoe.inventory.getSizeInventory(); i++) {
      ItemStack stack = farmerJoe.inventory.removeStackFromSlot(i);
      if (Prep.isValid(stack)) {
        result.add(stack);
      }
    }
    joeInUse = false;
    return result;
  }

  @Override
  @Nonnull
  public NNList<ItemStack> endUsingItem(@Nonnull IFarmingTool tool) {
    removeJoesTool();
    final NNList<ItemStack> items = endUsingItem(false);
    ItemStack toolStack = getTool(tool);
    if (TicProxy.isBroken(toolStack) || FarmingTool.isDryRfTool(toolStack)) {
      handleExtraItem(toolStack, null);
    }
    return items;
  }

  // Result Handling

  @Override
  public void handleExtraItems(@Nonnull NonNullList<ItemStack> items, @Nullable BlockPos pos) {
    NNList.wrap(items).apply(new Callback<ItemStack>() {
      @Override
      public void apply(@Nonnull ItemStack stack) {
        handleExtraItem(stack, NullHelper.first(pos, getLocation()));
      }
    });
  }

  // Actions

  @Override
  public boolean checkAction(@Nonnull FarmingAction action, @Nonnull IFarmingTool tool) {
    if (tool != FarmingTool.HAND && !hasTool(tool)) {
      switch (tool(tool)) {
      case AXE:
        setNotification(FarmNotification.NO_AXE);
        break;
      case HOE:
        setNotification(FarmNotification.NO_HOE);
        break;
      case SHEARS:
        setNotification(FarmNotification.NO_SHEARS);
        break;
      case TREETAP:
        setNotification(FarmNotification.NO_TREETAP);
        break;
      default:
        break;
      }
      return false;
    }
    return owner.getEnergyStored() >= getEnergyUse(action, tool);
  }

  @Override
  public void registerAction(@Nonnull FarmingAction action, @Nonnull IFarmingTool tool) {
    owner.usePower(getEnergyUse(action, tool));
  }

  private int getEnergyUse(@Nonnull FarmingAction action, @Nonnull IFarmingTool tool) {
    switch (action) {
    case FERTILIZE:
      return FarmConfig.farmBonemealEnergyUseSuccess.get();
    case HARVEST:
      return tool == FarmingTool.AXE ? FarmConfig.farmHarvestAxeEnergyUse.get() : FarmConfig.farmHarvestEnergyUse.get();
    case PLANT:
      return FarmConfig.farmPlantEnergyUse.get();
    case TILL:
      return FarmConfig.farmTillEnergyUse.get();
    default:
      return 0;
    }
  }

  @Override
  public void registerAction(@Nonnull FarmingAction action, @Nonnull IFarmingTool tool, @Nonnull IBlockState state, @Nonnull BlockPos pos) {
    registerAction(action, tool);
    ItemStack toolStack = getTool(tool);
    if (Prep.isValid(toolStack) && getWorld().rand.nextFloat() <= FarmConfig.farmToolDamageChance.get()) {
      FarmSlots toolSlot = null;
      for (FarmSlots slot : FarmSlots.TOOLS) {
        if (slot.get(owner) == toolStack) { // sic! identity check
          toolSlot = slot;
        }
      }
      boolean damageHandItem = false;
      ItemStack restoreJoe = Prep.getEmpty();
      if (joeInUse) {
        if (toolStack == farmerJoe.getHeldItem(EnumHand.MAIN_HAND)) {
          // good case
          damageHandItem = true;
        } else {
          // bad case
          restoreJoe = farmerJoe.getHeldItem(EnumHand.MAIN_HAND);
          farmerJoe.setHeldItem(EnumHand.MAIN_HAND, Prep.getEmpty());
        }
      } else if (Prep.isValid(farmerJoe.getHeldItem(EnumHand.MAIN_HAND))) {
        handleExtraItem(farmerJoe.getHeldItem(EnumHand.MAIN_HAND), null);
        farmerJoe.setHeldItem(EnumHand.MAIN_HAND, Prep.getEmpty());
      }
      if (!damageHandItem) {
        farmerJoe.setHeldItem(EnumHand.MAIN_HAND, toolStack);
      }

      boolean canDamage = FarmingTool.canDamage(toolStack);
      switch (tool(tool)) {
      case AXE:
        toolStack.getItem().onBlockDestroyed(toolStack, getWorld(), state, pos, farmerJoe);
        break;
      case HAND:
        break;
      case HOE:
        int origDamage = toolStack.getItemDamage();
        toolStack.getItem().onItemUse(farmerJoe, getWorld(), pos, EnumHand.MAIN_HAND, EnumFacing.UP, 0.5f, 0.5f, 0.5f);
        ItemStack newToolStack = farmerJoe.getHeldItem(EnumHand.MAIN_HAND);
        if (origDamage == newToolStack.getItemDamage() && canDamage) {
          newToolStack.damageItem(1, farmerJoe);
        }
        if (newToolStack != toolStack) {
          if (damageHandItem && toolSlot != null) {
            toolSlot.set(owner, newToolStack);
            toolStack = getTool(tool);
            farmerJoe.setHeldItem(EnumHand.MAIN_HAND, toolStack);
          } else {
            toolStack = newToolStack;
          }
        }
        break;
      case NONE:
        break;
      case SHEARS:
      case TREETAP:
      default:
        if (canDamage) {
          toolStack.damageItem(1, farmerJoe);
        }
        break;
      }

      if (damageHandItem) {
        owner.markDirty();
        return;
      }
      farmerJoe.setHeldItem(EnumHand.MAIN_HAND, restoreJoe);
      if (toolSlot != null) {
        toolSlot.set(owner, toolStack);
        owner.markDirty();
      }
    }
  }

  // Tool methods

  @Override
  public boolean tillBlock(@Nonnull BlockPos pos) {
    BlockPos dirtLoc = pos.down();
    IBlockState dirtBlockState = getBlockState(pos);
    Block dirtBlock = dirtBlockState.getBlock();
    if (dirtBlock == Blocks.FARMLAND) {
      return true;
    } else {
      if (!checkAction(FarmingAction.TILL, FarmingTool.HOE)) {
        return false;
      }
      ItemStack toolStack = getTool(FarmingTool.HOE);

      toolStack = toolStack.copy();

      ItemStack heldItem = farmerJoe.getHeldItem(EnumHand.MAIN_HAND);
      farmerJoe.setHeldItem(EnumHand.MAIN_HAND, toolStack);
      EnumActionResult itemUse = toolStack.getItem().onItemUse(farmerJoe, getWorld(), dirtLoc, EnumHand.MAIN_HAND, EnumFacing.UP, 0.5f, 0.5f, 0.5f);
      farmerJoe.setHeldItem(EnumHand.MAIN_HAND, heldItem);

      if (itemUse != EnumActionResult.SUCCESS) {
        return false;
      }

      getWorld().playSound(dirtLoc.getX() + 0.5F, dirtLoc.getY() + 0.5F, dirtLoc.getZ() + 0.5F, SoundEvents.BLOCK_GRASS_STEP, SoundCategory.BLOCKS,
          (Blocks.FARMLAND.getSoundType().getVolume() + 1.0F) / 2.0F, Blocks.FARMLAND.getSoundType().getPitch() * 0.8F, false);

      PacketHandler.sendToAllAround(new PacketFarmAction(pos), owner);

      registerAction(FarmingAction.TILL, FarmingTool.HOE, dirtBlockState, dirtLoc);
      return true;
    }
  }

  private @Nonnull FarmingTool tool(@Nonnull IFarmingTool tool) {
    return (tool instanceof FarmingTool) ? (FarmingTool) tool : FarmingTool.NONE;
  }
}
