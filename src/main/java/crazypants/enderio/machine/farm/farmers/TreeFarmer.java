package crazypants.enderio.machine.farm.farmers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import crazypants.enderio.config.Config;
import crazypants.enderio.machine.farm.FarmNotification;
import crazypants.enderio.machine.farm.FarmStationContainer;
import crazypants.enderio.machine.farm.TileFarmStation;
import crazypants.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.event.ForgeEventFactory;

public class TreeFarmer implements IFarmerJoe {

  private static final HeightComparator comp = new HeightComparator();

  protected Block sapling;
  protected ItemStack saplingItem;
  protected Block[] woods;

  protected TreeHarvestUtil harvester = new TreeHarvestUtil();
  private boolean ignoreMeta;

  public TreeFarmer(Block sapling, Block... wood) {
    this.sapling = sapling;
    if (sapling != null) {
      saplingItem = new ItemStack(sapling);
      FarmStationContainer.slotItemsSeeds.add(saplingItem);
    }
    woods = wood;
    for (Block awood : woods) {
      FarmStationContainer.slotItemsProduce.add(new ItemStack(awood));
    }
  }

  public TreeFarmer(boolean ignoreMeta, Block sapling, Block... wood) {
    this(sapling, wood);
    this.ignoreMeta = ignoreMeta;
  }

  public TreeFarmer(ItemStack sapling, ItemStack wood) {
    this(Block.getBlockFromItem(sapling.getItem()), Block.getBlockFromItem(wood.getItem()));
  }

  @Override
  public boolean canHarvest(TileFarmStation farm, BlockPos bc, Block block, IBlockState bs) {
    return isWood(block);
  }

  protected boolean isWood(Block block) {
    for (Block wood : woods) {
      if (block == wood) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean canPlant(ItemStack stack) {
    return Prep.isValid(stack) && stack.getItem() == saplingItem.getItem();
  }

  @Override
  public boolean prepareBlock(TileFarmStation farm, BlockPos bc, Block block, IBlockState meta) {
    if (block == sapling) {
      return true;
    }
    return plantFromInventory(farm, bc, block, meta);
  }

  protected boolean plantFromInventory(TileFarmStation farm, BlockPos bc, Block block, IBlockState meta) {
    World worldObj = farm.getWorld();
    if (canPlant(worldObj, bc)) {
      ItemStack seed = farm.takeSeedFromSupplies(saplingItem, bc, false);
      if (Prep.isValid(seed)) {
        return plant(farm, worldObj, bc, seed);
      }
    }
    return false;
  }

  protected boolean canPlant(World worldObj, BlockPos bc) {
    BlockPos grnPos = bc.down();
    IBlockState bs = worldObj.getBlockState(grnPos);
    Block ground = bs.getBlock();
    IPlantable plantable = (IPlantable) sapling;
    if (sapling.canPlaceBlockAt(worldObj, bc) && ground.canSustainPlant(bs, worldObj, grnPos, EnumFacing.UP, plantable)) {
      return true;
    }
    return false;
  }

  protected boolean plant(TileFarmStation farm, World worldObj, BlockPos bc, ItemStack seed) {
    worldObj.setBlockToAir(bc);
    if (canPlant(worldObj, bc)) {
      worldObj.setBlockState(bc, sapling.getStateFromMeta(seed.getItemDamage()), 1 | 2);
      farm.actionPerformed(false);
      return true;
    }
    return false;
  }

  @Override
  public IHarvestResult harvestBlock(TileFarmStation farm, BlockPos bc, Block block, IBlockState meta) {

    boolean hasAxe = farm.hasAxe();

    if (!hasAxe) {
      farm.setNotification(FarmNotification.NO_AXE);
      return null;
    }

    World worldObj = farm.getWorld();
    final EntityPlayerMP fakePlayer = farm.getFakePlayer();
    final int fortune = farm.getMaxLootingValue();
    HarvestResult res = new HarvestResult();
    harvester.harvest(farm, this, bc, res);
    Collections.sort(res.harvestedBlocks, comp);

    List<BlockPos> actualHarvests = new ArrayList<BlockPos>();

    // avoid calling this in a loop
    boolean hasShears = farm.hasShears();
    int noShearingPercentage = farm.isLowOnSaplings(bc);
    int shearCount = 0;

    for (int i = 0; i < res.harvestedBlocks.size() && hasAxe; i++) {
      BlockPos coord = res.harvestedBlocks.get(i);
      Block blk = farm.getBlock(coord);

      List<ItemStack> drops;
      boolean wasSheared = false;
      boolean wasAxed = false;
      boolean wasWood = isWood(blk);
      float chance = 1.0F;

      if (blk instanceof IShearable && hasShears && ((shearCount / res.harvestedBlocks.size() + noShearingPercentage) < 100)) {
        drops = ((IShearable) blk).onSheared(null, worldObj, coord, 0);
        wasSheared = true;
        shearCount += 100;
      } else {
        drops = blk.getDrops(worldObj, coord, farm.getBlockState(coord), fortune);
        chance = ForgeEventFactory.fireBlockHarvesting(drops, worldObj, coord, farm.getBlockState(coord), fortune, chance, false, fakePlayer);
        wasAxed = true;
      }

      if (drops != null) {
        for (ItemStack drop : drops) {
          if (worldObj.rand.nextFloat() <= chance) {
            res.drops.add(new EntityItem(worldObj, bc.getX() + 0.5, bc.getY() + 0.5, bc.getZ() + 0.5, drop.copy()));
          }
        }
      }

      if (wasAxed && !wasWood) {
        wasAxed = Config.farmAxeDamageOnLeafBreak;
      }

      farm.actionPerformed(wasWood || wasSheared);
      if (wasAxed) {
        farm.damageAxe(blk, new BlockPos(coord));
        hasAxe = farm.hasAxe();
      } else if (wasSheared) {
        farm.damageShears(blk, new BlockPos(coord));
        hasShears = farm.hasShears();
      }

      farm.getWorld().setBlockToAir(coord);
      actualHarvests.add(coord);
    }

    ItemStack[] inv = fakePlayer.inventory.mainInventory;
    for (int slot = 0; slot < inv.length; slot++) {
      ItemStack stack = inv[slot];
      if (Prep.isValid(stack)) {
        inv[slot] = Prep.getEmpty();
        EntityItem entityitem = new EntityItem(worldObj, bc.getX() + 0.5, bc.getY() + 1, bc.getZ() + 0.5, stack);
        res.drops.add(entityitem);
      }
    }

    if (!hasAxe) {
      farm.setNotification(FarmNotification.NO_AXE);
    }

    res.harvestedBlocks.clear();
    res.harvestedBlocks.addAll(actualHarvests);

    tryReplanting(farm, worldObj, bc, res);

    return res;
  }

  protected void tryReplanting(TileFarmStation farm, World worldObj, BlockPos bc, HarvestResult res) {
    ItemStack allowedSeed = Prep.getEmpty();
    int supplySlotForCoord = farm.getSupplySlotForCoord(bc);
    if (farm.isSlotLocked(supplySlotForCoord)) {
      ItemStack seedTypeInSuppliesFor = farm.getSeedTypeInSuppliesFor(supplySlotForCoord);
      if (Prep.isValid(seedTypeInSuppliesFor)) {
        allowedSeed = seedTypeInSuppliesFor;
      }
    }
    for (EntityItem drop : res.drops) {
      if (Prep.isInvalid(allowedSeed) || ItemStack.areItemsEqual(allowedSeed, drop.getEntityItem())) {
        if (canPlant(drop.getEntityItem()) && plant(farm, worldObj, bc, drop.getEntityItem())) {
          res.drops.remove(drop);
          return;
        }
      }
    }
  }

  public boolean getIgnoreMeta() {
    return ignoreMeta;
  }

  private static class HeightComparator implements Comparator<BlockPos> {

    @Override
    public int compare(BlockPos o1, BlockPos o2) {
      return compare(o2.getY(), o1.getY()); // reverse order
    }

    // same as 1.7 Integer.compare
    public static int compare(int x, int y) {
      return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }
  }

}
