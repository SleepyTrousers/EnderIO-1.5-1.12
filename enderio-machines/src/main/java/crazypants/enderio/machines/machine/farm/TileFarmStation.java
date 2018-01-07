package crazypants.enderio.machines.machine.farm;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.NNList.Callback;
import com.enderio.core.common.util.blockiterators.PlanarBlockIterator;
import com.enderio.core.common.util.blockiterators.PlanarBlockIterator.Orientation;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.farming.FarmNotification;
import crazypants.enderio.base.farming.FarmingAction;
import crazypants.enderio.base.farming.FarmingTool;
import crazypants.enderio.base.farming.IFarmer;
import crazypants.enderio.base.farming.farmers.IHarvestResult;
import crazypants.enderio.base.farming.registry.Commune;
import crazypants.enderio.base.machine.baselegacy.AbstractPoweredTaskEntity;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.base.machine.fakeplayer.FakePlayerEIO;
import crazypants.enderio.base.machine.interfaces.IPoweredTask;
import crazypants.enderio.base.machine.task.ContinuousTask;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.render.ranged.IRanged;
import crazypants.enderio.base.render.ranged.RangeParticle;
import crazypants.enderio.machines.config.config.FarmConfig;
import crazypants.enderio.machines.network.PacketHandler;
import crazypants.enderio.util.Prep;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
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

@Storable
public class TileFarmStation extends AbstractPoweredTaskEntity implements IPaintable.IPaintableTileEntity, IRanged {

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

  private final @Nonnull Set<FarmNotification> notification = EnumSet.noneOf(FarmNotification.class);
  private boolean sendNotification = false;
  private boolean wasActive;

  public TileFarmStation() {
    super(new SlotDefinition(9, 6, 1), FARM_POWER_INTAKE, FARM_POWER_BUFFER, FARM_POWER_USE);
  }

  public int getFarmSize() {
    return (int) (FARM_BASE_SIZE.getFloat(getCapacitorData()) + FARM_BONUS_SIZE.getFloat(getCapacitorData()));
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

  public void clearNotification() {
    if (hasNotification()) {
      getNotification().clear();
      sendNotification = true;
    }
  }

  public boolean hasNotification() {
    return !getNotification().isEmpty();
  }

  private void sendNotification() {
    PacketHandler.sendToAllAround(new PacketUpdateNotification(this, getNotification()), this);
    sendNotification = false;
  }

  @Override
  public boolean isMachineItemValidForSlot(int i, @Nonnull ItemStack stack) {
    if (Prep.isInvalid(stack)) {
      return false;
    }
    if (i <= maxToolSlot) {
      FarmingTool toolType = FarmingTool.getToolType(stack);
      if (toolType != FarmingTool.NONE && !FarmingTool.isBrokenTinkerTool(stack) && !FarmingTool.isDryRfTool(stack)) {
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

  protected FarmSlots getSlotForTool(@Nonnull FarmingTool tool) {
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
  protected boolean checkProgress(boolean redstoneChecksPassed) {
    if (shouldDoWorkThisTick(1 * 60 * 20)) {
      clearNotification();
    }
    if (redstoneChecksPassed) {
      if (tryToUsePower()) {
        removeNotification(FarmNotification.NO_POWER);
        if (shouldDoWorkThisTick(2)) {
          if (isOutputFull()) {
            setNotification(FarmNotification.OUTPUT_FULL);
          } else {
            removeNotification(FarmNotification.OUTPUT_FULL);
            doTick();
          }
        }
      } else {
        setNotification(FarmNotification.NO_POWER);
      }
    }
    if (sendNotification) {
      sendNotification();
    }
    return false;
  }

  private IFarmer farmerCache;

  private @Nonnull IFarmer getFarmer() {
    return farmerCache != null ? farmerCache : (farmerCache = new FarmLogic(this));
  }

  protected void doTick() {
    IFarmer farmer = getFarmer();
    BlockPos farmingPos = null;
    IBlockState bs = null;
    int infiniteLoop = 20;
    while (farmingPos == null || bs == null || farmingPos.equals(getPos()) || !world.isBlockLoaded(farmingPos) || !PermissionAPI
        .hasPermission(getOwner().getAsGameProfile(), BlockFarmStation.permissionFarming, new BlockPosContext(farmer.getFakePlayer(), farmingPos, bs, null))) {
      if (infiniteLoop-- <= 0) {
        return;
      }
      farmingPos = getNextCoord();
      bs = world.getBlockState(farmingPos);
    }

    Block block = bs.getBlock();

    if (isOpen(farmingPos, block)) {
      Commune.instance.prepareBlock(farmer, farmingPos, block, bs);
      bs = world.getBlockState(farmingPos);
      block = bs.getBlock();
    }

    if (!isOpen(farmingPos, block)) {
      if (!executeHarvest(farmer, farmingPos, bs, block)) {
        executeBonemeal(farmer, farmingPos, block);
      }
    }
  }

  private void executeBonemeal(@Nonnull IFarmer farmer, @Nonnull BlockPos farmingPos, @Nonnull Block block) {
    if (hasBonemeal() && bonemealCooldown-- <= 0 && random.nextFloat() <= FarmConfig.farmBonemealChance.get()
        && farmer.checkAction(FarmingAction.FERTILIZE, FarmingTool.HAND)) {
      final ItemStack fertStack = getStackInSlot(minFirtSlot);
      Fertilizer fertilizer = Fertilizer.getInstance(fertStack);
      if ((fertilizer.applyOnPlant() != isOpen(farmingPos, block)) || (fertilizer.applyOnAir() == world.isAirBlock(farmingPos))) {
        FakePlayerEIO farmerJoe = farmer.startUsingItem(fertStack);
        if (fertilizer.apply(fertStack, farmerJoe, world, farmingPos)) {
          PacketHandler.sendToAllAround(new PacketFarmAction(farmingPos), this);
          bonemealCooldown = FarmConfig.farmBonemealDelaySuccess.get();
          farmer.registerAction(FarmingAction.FERTILIZE, FarmingTool.HAND);
        } else {
          usePower(FarmConfig.farmBonemealEnergyUseFail.get());
          bonemealCooldown = FarmConfig.farmBonemealDelayFail.get();
        }
        setInventorySlotContents(minFirtSlot, farmerJoe.getHeldItem(EnumHand.MAIN_HAND));
        farmerJoe.setHeldItem(EnumHand.MAIN_HAND, Prep.getEmpty());
        farmer.handleExtraItems(farmer.endUsingItem(true), farmingPos);
      }
    }
  }

  private boolean isOpen(@Nonnull BlockPos farmingPos, @Nonnull Block block) {
    return world.isAirBlock(farmingPos) || block.isReplaceable(world, pos);
  }

  private boolean executeHarvest(@Nonnull IFarmer farmer, @Nonnull BlockPos farmingPos, @Nonnull IBlockState bs, @Nonnull Block block) {
    IHarvestResult harvest = Commune.instance.harvestBlock(farmer, farmingPos, block, bs);
    if (harvest != null && (!harvest.getHarvestedBlocks().isEmpty() || !harvest.getDrops().isEmpty())) {
      if (!harvest.getHarvestedBlocks().isEmpty()) {
        PacketFarmAction pkt = new PacketFarmAction(harvest.getHarvestedBlocks());
        PacketHandler.sendToAllAround(pkt, this);
      }
      harvest.getDrops().apply(new Callback<EntityItem>() {
        @Override
        public void apply(@Nonnull EntityItem ei) {
          if (!ei.isDead) {
            ItemStack stack = ei.getEntityItem();
            if (Prep.isValid(stack)) {
              getFarmer().handleExtraItem(stack, farmingPos);
            }
            ei.setDead();
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
    currentTask = createTask(null, 0f);
  }

  @Override
  protected IPoweredTask createTask(@Nullable IMachineRecipe nextRecipe, float chance) {
    return new ContinuousTask(getPowerUsePerTick());
  }

  @Override
  public int getInventoryStackLimit(int slot) {
    if (slot >= minSupSlot && slot <= maxSupSlot) {
      return Math.min(FARM_STACK_LIMIT.get(getCapacitorData()), 64);
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

  @Override
  protected int usePower(int wantToUse) {
    return super.usePower(wantToUse);
  }

  public @Nonnull Set<FarmNotification> getNotification() {
    return notification;
  };
}
