package crazypants.enderio.base.farming.farmers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList.Callback;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.farming.FarmNotification;
import crazypants.enderio.base.farming.FarmersRegistry;
import crazypants.enderio.base.farming.FarmingAction;
import crazypants.enderio.base.farming.FarmingTool;
import crazypants.enderio.base.farming.IFarmer;
import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry.Impl;

public class TreeFarmer extends Impl<IFarmerJoe> implements IFarmerJoe {

  private static final @Nonnull HeightComparator comp = new HeightComparator();

  protected final @Nonnull Things saplings;
  protected final @Nonnull Things woods;

  protected final @Nonnull TreeHarvestUtil harvester = new TreeHarvestUtil();
  private boolean ignoreMeta = false;

  public TreeFarmer(@Nonnull Things saplings, @Nonnull Things woods) {
    this.woods = woods;
    FarmersRegistry.slotItemsProduce.add(woods);
    this.saplings = saplings;
    FarmersRegistry.slotItemsSeeds.add(saplings);
  }

  private static @Nonnull Things makeThings(Block... wood) {
    Things result = new Things();
    for (Block block : wood) {
      result.add(block);
    }
    return result;
  }

  public TreeFarmer(Block sapling, Block... wood) {
    this(makeThings(sapling), makeThings(wood));
  }

  public TreeFarmer(boolean ignoreMeta, Block sapling, Block... wood) {
    this(sapling, wood);
    this.ignoreMeta = ignoreMeta;
  }

  @Override
  public boolean canHarvest(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull Block block, @Nonnull IBlockState bs) {
    return isWood(block);
  }

  protected boolean isWood(Block block) {
    return woods.contains(block);
  }

  @Override
  public boolean canPlant(@Nonnull ItemStack stack) {
    return Prep.isValid(stack) && saplings.contains(stack) && Block.getBlockFromItem(stack.getItem()) != Blocks.AIR;
  }

  @Override
  public boolean prepareBlock(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull Block block, @Nonnull IBlockState meta) {
    if (saplings.contains(block)) {
      return true;
    }
    return plantFromInventory(farm, bc, block, meta);
  }

  protected boolean plantFromInventory(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull Block block, @Nonnull IBlockState meta) {
    World world = farm.getWorld();
    final ItemStack currentSapling = farm.getSeedTypeInSuppliesFor(bc);
    if (canPlant(world, bc, currentSapling)) {
      ItemStack seed = farm.takeSeedFromSupplies(bc);
      if (Prep.isValid(seed)) {
        return plant(farm, world, bc, seed);
      }
    }
    return false;
  }

  protected boolean canPlant(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull ItemStack sapling) {
    if (!saplings.contains(sapling)) {
      return false;
    }
    BlockPos grnPos = pos.down();
    IBlockState bs = world.getBlockState(grnPos);
    Block ground = bs.getBlock();
    Block saplingBlock = Block.getBlockFromItem(sapling.getItem());
    if (saplingBlock == Blocks.AIR) {
      return false;
    }
    if (saplingBlock.canPlaceBlockAt(world, pos)) {
      if (saplingBlock instanceof IPlantable) {
        return ground.canSustainPlant(bs, world, grnPos, EnumFacing.UP, (IPlantable) saplingBlock);
      }
      return true;
    }
    return false;
  }

  protected boolean plant(@Nonnull IFarmer farm, @Nonnull World world, @Nonnull BlockPos bc, @Nonnull ItemStack sapling) {
    if (canPlant(world, bc, sapling)) {
      world.setBlockToAir(bc);
      final Item item = sapling.getItem();
      final IBlockState state = Block.getBlockFromItem(item).getStateFromMeta(item.getMetadata(sapling.getMetadata()));
      world.setBlockState(bc, state, 1 | 2);
      farm.registerAction(FarmingAction.PLANT, FarmingTool.HOE, state, bc);
      return true;
    } else {
      return false;
    }
  }

  // these will be using during harvesting
  boolean hasAxe, hasShears, hasHoe;
  int fortune, noShearingPercentage, shearCount;

  protected void setupHarvesting(@Nonnull IFarmer farm, @Nonnull BlockPos harvestLocation) {
    hasAxe = farm.hasTool(FarmingTool.AXE);
    if (hasAxe) {
      fortune = farm.getLootingValue(FarmingTool.AXE);
      hasShears = farm.hasTool(FarmingTool.SHEARS);
      hasHoe = farm.hasTool(FarmingTool.HOE);
      noShearingPercentage = farm.isLowOnSaplings(harvestLocation);
      shearCount = 0;
    }
  }

  @Override
  public IHarvestResult harvestBlock(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull Block block, @Nonnull IBlockState meta) {
    setupHarvesting(farm, bc);

    if (!hasAxe) {
      farm.setNotification(FarmNotification.NO_AXE);
      return null;
    }

    final World world = farm.getWorld();
    final HarvestResult res = new HarvestResult();
    harvester.harvest(farm, this, bc, res);
    Collections.sort(res.getHarvestedBlocks(), comp);

    List<BlockPos> actualHarvests = new ArrayList<BlockPos>();

    // avoid calling this in a loop

    for (int i = 0; i < res.getHarvestedBlocks().size() && hasAxe; i++) {
      final BlockPos coord = res.getHarvestedBlocks().get(i);
      harvestSingleBlock(farm, world, res, coord);
      actualHarvests.add(coord);
    }

    res.getHarvestedBlocks().clear();
    res.getHarvestedBlocks().addAll(actualHarvests);

    tryReplanting(farm, world, bc, res);

    return res;
  }

  void harvestSingleBlock(@Nonnull IFarmer farm, final @Nonnull World world, final @Nonnull HarvestResult result, final @Nonnull BlockPos harvestPos) {
    float chance = 1.0F;
    List<ItemStack> drops;
    final IBlockState state = farm.getBlockState(harvestPos);
    final Block blk = state.getBlock();

    if (blk instanceof IShearable && hasShears && ((shearCount / result.getHarvestedBlocks().size() + noShearingPercentage) < 100)) {
      drops = ((IShearable) blk).onSheared(farm.getTool(FarmingTool.SHEARS), world, harvestPos, 0);
      shearCount += 100;
      farm.registerAction(FarmingAction.HARVEST, FarmingTool.SHEARS, state, harvestPos);
      hasShears = farm.hasTool(FarmingTool.SHEARS);
      if (!hasShears) {
        farm.setNotification(FarmNotification.NO_SHEARS);
      }
    } else {
      drops = blk.getDrops(world, harvestPos, state, fortune);
      EntityPlayerMP joe = farm.startUsingItem(FarmingTool.AXE);
      chance = ForgeEventFactory.fireBlockHarvesting(drops, joe.world, harvestPos, state, fortune, chance, false, joe);
      if (isWood(blk) || !hasHoe) {
        farm.registerAction(FarmingAction.HARVEST, FarmingTool.AXE, state, harvestPos);
        hasAxe = farm.hasTool(FarmingTool.AXE);
        if (!hasAxe) {
          farm.setNotification(FarmNotification.NO_AXE);
        }
      } else {
        farm.registerAction(FarmingAction.HARVEST, FarmingTool.HOE, state, harvestPos);
        hasHoe = farm.hasTool(FarmingTool.HOE);
        if (!hasHoe) {
          farm.setNotification(FarmNotification.NO_HOE);
        }
      }
      farm.endUsingItem(FarmingTool.HOE).apply(new Callback<ItemStack>() {
        @Override
        public void apply(@Nonnull ItemStack drop) {
          result.getDrops().add(new EntityItem(world, harvestPos.getX() + 0.5, harvestPos.getY() + 0.5, harvestPos.getZ() + 0.5, drop.copy()));
        }
      });
    }

    if (drops != null) {
      BlockPos farmPos = farm.getLocation();
      for (ItemStack drop : drops) {
        if (world.rand.nextFloat() <= chance) {
          result.getDrops().add(new EntityItem(world, farmPos.getX() + 0.5, farmPos.getY() + 0.5, farmPos.getZ() + 0.5, drop.copy()));
        }
      }
    }

    farm.getWorld().setBlockToAir(harvestPos);
  }

  protected void tryReplanting(@Nonnull IFarmer farm, @Nonnull World world, @Nonnull BlockPos bc, @Nonnull HarvestResult res) {
    if (!world.isAirBlock(bc)) {
      return;
    }
    ItemStack allowedSeed = Prep.getEmpty();
    if (farm.isSlotLocked(bc)) {
      ItemStack seedTypeInSuppliesFor = farm.getSeedTypeInSuppliesFor(bc);
      if (Prep.isValid(seedTypeInSuppliesFor)) {
        allowedSeed = seedTypeInSuppliesFor;
      }
    }
    for (EntityItem drop : res.getDrops()) {
      if (Prep.isInvalid(allowedSeed) || ItemStack.areItemsEqual(allowedSeed, drop.getEntityItem())) {
        if (canPlant(drop.getEntityItem()) && plant(farm, world, bc, drop.getEntityItem())) {
          res.getDrops().remove(drop);
          return;
        }
      }
    }
  }

  public boolean getIgnoreMeta() {
    return ignoreMeta;
  }

  public void setIgnoreMeta(boolean ignoreMeta) {
    this.ignoreMeta = ignoreMeta;
  }

  private static class HeightComparator implements Comparator<BlockPos> {

    @Override
    public int compare(BlockPos o1, BlockPos o2) {
      return Integer.compare(o2.getY(), o1.getY()); // reverse order
    }

  }

}
