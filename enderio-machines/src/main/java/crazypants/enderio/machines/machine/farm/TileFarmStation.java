package crazypants.enderio.machines.machine.farm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.fluid.SmartTank;
import com.enderio.core.common.fluid.SmartTankFluidHandler;
import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.util.blockiterators.PlanarBlockIterator;
import com.enderio.core.common.util.blockiterators.PlanarBlockIterator.Orientation;
import com.enderio.core.common.vecmath.Vector4f;
import com.google.common.collect.Iterators;

import crazypants.enderio.api.farm.FarmNotification;
import crazypants.enderio.api.farm.FarmingAction;
import crazypants.enderio.api.farm.IFarmer;
import crazypants.enderio.api.farm.IFarmingTool;
import crazypants.enderio.api.farm.IFertilizer;
import crazypants.enderio.api.farm.IFertilizerResult;
import crazypants.enderio.api.farm.IHarvestResult;
import crazypants.enderio.base.capacitor.DefaultCapacitorData;
import crazypants.enderio.base.farming.FarmingTool;
import crazypants.enderio.base.farming.fertilizer.Fertilizer;
import crazypants.enderio.base.farming.registry.Commune;
import crazypants.enderio.base.fluid.SmartTankFluidMachineHandler;
import crazypants.enderio.base.integration.tic.TicProxy;
import crazypants.enderio.base.machine.baselegacy.AbstractPoweredTaskEntity;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.base.machine.interfaces.INotifier;
import crazypants.enderio.base.machine.interfaces.IPoweredTask;
import crazypants.enderio.base.machine.task.ContinuousTask;
import crazypants.enderio.base.network.PacketSpawnParticles;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.render.ranged.IRanged;
import crazypants.enderio.base.render.ranged.RangeParticle;
import crazypants.enderio.machines.capacitor.CapacitorKey;
import crazypants.enderio.machines.config.config.FarmConfig;
import crazypants.enderio.machines.network.PacketHandler;
import crazypants.enderio.util.Prep;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.util.NBTAction;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.BlockPosContext;

import static crazypants.enderio.machines.capacitor.CapacitorKey.FARM_BASE_SIZE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.FARM_BONUS_SIZE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.FARM_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.FARM_POWER_INTAKE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.FARM_POWER_USE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.FARM_STACK_LIMIT;
import static net.minecraft.util.EnumParticleTypes.PORTAL;
import static net.minecraft.util.EnumParticleTypes.WATER_DROP;
import static net.minecraft.util.EnumParticleTypes.WATER_SPLASH;

@Storable
public class TileFarmStation extends AbstractPoweredTaskEntity implements IPaintable.IPaintableTileEntity, IRanged, INotifier, ITankAccess.IExtendedTankAccess {

  public static final int NUM_TOOL_SLOTS = 3;

  public static final int minToolSlot = 0;
  public static final int maxToolSlot = -1 + NUM_TOOL_SLOTS;

  public static final int NUM_FERTILIZER_SLOTS = 2;

  public static final int minFirtSlot = maxToolSlot + 1;
  public static final int maxFirtSlot = maxToolSlot + NUM_FERTILIZER_SLOTS;

  public static final int NUM_SUPPLY_SLOTS = 4;

  public static final int minSupSlot = maxFirtSlot + 1;
  public static final int maxSupSlot = maxFirtSlot + NUM_SUPPLY_SLOTS;

  @Store
  private int lockedSlots = 0x00;

  @Store(value = { NBTAction.SAVE, NBTAction.ITEM })
  private @Nonnull NNList<ItemStack> overflowQueue = new NNList<>();

  private final @Nonnull Set<FarmNotification> notification = EnumSet.noneOf(FarmNotification.class);
  private boolean sendNotification = false;
  private boolean wasActive;

  public TileFarmStation() {
    super(new SlotDefinition(9, 6, 1), FARM_POWER_INTAKE, FARM_POWER_BUFFER, FARM_POWER_USE);
    ticket = new TicketFarmingStation(this);
    tank.setTileEntity(this);
    addICap(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, this::getSmartTankFluidHandler);
  }

  public int getFarmSize() {
    return (int) (FARM_BASE_SIZE.getFloat(getCapacitorData()) + FARM_BONUS_SIZE.getFloat(getCapacitorData()));
  }

  public void setSingleNotification(@Nonnull FarmNotification note) {
    setNotification(note);
    for (Iterator<FarmNotification> itr = notification.iterator(); itr.hasNext();) {
      if (itr.next() != note) {
        itr.remove();
        sendNotification = true;
      }
    }
  }

  public void setNotification(@Nonnull FarmNotification note) {
    if (!notification.contains(note)) {
      notification.add(note);
      sendNotification = true;
    }
  }

  public void removeNotification(FarmNotification note) {
    if (getNotification().remove(note)) {
      sendNotification = true;
    }
  }

  public void clearNotification(boolean all) {
    if (hasNotification()) {
      if (all) {
        getNotification().clear();
        sendNotification = true;
      } else {
        for (Iterator<FarmNotification> itr = notification.iterator(); itr.hasNext();) {
          if (itr.next().isAutoCleanup()) {
            itr.remove();
            sendNotification = true;
          }
        }
      }
    }
  }

  public boolean hasNotification() {
    return !getNotification().isEmpty();
  }

  private void sendNotification() {
    PacketHandler.sendToAllAround(new PacketUpdateNotification(this, notification), this);
    sendNotification = false;
  }

  @Override
  public boolean isMachineItemValidForSlot(int i, @Nonnull ItemStack stack) {
    if (Prep.isInvalid(stack)) {
      return false;
    }
    if (i <= maxToolSlot) {
      IFarmingTool toolType = FarmingTool.getToolType(stack);
      if (toolType != FarmingTool.NONE && !TicProxy.isBroken(stack) && !FarmingTool.isDryRfTool(stack)) {
        return getSlotForTool(toolType) == null;
      }
      return false;
    } else if (i <= maxFirtSlot) {
      return Fertilizer.isFertilizer(stack);
    } else if (i <= maxSupSlot) {
      // TODO: When switching to EnderInventory, make it so that the GUI allows putting things into empty locked slots
      return isSlotLocked(i) ? ItemUtil.areStacksEqual(stack, getStackInSlot(i)) : Commune.instance.canPlant(stack);
    } else {
      return false;
    }
  }

  private EnumMap<FarmingTool, FarmSlots> toolmapping = new EnumMap<>(FarmingTool.class);
  private boolean toolmappingInitialized = false;

  private void buildToolmapping() {
    if (!toolmappingInitialized || world.isRemote) {
      toolmapping.clear();
      toolmapping.put(FarmingTool.getToolType(getStackInSlot(0)), FarmSlots.TOOL1);
      toolmapping.put(FarmingTool.getToolType(getStackInSlot(1)), FarmSlots.TOOL2);
      toolmapping.put(FarmingTool.getToolType(getStackInSlot(2)), FarmSlots.TOOL3);
      toolmappingInitialized = true;
    }
  }

  @Override
  public void markDirty() {
    super.markDirty();
    toolmappingInitialized = false;
  }

  protected FarmSlots getSlotForTool(@Nonnull IFarmingTool tool) {
    buildToolmapping();
    return toolmapping.get(tool);
  }

  @Override
  public void doUpdate() {
    super.doUpdate();
    if (isActive() != wasActive) {
      wasActive = isActive();
      world.checkLightFor(EnumSkyBlock.BLOCK, pos);
    }
  }

  @Override
  protected void checkProgress(boolean redstoneChecksPassed) {
    if (shouldDoWorkThisTick(1 * 60 * 20)) {
      clearNotification(false);
    }
    if (redstoneChecksPassed) {
      if (getCapacitorData() == DefaultCapacitorData.NONE) {
        setSingleNotification(FarmNotification.NO_CAP);
      } else if (tryToUsePower()) {
        removeNotification(FarmNotification.NO_POWER);
        if (shouldDoWorkThisTick(10 - getEnergyStoredScaled(8))) {
          if (isOutputFull()) {
            setNotification(FarmNotification.OUTPUT_FULL);
          } else {
            removeNotification(FarmNotification.OUTPUT_FULL);
            doTick();
          }
          doBoost();
        }
      } else {
        setSingleNotification(FarmNotification.NO_POWER);
      }
    }
    if (sendNotification) {
      sendNotification();
    }
  }

  private IFarmer farmerCache;

  private @Nonnull IFarmer getFarmer() {
    return farmerCache != null ? farmerCache : (farmerCache = new FarmLogic(this));
  }

  protected @Nullable BlockPos findNextPos() {
    int i = 20;
    while (i-- > 0) {
      BlockPos farmingPos = getNextCoord();
      if (!farmingPos.equals(getPos()) && world.isBlockLoaded(farmingPos)) {
        if (PermissionAPI.hasPermission(getOwner().getAsGameProfile(), BlockFarmStation.permissionFarming,
            new BlockPosContext(getFarmer().getFakePlayer(), farmingPos, world.getBlockState(farmingPos), null))) {
          return farmingPos;
        }
      }
    }
    return null;
  }

  protected void doTick() {
    BlockPos farmingPos = findNextPos();
    if (farmingPos == null) {
      return;
    }
    IBlockState bs = world.getBlockState(farmingPos);

    if (isOpen(farmingPos, bs)) {
      switch (Commune.instance.tryPrepareBlock(getFarmer(), farmingPos, bs)) {
      case ACTION:
        PacketHandler.sendToAllAround(new PacketFarmAction(farmingPos), this);
      case CLAIM:
        return;
      case NEXT:
      default:
        break;
      }
    }

    if (isOpen(farmingPos, bs) || !executeHarvest(farmingPos, bs)) {
      executeBonemeal(farmingPos, bs);
    }
  }

  private void executeBonemeal(@Nonnull BlockPos farmingPos, @Nonnull IBlockState bs) {
    final IFarmer farmer = getFarmer();
    if (hasBonemeal() && bonemealCooldown-- <= 0 && random.nextFloat() <= FarmConfig.farmBonemealChance.get()
        && farmer.checkAction(FarmingAction.FERTILIZE, FarmingTool.HAND)) {
      final ItemStack fertStack = getStackInSlot(minFirtSlot);
      IFertilizer fertilizer = Fertilizer.getInstance(fertStack);
      boolean doApply;

      if (fertilizer.applyOnPlant() && fertilizer.applyOnAir()) {
        doApply = !isOpen(farmingPos, bs) || world.isAirBlock(farmingPos);
      } else if (fertilizer.applyOnPlant()) {
        doApply = !isOpen(farmingPos, bs);
      } else if (fertilizer.applyOnAir()) {
        doApply = world.isAirBlock(farmingPos);
      } else {
        doApply = true;
      }

      if (doApply) {
        FakePlayer farmerJoe = farmer.startUsingItem(Prep.getEmpty());
        final IFertilizerResult result = fertilizer.apply(fertStack, farmerJoe, world, farmingPos);
        if (result.wasApplied()) {
          setInventorySlotContents(minFirtSlot, result.getStack());
          PacketHandler.sendToAllAround(new PacketFarmAction(farmingPos), this);
          bonemealCooldown = FarmConfig.farmBonemealDelaySuccess.get();
          farmer.registerAction(FarmingAction.FERTILIZE, FarmingTool.HAND);
        } else {
          usePower(FarmConfig.farmBonemealEnergyUseFail.get());
          bonemealCooldown = FarmConfig.farmBonemealDelayFail.get();
        }
        farmer.handleExtraItems(farmer.endUsingItem(false), farmingPos);
      }
    }
  }

  private boolean isOpen(@Nonnull BlockPos farmingPos, @Nonnull IBlockState bs) {
    return world.isAirBlock(farmingPos) || bs.getBlock().isReplaceable(world, farmingPos);
  }

  private boolean executeHarvest(@Nonnull BlockPos farmingPos, @Nonnull IBlockState bs) {
    IHarvestResult harvest = Commune.instance.harvestBlock(getFarmer(), farmingPos, bs);
    if (harvest != null && (!harvest.getHarvestedBlocks().isEmpty() || !harvest.getDrops().isEmpty())) {
      if (!harvest.getHarvestedBlocks().isEmpty()) {
        PacketHandler.sendToAllAround(new PacketFarmAction(harvest.getHarvestedBlocks()), this);
      }
      NNList.wrap(harvest.getDrops()).apply(new Callback<Pair<BlockPos, ItemStack>>() {
        @Override
        public void apply(@Nonnull Pair<BlockPos, ItemStack> ei) {
          ItemStack stack = ei.getValue();
          if (stack != null && Prep.isValid(stack)) {
            getFarmer().handleExtraItem(stack, ei.getLeft());
          }
        }
      });
      return true;
    }
    return false;
  }

  private int bonemealCooldown = 4; // no need to persist this

  private boolean hasBonemeal() {
    for (FarmSlots slot : FarmSlots.FERTS) {
      if (Prep.isValid(slot.get(this))) {
        if (slot != FarmSlots.FERT1) {
          FarmSlots.FERT1.set(this, slot.get(this));
          slot.set(this, Prep.getEmpty());
        }
        return true;
      }
    }
    return false;
  }

  private boolean isOutputFull() {
    if (!overflowQueue.isEmpty()) {
      NNList<ItemStack> old = overflowQueue;
      overflowQueue = new NNList<>();
      getFarmer().handleExtraItems(old, pos);
    }
    if (FarmConfig.useOutputQueue.get()) {
      return !overflowQueue.isEmpty();
    }
    for (FarmSlots slot : FarmSlots.OUTPUTS) {
      ItemStack curStack = slot.get(this);
      if (Prep.isInvalid(curStack) || (!FarmConfig.farmStopOnNoOutputSlots.get() && curStack.getCount() < curStack.getMaxStackSize())) {
        return false;
      }
    }
    return true;
  }

  private PlanarBlockIterator blockIterator;

  private @Nonnull BlockPos getNextCoord() {
    if (blockIterator == null || !blockIterator.hasNext()) {
      blockIterator = new PlanarBlockIterator(getPos(), Orientation.HORIZONTAL, getFarmSize());
    }
    return blockIterator.next();
  }

  private final @Nonnull List<BlockPos> boostCoords = new ArrayList<>();

  private @Nonnull BlockPos getNextBoostCoord() {
    if (boostCoords.isEmpty()) {
      Iterators.addAll(boostCoords, new PlanarBlockIterator(getPos(), Orientation.HORIZONTAL, getFarmSize()));
      Collections.shuffle(boostCoords);
    }
    return boostCoords.isEmpty() ? pos : NullHelper.first(boostCoords.remove(boostCoords.size() - 1), pos);
  }

  private void doBoost() {
    ticket.prepare();
    if (FarmConfig.enableCarefulCare.get()) {
      if (FarmConfig.waterCarefulCare.get() == 0 || !hasTank() || !tank.isEmpty()) {
        // capKey base is an int, so to give it a usable range, we scale it down by a factor of 100
        float boost = CapacitorKey.FARM_BOOST.getFloat(getCapacitorData()) / 100f;
        while (boost > 0) {
          BlockPos boostPos = getNextBoostCoord();
          if ((boost >= 1 || random.nextFloat() < boost) && world.isBlockLoaded(boostPos)) {
            IBlockState blockState = world.getBlockState(boostPos);
            Block block = blockState.getBlock();

            if (block.getTickRandomly()) {
              block.randomTick(world, boostPos, blockState, world.rand);
              tank.drainInternal(FarmConfig.waterCarefulCare.get(), true);
              // much too noisy:
              // PacketHandler.sendToAllAround(new PacketFarmAction(Collections.singletonList(boostPos)), this);
            }
          }
          boost--;
        }
        if (!tank.isEmpty()) {
          removeNotification(FarmNotification.NO_WATER);
        }
      } else {
        setNotification(FarmNotification.NO_WATER);
      }
    }
  }

  public void toggleLockedState(int slot) {
    if (world.isRemote) {
      PacketHandler.sendToServer(new PacketFarmLockedSlot(this, slot));
    }
    setSlotLocked(slot, !isSlotLocked(slot));
  }

  public boolean isSlotLocked(FarmSlots slot) {
    return (lockedSlots & slot.getBitmask()) != 0;
  }

  public boolean isSlotLocked(int slot) {
    return (lockedSlots & (1 << (slot - minSupSlot))) != 0;
  }

  private void setSlotLocked(int slot, boolean value) {
    if (value) {
      lockedSlots = lockedSlots | (1 << (slot - minSupSlot));
    } else {
      lockedSlots = lockedSlots & ~(1 << (slot - minSupSlot));
    }
  }

  @Override
  public @Nonnull String getMachineName() {
    return MachineRecipeRegistry.FARM;
  }

  @Override
  public float getProgress() {
    return 0.5f;
  }

  @Override
  public void onCapacitorDataChange() {
    super.onCapacitorDataChange();
    currentTask = createTask(null, 0L);
    wateringBounds = new BoundingBox(getPos().down()).expand(getRange(), 0, getRange());
  }

  @Override
  protected IPoweredTask createTask(@Nullable IMachineRecipe nextRecipe, long nextSeed) {
    return new ContinuousTask(getPowerUsePerTick());
  }

  @Override
  public int getInventoryStackLimit(int slot) {
    if (slot >= minToolSlot && slot <= maxToolSlot) {
      return 1;
    }
    if (slot >= minSupSlot && slot <= maxSupSlot) {
      return getCapacitorData() == DefaultCapacitorData.NONE ? 0 : Math.min(FARM_STACK_LIMIT.get(getCapacitorData()), 64);
    }
    return 64;
  }

  @Override
  public int getInventoryStackLimit() {
    // We return the (lowered) input slot limit here, so others who insert into us
    // will behave nicely.
    return getInventoryStackLimit(minSupSlot);
  }

  @Override
  public boolean shouldRenderInPass(int pass) {
    return pass == 1;
  }

  // RANGE

  private boolean showingRange;

  @Override
  @SideOnly(Side.CLIENT)
  public boolean isShowingRange() {
    return showingRange;
  }

  private final static Vector4f color = new Vector4f(145f / 255f, 82f / 255f, 21f / 255f, .4f);

  @SideOnly(Side.CLIENT)
  public void setShowRange(boolean showRange) {
    if (showingRange == showRange) {
      return;
    }
    showingRange = showRange;
    if (showingRange) {
      Minecraft.getMinecraft().effectRenderer.addEffect(new RangeParticle<TileFarmStation>(this, color));
    }
  }

  @Override
  public @Nonnull BoundingBox getBounds() {
    return new BoundingBox(getPos()).expand(getRange(), 0, getRange());
  }

  public float getRange() {
    return getFarmSize();
  }

  // RANGE END

  // WATER TICKET

  private BoundingBox wateringBounds;
  private final @Nonnull TicketFarmingStation ticket;
  @Store
  protected final @Nonnull SmartTank tank = new SmartTank(FluidRegistry.WATER, FarmConfig.waterTankSize.get());

  public boolean hasTank() {
    return FarmConfig.waterTankSize.get() > 0;
  }

  public boolean canWater(@Nonnull Vec3d toMatch) {
    if (redstoneCheckPassed && wateringBounds != null && hasPower() && wateringBounds.contains(toMatch)) {
      if (FarmConfig.waterPerFarmland.get() == 0 || !hasTank() || !tank.isEmpty()) {
        tank.drainInternal(FarmConfig.waterPerFarmland.get(), true);
        if (FarmConfig.waterFarmlandParticles.get()) {
          PacketSpawnParticles.create(world, new BlockPos(toMatch).up(2), WATER_DROP, WATER_SPLASH, PORTAL);
        }
        if (!tank.isEmpty()) {
          removeNotification(FarmNotification.NO_WATER);
        }
        return true;
      } else {
        setNotification(FarmNotification.NO_WATER);
      }
    }
    return false;
  }

  @Override
  public void invalidate() {
    super.invalidate();
    ticket.invalidate();
  }

  // WATER TICKET END

  @Override
  protected int usePower(int wantToUse) {
    return super.usePower(wantToUse);
  }

  // TANK

  private SmartTankFluidHandler smartTankFluidHandler;

  protected IFluidHandler getSmartTankFluidHandler(EnumFacing facingIn) {
    if (smartTankFluidHandler == null) {
      smartTankFluidHandler = new SmartTankFluidMachineHandler(this, tank);
    }
    return smartTankFluidHandler.get(facingIn);
  }

  @Override
  public @Nonnull Set<FarmNotification> getNotification() {
    return notification;
  };

  public void enQueueOverflow(@Nonnull ItemStack stack) {
    overflowQueue.add(stack);
  }

  @Override
  @Nullable
  public FluidTank getInputTank(FluidStack forFluidType) {
    if (tank.canFill(forFluidType)) {
      return tank;
    }
    return null;
  }

  @Override
  @Nonnull
  public FluidTank[] getOutputTanks() {
    return new FluidTank[0];
  }

  @Override
  public void setTanksDirty() {
    markDirty();
  }

  @Override
  @Nonnull
  public List<ITankData> getTankDisplayData() {
    return Collections.<ITankData> singletonList(new ITankData() {

      @Override
      @Nonnull
      public EnumTankType getTankType() {
        return EnumTankType.INPUT;
      }

      @Override
      @Nullable
      public FluidStack getContent() {
        return tank.getFluid();
      }

      @Override
      public int getCapacity() {
        return tank.getCapacity();
      }
    });
  }

  // TANK END

}
