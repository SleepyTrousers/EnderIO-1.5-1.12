package crazypants.enderio.machine.farm.farmers;

import net.minecraft.block.*;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.machine.farm.TileFarmStation;
import crazypants.util.BlockCoord;

import java.util.ArrayList;
import java.util.List;

public class TreeHarvestUtil {

  // Determine if harvested leaves can drop fruit items.
  public static boolean canDropFood(Block block, int meta){
    if (isBlockBOPFruitLeaf(block)) {
      // Block is either BoP Apple or Persimmon leaf
      return true;
    } else if (block instanceof BlockOldLeaf && (meta == 0 || meta == 4 || meta == 8 || meta == 12)) {
      // Block is vanilla Minecraft Oak Leaf
      return true;
    } else if (block instanceof BlockNewLeaf && (meta == 1 || meta == 5 || meta == 9 || meta == 13)){
      // Block is vanilla Minecraft Dark Oak Leaf
      return true;
    }
    return false;
  }

  // Calculate Drop chance for fruit when harvesting leaves.
  public static EntityItem dropFoodAsItemWithChance(World world, BlockCoord bc, Block block, int meta) {
    ItemStack fruit = new ItemStack(Items.apple);

    // Biomes O' Plenty
    if (isBlockBOPFruitLeaf(block)) {
      Class<?> LeafClass = block.getClass();
      Class<?> BlockBOPPersimmonLeaves = GameRegistry.findBlock("BiomesOPlenty", "persimmonLeaves").getClass();

      Item food = GameRegistry.findItem("BiomesOPlenty", "food");
      if (LeafClass == BlockBOPPersimmonLeaves) fruit = new ItemStack(food, 1, 8); // Change fruit to Persimmon.

      if ((meta & 3) == 3 && world.rand.nextInt(10) == 0) return new EntityItem(world, bc.x + 0.5, bc.y + 0.5, bc.z + 0.5, fruit);
      else if ((meta & 3) == 2 && world.rand.nextInt(50) == 0) return new EntityItem(world, bc.x + 0.5, bc.y + 0.5, bc.z + 0.5, fruit);
      else if ((meta & 3) == 1 && world.rand.nextInt(100) == 0) return new EntityItem(world, bc.x + 0.5, bc.y + 0.5, bc.z + 0.5, fruit);
      else if ((meta & 3) == 0 && world.rand.nextInt(200) == 0) return new EntityItem(world, bc.x + 0.5, bc.y + 0.5, bc.z + 0.5, fruit);
    }

    // Vanilla Oak and Dark Oak trees.
    if(world.rand.nextInt(200) == 0) {
      return new EntityItem(world, bc.x + 0.5, bc.y + 0.5, bc.z + 0.5, fruit);
    }
    return null;
  }
  
  private int horizontalRange;
  private int verticalRange;
  private BlockCoord origin;
  
  public TreeHarvestUtil() {
  }

  public void harvest(TileFarmStation farm, TreeFarmer farmer, BlockCoord bc, HarvestResult res) {
    horizontalRange = farm.getFarmSize() + 7;
    verticalRange = 30;
    harvest(farm.getWorldObj(), farm.getLocation(), bc, res, farmer.getIgnoreMeta());
  }
  
  public void harvest(World world, BlockCoord bc, HarvestResult res) {
    horizontalRange = 12;
    verticalRange = 30;
    origin = new BlockCoord(bc);
    Block wood = world.getBlock(bc.x, bc.y, bc.z);
    int woodMeta = world.getBlockMetadata(bc.x, bc.y, bc.z);
    harvestUp(world, bc, res, new HarvestTarget(wood, woodMeta));
  }
  
  private void harvest(World world, BlockCoord origin, BlockCoord bc, HarvestResult res, boolean ignoreMeta) {
    this.origin = new BlockCoord(origin);
    Block wood = world.getBlock(bc.x, bc.y, bc.z);
    int woodMeta = world.getBlockMetadata(bc.x, bc.y, bc.z);
    if (ignoreMeta)
    {
      harvestUp(world, bc, res, new BaseHarvestTarget(wood));
    }
    else
    {
      harvestUp(world, bc, res, new HarvestTarget(wood, woodMeta));
    }
  }
  
  protected void harvestUp(World world, BlockCoord bc, HarvestResult res, BaseHarvestTarget target) {

    if(!isInHarvestBounds(bc) || res.harvestedBlocks.contains(bc)) {
      return;
    }

    Block blk = world.getBlock(bc.x, bc.y,bc.z);
    boolean isLeaves = areTheseLeaves(blk);
    if(target.isTarget(blk, world.getBlockMetadata(bc.x, bc.y, bc.z)) || isLeaves) {
      res.harvestedBlocks.add(bc);
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        if(dir != ForgeDirection.DOWN) {
          harvestUp(world, bc.getLocation(dir), res, target);
        }
      }
    } else {
      // check the sides for connected wood
      harvestAdjacentWood(world, bc, res, target);
      //and another check for large oaks, where wood can be surrounded by leaves
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        if(dir.offsetY == 0) {
          BlockCoord loc = bc.getLocation(dir);
          Block targetBlock = world.getBlock(loc.x,loc.y,loc.z);
          if(targetBlock instanceof BlockLeaves) {
            harvestAdjacentWood(world, bc, res, target);
          }
        }
      }
    }

  }

  private void harvestAdjacentWood(World world, BlockCoord bc, HarvestResult res, BaseHarvestTarget target) {
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      if(dir.offsetY == 0) {
        BlockCoord loc = bc.getLocation(dir);
        Block targetBlock = world.getBlock(loc.x, loc.y, loc.z);
        if(target.isTarget(targetBlock, world.getBlockMetadata(loc.x, loc.y, loc.z))) {
          harvestUp(world, bc.getLocation(dir), res, target);
        }
      }
    }
  }

  private boolean isInHarvestBounds(BlockCoord bc) {
    
    int dist = Math.abs(origin.x - bc.x);
    if(dist > horizontalRange) {
      return false;
    }
    dist = Math.abs(origin.z - bc.z);
    if(dist > horizontalRange) {
      return false;
    }
    dist = Math.abs(origin.y - bc.y);
    if(dist > verticalRange) {
      return false;
    }
    return true;
  }

  private boolean areTheseLeaves(Block blk){
    List<Class<?>> leafTypes = new ArrayList<Class<?>>();

    // Leaf Blocks that subclass standard vanilla Minecraft BlockLeaves
    if (blk instanceof BlockLeaves) return true;

    // Add Biomes O' Plenty custom leaves
    if (Loader.isModLoaded("BiomesOPlenty")) try {
      Class<?> BlockBOPAppleLeaves = Class.forName("biomesoplenty.common.blocks.BlockBOPAppleLeaves");
      Class<?> BlockBOPColorizedLeaves = Class.forName("biomesoplenty.common.blocks.BlockBOPColorizedLeaves");
      Class<?> BlockBOPLeaves = Class.forName("biomesoplenty.common.blocks.BlockBOPLeaves");
      Class<?> BlockBOPPersimmonLeaves = Class.forName("biomesoplenty.common.blocks.BlockBOPPersimmonLeaves");

      leafTypes.add(BlockBOPAppleLeaves);
      leafTypes.add(BlockBOPColorizedLeaves);
      leafTypes.add(BlockBOPLeaves);
      leafTypes.add(BlockBOPPersimmonLeaves);
    } catch (Exception e) {
      // No BoP leaf blocks found... Carry on.
    }
    return leafTypes.contains(blk.getClass());
  }

  private static boolean isBlockBOPFruitLeaf(Block block) {
    String bop = "BiomesOPlenty";
    if (Loader.isModLoaded(bop)) {
      try {
        Class<?> LeafClass = block.getClass();
        Class<?> BlockBOPAppleLeaves = GameRegistry.findBlock(bop, "appleLeaves").getClass();
        Class<?> BlockBOPPersimmonLeaves = GameRegistry.findBlock(bop, "persimmonLeaves").getClass();
        if (LeafClass == BlockBOPAppleLeaves || LeafClass == BlockBOPPersimmonLeaves) {
          return true;
        }
      } catch (Exception e) {
        // Not a BoP fruit bearing leaf block... Carry on.
      }
    }
    return false;
  }
  
  private static final class HarvestTarget extends BaseHarvestTarget
  {
    private final int woodMeta;

    HarvestTarget(Block wood, int woodMeta) {
      super(wood);
      this.woodMeta = woodMeta;
    }

    boolean isTarget(Block blk, int meta) {
      return super.isTarget(blk,meta) && ((meta & 3) == (woodMeta & 3));
    }
  }

  private static class BaseHarvestTarget
  {
    private final Block wood;

    BaseHarvestTarget(Block wood) {
      this.wood = wood;
    }

    boolean isTarget(Block blk, int meta) {
      return blk == wood;
    }
  }

}
