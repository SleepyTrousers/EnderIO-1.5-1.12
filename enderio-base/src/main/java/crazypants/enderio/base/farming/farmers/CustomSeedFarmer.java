package crazypants.enderio.base.farming.farmers;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.api.farm.FarmNotification;
import crazypants.enderio.api.farm.FarmingAction;
import crazypants.enderio.api.farm.IFarmer;
import crazypants.enderio.api.farm.IFarmerJoe;
import crazypants.enderio.api.farm.IHarvestResult;
import crazypants.enderio.base.farming.FarmersRegistry;
import crazypants.enderio.base.farming.FarmingTool;
import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;

public class CustomSeedFarmer extends Impl<IFarmerJoe> implements IFarmerJoe {

  protected final @Nonnull Block plantedBlock;
  protected final int plantedBlockMeta;
  protected final int grownBlockMeta;
  protected final @Nonnull ItemStack seeds;
  protected @Nonnull Things tilledBlocks = new Things();
  protected boolean ignoreSustainCheck = false;
  protected boolean requiresTilling = true;
  protected boolean checkGroundForFarmland = false;
  protected boolean disableTreeFarm;

  public CustomSeedFarmer(@Nonnull Block plantedBlock, @Nonnull ItemStack seeds) {
    this(plantedBlock, 0, 7, seeds);
  }

  public CustomSeedFarmer(@Nonnull Block plantedBlock, int grownBlockMeta, @Nonnull ItemStack seeds) {
    this(plantedBlock, 0, grownBlockMeta, seeds);
  }

  public CustomSeedFarmer(@Nonnull Block plantedBlock, int plantedBlockMeta, int grownBlockMeta, @Nonnull ItemStack seeds) {
    this.plantedBlock = plantedBlock;
    this.plantedBlockMeta = plantedBlockMeta;
    this.grownBlockMeta = grownBlockMeta;
    this.seeds = seeds;
    FarmersRegistry.slotItemsSeeds.add(seeds);
    addTilledBlock(Blocks.FARMLAND);
  }

  public void clearTilledBlocks() {
    tilledBlocks = new Things();
  }

  public void addTilledBlock(@Nonnull Things block) {
    tilledBlocks.add(block);
  }

  public void addTilledBlock(@Nonnull Block block) {
    tilledBlocks.add(block);
  }

  public boolean isIgnoreGroundCanSustainCheck() {
    return ignoreSustainCheck;
  }

  public @Nonnull CustomSeedFarmer setIgnoreGroundCanSustainCheck(boolean ignoreSustainCheck) {
    this.ignoreSustainCheck = ignoreSustainCheck;
    return this;
  }

  public boolean isCheckGroundForFarmland() {
    return checkGroundForFarmland;
  }

  public @Nonnull CustomSeedFarmer setCheckGroundForFarmland(boolean checkGroundForFarmland) {
    this.checkGroundForFarmland = checkGroundForFarmland;
    return this;
  }

  public int getPlantedBlockMeta() {
    return plantedBlockMeta;
  }

  public @Nonnull Block getPlantedBlock() {
    return plantedBlock;
  }

  public @Nonnull ItemStack getSeeds() {
    return seeds;
  }

  public int getFullyGrownBlockMeta() {
    return grownBlockMeta;
  }

  @Override
  public boolean canHarvest(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull Block block, @Nonnull IBlockState bs) {
    int meta = bs.getBlock().getMetaFromState(bs);
    return block == getPlantedBlock() && getFullyGrownBlockMeta() == meta;
  }

  @Override
  public boolean canPlant(@Nonnull ItemStack stack) {
    return Prep.isValid(stack) && stack.isItemEqual(getSeeds());
  }

  @Override
  public boolean prepareBlock(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull Block block, @Nonnull IBlockState meta) {
    if (!farm.hasSeed(getSeeds(), bc)) {
      return false;
    }
    if (requiresTilling() && !isGroundTilled(farm, bc) && !farm.tillBlock(bc)) {
      return false;
    }
    return plantFromInventory(farm, bc);
  }

  public boolean requiresTilling() {
    return requiresTilling;
  }

  public @Nonnull CustomSeedFarmer setRequiresTilling(boolean requiresFarmland) {
    this.requiresTilling = requiresFarmland;
    return this;
  }

  protected boolean plantFromInventory(@Nonnull IFarmer farm, @Nonnull BlockPos bc) {
    World world = farm.getWorld();
    if (canPlant(farm, world, bc) && Prep.isValid(farm.takeSeedFromSupplies(bc))) {
      return plant(farm, world, bc);
    }
    return false;
  }

  @Override
  public IHarvestResult harvestBlock(@Nonnull IFarmer farm, @Nonnull final BlockPos pos, @Nonnull Block block, @Nonnull IBlockState state) {

    if (!canHarvest(farm, pos, block, state)) {
      return null;
    }
    if (!farm.hasTool(FarmingTool.HOE)) {
      farm.setNotification(FarmNotification.NO_HOE);
      return null;
    }

    final World world = farm.getWorld();
    final EntityPlayerMP joe = farm.startUsingItem(FarmingTool.HOE);
    final int fortune = farm.getLootingValue(FarmingTool.HOE);
    final NNList<EntityItem> result = new NNList<EntityItem>();

    List<ItemStack> drops = block.getDrops(world, pos, state, fortune);
    float chance = ForgeEventFactory.fireBlockHarvesting(drops, joe.world, pos, state, fortune, 1.0F, false, joe);
    farm.registerAction(FarmingAction.HARVEST, FarmingTool.HOE, state, pos);
    boolean removed = false;
    for (ItemStack stack : drops) {
      if (world.rand.nextFloat() <= chance) {
        if (!removed && stack.isItemEqual(getSeeds())) {
          stack.shrink(1);
          removed = true;
          if (Prep.isValid(stack)) {
            result.add(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack.copy()));
          }
        } else {
          result.add(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack.copy()));
        }
      }
    }

    NNList.wrap(farm.endUsingItem(FarmingTool.HOE)).apply(new Callback<ItemStack>() {
      @Override
      public void apply(@Nonnull ItemStack drop) {
        result.add(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop.copy()));
      }
    });

    if (removed) {
      if (!plant(farm, world, pos)) {
        result.add(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, getSeeds().copy()));
        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 1 | 2);
      }
    } else {
      world.setBlockState(pos, Blocks.AIR.getDefaultState(), 1 | 2);
    }

    return new HarvestResult(result, pos);
  }

  protected boolean isGroundTilled(@Nonnull IFarmer farm, @Nonnull BlockPos plantingLocation) {
    return tilledBlocks.contains(farm.getBlockState(plantingLocation.down()).getBlock());
  }

  protected boolean canPlant(@Nonnull IFarmer farm, @Nonnull World world, @Nonnull BlockPos bc) {
    Block target = getPlantedBlock();
    BlockPos groundPos = bc.down();
    IBlockState bs = world.getBlockState(groundPos);
    Block ground = bs.getBlock();
    IPlantable plantable = (IPlantable) getPlantedBlock();
    if (target.canPlaceBlockAt(world, bc) && (ground.canSustainPlant(bs, world, groundPos, EnumFacing.UP, plantable) || ignoreSustainCheck)
        && (!isCheckGroundForFarmland() || isGroundTilled(farm, bc))) {
      return true;
    }
    return false;
  }

  protected boolean plant(@Nonnull IFarmer farm, @Nonnull World world, @Nonnull BlockPos bc) {
    world.setBlockState(bc, Blocks.AIR.getDefaultState(), 1 | 2);
    if (canPlant(farm, world, bc)) {
      world.setBlockState(bc, getPlantedBlock().getStateFromMeta(getPlantedBlockMeta()), 1 | 2);
      farm.registerAction(FarmingAction.PLANT, FarmingTool.HOE, Blocks.AIR.getDefaultState(), bc);
      return true;
    }
    return false;
  }

  public boolean doesDisableTreeFarm() {
    return disableTreeFarm;
  }
}
