package crazypants.enderio.conduit.refinedstorage.conduit;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.BlockEnder;
import com.enderio.core.common.util.EndlessNNIterator;
import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.INetworkNodeVisitor;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.storage.IStorageProvider;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.api.util.IComparer;

import crazypants.enderio.base.capability.ItemTools;
import crazypants.enderio.base.conduit.item.FunctionUpgrade;
import crazypants.enderio.base.conduit.item.ItemFunctionUpgrade;
import crazypants.enderio.base.filter.IFilter;
import crazypants.enderio.base.filter.fluid.IFluidFilter;
import crazypants.enderio.base.filter.gui.DamageMode;
import crazypants.enderio.base.filter.item.IItemFilter;
import crazypants.enderio.base.filter.item.ItemFilter;
import crazypants.enderio.conduit.refinedstorage.RSHelper;
import crazypants.enderio.conduit.refinedstorage.init.ConduitRefinedStorageObject;
import crazypants.enderio.util.FuncUtil;
import crazypants.enderio.util.MathUtil;
import crazypants.enderio.util.Prep;
import crazypants.enderio.util.SidedInt;
import crazypants.enderio.util.SidedNNObject;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class ConduitRefinedStorageNode implements INetworkNode, INetworkNodeVisitor {

  public static final @Nonnull String ID = "rs_conduit";

  @Nullable
  protected INetwork rsNetwork;
  protected final @Nonnull World world;
  protected final @Nonnull BlockPos pos;
  protected final @Nonnull IRefinedStorageConduit con;

  private final @Nonnull NNIterator<EnumFacing> dirsToCheck = new EndlessNNIterator<>(NNList.FACING);

  /**
   * The slot ID in the connected inventory we're currently are importing from (or have imported from last tick).
   */
  private final @Nonnull SidedInt currentImportSlot = new SidedInt();

  private int importFilterSlot;
  /**
   * The slot ID in the export filter we are currently exporting for (or have exported for last tick).
   */
  private final @Nonnull SidedInt exportFilterSlot = new SidedInt();

  private final @Nonnull SidedNNObject<UUID> craftingTask = new SidedNNObject<>(UUID.randomUUID());

  public ConduitRefinedStorageNode(@Nonnull IRefinedStorageConduit con) {
    this.con = con;
    this.world = con.getBundle().getBundleworld();
    this.pos = con.getBundle().getLocation();
  }

  @Override
  public int getEnergyUsage() {
    return 0;
  }

  @Nonnull
  @Override
  public ItemStack getItemStack() {
    return new ItemStack(ConduitRefinedStorageObject.item_refined_storage_conduit.getItemNN(), 1);
  }

  @Override
  public void onConnected(INetwork network) {
    rsNetwork = network;
  }

  @Override
  public void onDisconnected(INetwork network) {
    rsNetwork = null;
  }

  @Override
  public boolean canUpdate() {
    return con.hasExternalConnections();
  }

  @Nullable
  @Override
  public INetwork getNetwork() {
    return rsNetwork;
  }

  @Override
  public void update() {
    if (canUpdate()) {
      EnumFacing dir = dirsToCheck.next();
      if (con.isActiveExternalConnection(dir)) {
        IFilter outputFilter = con.getOutputFilter(dir);
        IFilter inputFilter = con.getInputFilter(dir);
        if (outputFilter != null || inputFilter != null) {
          TileEntity te = BlockEnder.getAnyTileEntitySafe(world, pos.offset(dir));
          if (te != null && !(te instanceof IStorageProvider)) {
            if (outputFilter instanceof IItemFilter || inputFilter instanceof IItemFilter) {
              updateDirItems(dir, outputFilter, inputFilter, te);
            }
            if (outputFilter instanceof IFluidFilter || inputFilter instanceof IFluidFilter) {
              updateDirFluids(dir, outputFilter, inputFilter, te);
            }
          }
        }
      }
    }
  }

  private void updateDirFluids(@Nonnull EnumFacing dir, IFilter outputFilter, IFilter inputFilter, @Nonnull TileEntity te) {
    IFluidHandler handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir.getOpposite());

    if (handler != null) {

      // Export
      if (outputFilter instanceof IFluidFilter) {

        IFluidFilter exportFilter = (IFluidFilter) outputFilter;

        FluidStack stack = null;

        if (!exportFilter.isEmpty()) {
          do {
            stack = exportFilter
                .getFluidStackAt(exportFilterSlot.get(dir) >= exportFilter.getSlotCount() ? exportFilterSlot.set(dir, 0) : exportFilterSlot.get(dir));

            if (stack == null) {
              exportFilterSlot.set(dir, exportFilterSlot.get(dir) + 1);
            }
          } while (stack == null);
        }

        ItemStack upgrade = con.getUpgradeStack(dir.ordinal());
        FunctionUpgrade up = null;

        if (!upgrade.isEmpty()) {
          up = ((ItemFunctionUpgrade) upgrade.getItem()).getFunctionUpgrade();
        }

        if (stack != null) {
          int toExtract = Fluid.BUCKET_VOLUME;

          int compare = IComparer.COMPARE_DAMAGE;
          FluidStack stackInStorage = rsNetwork.getFluidStorageCache().getList().get(stack, compare);

          if (stackInStorage != null) {
            toExtract = Math.min(toExtract, stackInStorage.amount);

            FluidStack took = rsNetwork.extractFluid(stack, toExtract, compare, Action.SIMULATE);

            if (took != null) {
              int filled = handler.fill(took, false);

              if (filled > 0) {
                took = rsNetwork.extractFluid(stack, filled, compare, Action.PERFORM);

                handler.fill(took, true);
                exportFilterSlot.set(dir, exportFilterSlot.get(dir) + 1);
                return;
              }
            }
          } else if (up != null && isCraftingUpgrade(up)) {
            rsNetwork.getCraftingManager().request(stack, toExtract);
          }
        }
      }

      // Importing
      if (inputFilter instanceof IFluidFilter) {

        IFluidFilter importFilter = (IFluidFilter) inputFilter;

        boolean all = importFilter.isEmpty();

        FluidStack toDrain = handler.drain(Fluid.BUCKET_VOLUME, false);

        FluidStack stack = null;

        if (!all) {
          do {
            stack = importFilter.getFluidStackAt(importFilterSlot >= importFilter.getSlotCount() ? importFilterSlot = 0 : importFilterSlot);

            if (stack == null) {
              importFilterSlot++;
            }
          } while (stack == null);
        }

        if (all || (stack != null && toDrain != null && stack.isFluidEqual(toDrain))) {

          if (toDrain != null) {
            FluidStack remainder = rsNetwork.insertFluidTracked(toDrain, toDrain.amount);
            if (remainder != null) {
              toDrain.amount -= remainder.amount;
            }

            handler.drain(toDrain, true);
            importFilterSlot++;
            return;
          }
        }
      }
    }
  }

  private int getInsertLimit(@Nonnull IItemFilter filter, @Nonnull IItemHandler inventory, @Nonnull ItemStack item, int limit) {
    if (filter.isLimited()) {
      final int count = filter.getMaxCountThatPassesFilter(inventory, item);
      if (count <= 0) {
        return 0;
      } else {
        final int maxInsert = ItemTools.getInsertLimit(inventory, item, count);
        if (maxInsert <= 0) {
          return 0;
        } else if (maxInsert < item.getCount()) {
          return Math.min(limit, maxInsert);
        }
      }
    } else if (!filter.doesItemPassFilter(inventory, item)) {
      return 0;
    }
    return Math.min(limit, item.getCount());
  }

  // note: existing needs to be the slot from the inventory with its real count
  private int getExtractLimit(@Nonnull IItemFilter filter, @Nonnull IItemHandler inventory, @Nonnull ItemStack existing, int limit) {
    if (filter.isLimited()) {
      final int count = filter.getMaxCountThatPassesFilter(inventory, existing);
      if (count <= 0) {
        return 0;
      } else if (count < Integer.MAX_VALUE) {
        return Math.max(0, Math.min(limit, existing.getCount() - count));
      }
    } else if (!filter.doesItemPassFilter(inventory, existing)) {
      return 0; // skip slot
    }
    return Math.min(limit, existing.getCount());
  }

  private void updateDirItems(@Nonnull EnumFacing dir, IFilter outputFilter, IFilter inputFilter, @Nonnull TileEntity te) {
    INetwork network = rsNetwork;
    IItemHandler handler = ItemTools.getExternalInventory(te, dir.getOpposite());

    if (network != null && handler != null && handler.getSlots() > 0) {

      // Exporting
      if (outputFilter instanceof IItemFilter) {
        IItemFilter exportFilter = (IItemFilter) outputFilter;
        if (!exportFilter.isEmpty()) {
          ItemStack prototype = exportFilter
              .getInventorySlotContents(exportFilterSlot.set(dir, MathUtil.cycle(exportFilterSlot.get(dir), 0, exportFilter.getSlotCount() - 1)));
          int compare = IComparer.COMPARE_DAMAGE;
          if (exportFilter instanceof ItemFilter) {
            // Note: damage mode cannot be handled by RS, so ask it to export ignoring damage and let our filter handle it. Yes, this most likely will lead to
            // stalled exports, but that's better than wrong ones, isn't it?
            boolean matchMeta = ((ItemFilter) exportFilter).isMatchMeta() && ((ItemFilter) exportFilter).getDamageMode() == DamageMode.DISABLED;
            boolean matchNBT = ((ItemFilter) exportFilter).isMatchNBT();
            compare = (matchMeta ? IComparer.COMPARE_DAMAGE : 0) | (matchNBT ? IComparer.COMPARE_NBT : 0);
          }
          if (!prototype.isEmpty()) {
            ItemStack upgrade = con.getUpgradeStack(dir.ordinal());
            int itemsPerTick = FunctionUpgrade.getMaximumExtracted(upgrade);
            ItemStack available = network.extractItem(prototype, Math.min(prototype.getMaxStackSize(), itemsPerTick), compare, Action.SIMULATE);
            if (available == null || Prep.isInvalid(available)) {
              if (isCraftingUpgrade(ItemFunctionUpgrade.getFunctionUpgrade(upgrade))) {
                available = ItemHandlerHelper.copyStackWithSize(prototype, getInsertLimit(exportFilter, handler, prototype, Integer.MAX_VALUE));
                int insertable = available.getCount() - ItemTools.insertItemStacked(handler, available, true).getCount();
                if (insertable > 0 && network.getCraftingManager().getTask(craftingTask.get(dir)) == null) {
                  craftingTask.set(dir, FuncUtil.runIf(network.getCraftingManager().request(prototype, insertable), ICraftingTask::getId));
                }
              }
            } else {
              available = ItemHandlerHelper.copyStackWithSize(available, getInsertLimit(exportFilter, handler, available, itemsPerTick));
              if (Prep.isValid(available)) {
                ItemStack remains = ItemTools.insertItemStacked(handler, available, true);
                if (remains.getCount() < available.getCount()) { // meaning something was inserted
                  available = network.extractItem(prototype, available.getCount() - remains.getCount(), compare, Action.PERFORM);
                  if (available != null && Prep.isValid(available)) {
                    remains = ItemTools.insertItemStacked(handler, available, false);
                    if (Prep.isValid(remains)) {
                      // darn, simulate lied to us. put the stuff back
                      remains = network.insertItemTracked(remains, remains.getCount());
                      if (remains != null && Prep.isValid(remains)) {
                        // oh come one, we just got that stuff from the network...
                        ItemUtil.spawnItemInWorldWithRandomMotion(getWorld(), remains, getPos());
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }

      // Importing
      if (inputFilter instanceof IItemFilter) {
        IItemFilter importFilter = (IItemFilter) inputFilter;
        currentImportSlot.set(dir, MathUtil.cycle(currentImportSlot.get(dir), 0, handler.getSlots() - 1));
        ItemStack existing = handler.getStackInSlot(currentImportSlot.get(dir));
        if (Prep.isValid(existing)) {
          int maxCountThatPassesFilter = getExtractLimit(importFilter, handler, existing,
              FunctionUpgrade.getMaximumExtracted(con.getUpgradeStack(dir.ordinal())));
          if (maxCountThatPassesFilter > 0) {
            ItemStack extractable = handler.extractItem(currentImportSlot.get(dir), maxCountThatPassesFilter, true);
            if (Prep.isValid(extractable)) {
              ItemStack remains = network.insertItem(extractable, maxCountThatPassesFilter, Action.SIMULATE);
              int insertable = maxCountThatPassesFilter - (remains == null ? 0 : remains.getCount());
              if (insertable > 0) {
                extractable = handler.extractItem(currentImportSlot.get(dir), insertable, false);
                remains = network.insertItemTracked(extractable, extractable.getCount());
                if (remains != null && Prep.isValid(remains)) {
                  // hey, RS, why you lie to us?
                  // not trying to put stuff back, this is not an issue in some random mod we won't be able to get hold of
                  ItemUtil.spawnItemInWorldWithRandomMotion(getWorld(), remains, getPos());
                }
              }
            }
          }
        }
      }

    }
  }

  private boolean isCraftingUpgrade(@Nullable FunctionUpgrade functionUpgrade) {
    return functionUpgrade == FunctionUpgrade.RS_CRAFTING_UPGRADE || functionUpgrade == FunctionUpgrade.RS_CRAFTING_SPEED_UPGRADE
        || functionUpgrade == FunctionUpgrade.RS_CRAFTING_SPEED_DOWNGRADE;
  }

  @Override
  public NBTTagCompound write(NBTTagCompound tag) {
    return tag;
  }

  @Override
  public @Nonnull BlockPos getPos() {
    return pos;
  }

  @Override
  public @Nonnull World getWorld() {
    return world;
  }

  @Override
  public void markDirty() {
    if (!world.isRemote) {
      RSHelper.API.getNetworkNodeManager(world).markForSaving();
    }
  }

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public boolean equals(Object right) {
    return RSHelper.API.isNetworkNodeEqual(this, right);
  }

  @Override
  public int hashCode() {
    return RSHelper.API.getNetworkNodeHashCode(this);
  }

  /**
   * Checks if the node is allowed to connect to something in the given direction.
   * <p>
   * That is the case if there's a another RS conduit that is not detached, or if there's a (potential) external connection that's not disabled.
   * <p>
   * Note that disabled conduit connections look the same as disabled external connections, so this case doesn't need to be handled explicitly.
   */
  private boolean canConduct(@Nonnull EnumFacing direction) {
    return con.containsConduitConnection(direction) || con.getConnectionMode(direction).isActive();
  }

  @Override
  public void visit(Operator operator) {
    NNList.FACING.apply(facing -> {
      if (canConduct(facing)) {
        operator.apply(world, pos.offset(facing), facing.getOpposite());
      }
    });
  }

  public void onConduitConnectionChange() {
    if (rsNetwork != null) {
      rsNetwork.getNodeGraph().rebuild();
    }
  }

}
