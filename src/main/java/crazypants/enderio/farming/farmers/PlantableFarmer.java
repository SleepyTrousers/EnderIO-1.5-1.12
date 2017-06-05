package crazypants.enderio.farming.farmers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.farming.FarmNotification;
import crazypants.enderio.farming.FarmingAction;
import crazypants.enderio.farming.FarmingTool;
import crazypants.enderio.farming.IFarmer;
import crazypants.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStem;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.ForgeEventFactory;

public class PlantableFarmer implements IFarmerJoe {

  private Set<Block> harvestExcludes = new HashSet<Block>();

  public void addHarvestExlude(Block block) {
    harvestExcludes.add(block);
  }

  @Override
  public boolean canPlant(@Nonnull ItemStack stack) {
    if (Prep.isInvalid(stack)) {
      return false;
    }
    return stack.getItem() instanceof IPlantable;
  }

  @Override
  public boolean prepareBlock(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull Block block, @Nonnull IBlockState meta) {
    ItemStack seedStack = farm.getSeedTypeInSuppliesFor(bc);
    if (Prep.isInvalid(seedStack)) {
      if (!farm.isSlotLocked(bc)) {
        farm.setNotification(FarmNotification.NO_SEEDS);
      }
      return false;
    }

    if (!(seedStack.getItem() instanceof IPlantable)) {
      return false;
    }

    IPlantable plantable = (IPlantable) seedStack.getItem();
    EnumPlantType type = plantable.getPlantType(farm.getWorld(), bc);
    if (type == null) {
      return false;
    }
    if (type == EnumPlantType.Nether) {
      Block ground = farm.getBlockState(bc.down()).getBlock();
      if (ground != Blocks.SOUL_SAND) {
        return false;
      }
      return plantFromInventory(farm, bc, plantable);
    }

    if (type == EnumPlantType.Crop) {
      farm.tillBlock(bc);
      return plantFromInventory(farm, bc, plantable);
    }

    if (type == EnumPlantType.Water) {
      return plantFromInventory(farm, bc, plantable);
    }

    return false;
  }

  // From BlockBush, as a reference
  // @Override
  // public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z)
  // {
  // if (this == Blocks.wheat) return Crop;
  // if (this == Blocks.carrots) return Crop;
  // if (this == Blocks.potatoes) return Crop;
  // if (this == Blocks.melon_stem) return Crop;
  // if (this == Blocks.pumpkin_stem) return Crop;
  // if (this == Blocks.deadbush) return Desert;
  // if (this == Blocks.waterlily) return Water;
  // if (this == Blocks.red_mushroom) return Cave;
  // if (this == Blocks.brown_mushroom) return Cave;
  // if (this == Blocks.nether_wart) return Nether;
  // if (this == Blocks.sapling) return Plains;
  // if (this == Blocks.tallgrass) return Plains;
  // if (this == Blocks.double_plant) return Plains;
  // if (this == Blocks.red_flower) return Plains;
  // if (this == Blocks.yellow_flower) return Plains;
  // return Plains;
  // }

  protected boolean plantFromInventory(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull IPlantable plantable) {
    World world = farm.getWorld();
    if (canPlant(world, bc, plantable) && Prep.isValid(farm.takeSeedFromSupplies(bc))) {
      return plant(farm, world, bc, plantable);
    }
    return false;
  }

  protected boolean plant(@Nonnull IFarmer farm, @Nonnull World world, @Nonnull BlockPos bc, @Nonnull IPlantable plantable) {
    world.setBlockState(bc, Blocks.AIR.getDefaultState(), 1 | 2);
    IBlockState target = plantable.getPlant(null, new BlockPos(0, 0, 0));
    if (target == null) {
      return false;
    }
    world.setBlockState(bc, target, 1 | 2);
    farm.registerAction(FarmingAction.PLANT, FarmingTool.HOE, target, bc);
    return true;
  }

  protected boolean canPlant(@Nonnull World world, @Nonnull BlockPos bc, @Nonnull IPlantable plantable) {
    IBlockState target = plantable.getPlant(null, new BlockPos(0, 0, 0));
    BlockPos groundPos = bc.down();
    IBlockState groundBS = world.getBlockState(groundPos);
    Block ground = groundBS.getBlock();
    if (target != null && target.getBlock().canPlaceBlockAt(world, bc) && ground.canSustainPlant(groundBS, world, groundPos, EnumFacing.UP, plantable)) {
      return true;
    }
    return false;
  }

  @Override
  public boolean canHarvest(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull Block block, @Nonnull IBlockState meta) {
    if (!harvestExcludes.contains(block) && block instanceof IGrowable && !(block instanceof BlockStem)) {
      return !((IGrowable) block).canGrow(farm.getWorld(), bc, meta, true);
    }
    return false;
  }

  @Override
  public IHarvestResult harvestBlock(@Nonnull IFarmer farm, final @Nonnull BlockPos pos, @Nonnull Block block, @Nonnull IBlockState meta) {
    if (!canHarvest(farm, pos, block, meta)) {
      return null;
    }
    if (!farm.hasTool(FarmingTool.HOE)) {
      farm.setNotification(FarmNotification.NO_HOE);
      return null;
    }

    final World world = farm.getWorld();
    final NNList<EntityItem> result = new NNList<EntityItem>();
    final EntityPlayerMP fakePlayer = farm.startUsingItem(FarmingTool.HOE);
    final int fortune = farm.getLootingValue(FarmingTool.HOE);

    ItemStack removedPlantable = Prep.getEmpty();

    List<ItemStack> drops = block.getDrops(world, pos, meta, fortune);
    float chance = ForgeEventFactory.fireBlockHarvesting(drops, world, pos, meta, fortune, 1.0F, false, fakePlayer);
    farm.registerAction(FarmingAction.HARVEST, FarmingTool.HOE, meta, pos);
    for (ItemStack stack : drops) {
      if (stack != null && Prep.isValid(stack) && world.rand.nextFloat() <= chance) {
        if (Prep.isInvalid(removedPlantable) && isPlantableForBlock(stack, block)) {
          removedPlantable = stack.copy();
          removedPlantable.setCount(1);
          stack.shrink(1);
        }
        if (Prep.isValid(stack)) {
          result.add(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack.copy()));
        }
      }
    }

    farm.endUsingItem(FarmingTool.HOE).apply(new Callback<ItemStack>() {
      @Override
      public void apply(@Nonnull ItemStack drop) {
        result.add(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop.copy()));
      }
    });

    if (Prep.isValid(removedPlantable)) {
      if (!plant(farm, world, pos, (IPlantable) removedPlantable.getItem())) {
        result.add(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, removedPlantable.copy()));
        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 1 | 2);
      }
    } else {
      world.setBlockState(pos, Blocks.AIR.getDefaultState(), 1 | 2);
    }

    return new HarvestResult(result, pos);
  }

  private boolean isPlantableForBlock(@Nonnull ItemStack stack, @Nonnull Block block) {
    if (!(stack.getItem() instanceof IPlantable)) {
      return false;
    }
    IPlantable plantable = (IPlantable) stack.getItem();
    IBlockState b = plantable.getPlant(null, new BlockPos(0, 0, 0));
    return b != null && b.getBlock() == block;
  }

}
