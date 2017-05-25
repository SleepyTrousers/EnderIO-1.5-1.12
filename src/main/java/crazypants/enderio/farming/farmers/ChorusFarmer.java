package crazypants.enderio.farming.farmers;

import java.util.ArrayList;
import java.util.List;

import crazypants.enderio.farming.FarmNotification;
import crazypants.enderio.farming.TileFarmStation;
import crazypants.enderio.farming.TileFarmStation.ToolType;
import crazypants.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChorusFlower;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class ChorusFarmer implements IFarmerJoe {

  private final Item flowerItem;

  public ChorusFarmer() {
    flowerItem = Item.getItemFromBlock(Blocks.CHORUS_FLOWER);
  }

  @Override
  public boolean canPlant(ItemStack stack) {
    return Prep.isValid(stack) && stack.getItem() == flowerItem;
  }

  @Override
  public boolean prepareBlock(TileFarmStation farm, BlockPos pos, Block block, IBlockState state) {
    final ItemStack seed = farm.getSeedTypeInSuppliesFor(pos);
    final EntityPlayerMP player = farm.getFakePlayer();
    final World world = farm.getWorld();
    if (canPlant(seed) && isValidPlantingSpot(world, pos) && player.canPlayerEdit(pos, EnumFacing.UP, seed)
        && world.canBlockBePlaced(Blocks.CHORUS_FLOWER, pos, false, EnumFacing.UP, (Entity) null, seed)) {
      IBlockState iblockstate1 = Blocks.CHORUS_FLOWER.getDefaultState().withProperty(BlockChorusFlower.AGE, 0);

      IBlockState oldState = world.getBlockState(pos);
      if (placeBlockAt(seed, player, world, pos, iblockstate1)) {
        if (Prep.isValid(farm.takeSeedFromSupplies(pos))) {
          SoundType soundtype = world.getBlockState(pos).getBlock().getSoundType(world.getBlockState(pos), world, pos, player);
          world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
          return true;
        } else {
          world.setBlockState(pos, oldState, 3);
        }
      }

    }
    return false;
  }

  private boolean isValidPlantingSpot(World world, BlockPos pos) {
    for (int x = -1; x <= 1; x++) {
      for (int y = 0; y <= 1; y++) {
        for (int z = -1; z <= 1; z++) {
          if (!world.getBlockState(pos.add(x, y, z)).getBlock().isReplaceable(world, pos.add(x, y, z))) {
            return false;
          }
        }
      }
    }
    return true;
  }

  private boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, IBlockState newState) {
    if (!world.setBlockState(pos, newState, 3))
      return false;

    IBlockState state = world.getBlockState(pos);
    if (state.getBlock() == newState.getBlock()) {
      state.getBlock().onBlockPlacedBy(world, pos, state, player, stack);
    }

    return true;
  }

  @Override
  public boolean canHarvest(TileFarmStation farm, BlockPos bc, Block block, IBlockState state) {
    return state.getBlock() == Blocks.CHORUS_PLANT || state.getBlock() == Blocks.CHORUS_FLOWER;
  }

  @Override
  public IHarvestResult harvestBlock(TileFarmStation farm, BlockPos bc, Block block, IBlockState state) {
    if (!farm.hasAxe()) {
      farm.setNotification(FarmNotification.NO_AXE);
      return null;
    }

    ChorusWalker walker = new ChorusWalker(farm, bc);
    if (walker.flowersToHarvest.isEmpty() && walker.stemsToHarvest.isEmpty()) {
      return null;
    }

    World world = farm.getWorld();
    final int fortune = farm.getMaxLootingValue();
    HarvestResult result = new HarvestResult();

    while (!walker.flowersToHarvest.isEmpty() && farm.hasAxe() && farm.hasPower()) {
      BlockPos remove = walker.flowersToHarvest.remove(0);
      doHarvest(farm, world, farm.getBlockState(remove), remove, fortune, result);
    }
    while (!walker.stemsToHarvest.isEmpty() && farm.hasAxe() && farm.hasPower()) {
      // Note: While stems can be broken by any tool, I decided to use the axe. This way a chorus farm can be used without hoe and the various auto-smelt
      // enchantments on axes can be used for the fruits.
      BlockPos remove = walker.stemsToHarvest.remove(walker.stemsToHarvest.size() - 1);
      doHarvest(farm, world, farm.getBlockState(remove), remove, fortune, result);
    }

    return result.getDrops().isEmpty() && result.getHarvestedBlocks().isEmpty() ? null : result;
  }

  private void doHarvest(TileFarmStation farm, World world, IBlockState blockState, BlockPos pos, int fortune, HarvestResult result) {
    farm.setJoeUseItem(farm.getTool(ToolType.AXE));
    List<ItemStack> drops = blockState.getBlock().getDrops(world, pos, blockState, fortune);
    float chance = ForgeEventFactory.fireBlockHarvesting(drops, world, pos, blockState, fortune, 1f, false, farm.getFakePlayer());

    if (drops != null) {
      for (ItemStack drop : drops) {
        if (farm.getWorld().rand.nextFloat() <= chance) {
          result.drops.add(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop.copy()));
        }
      }
    }

    // flowers drop here by spawning their drops into the world (joe's world captures those)
    blockState.getBlock().harvestBlock(farm.getFakePlayer().worldObj, farm.getFakePlayer(), pos, blockState, null, farm.getFakePlayer().getHeldItemMainhand());
    farm.clearJoeUseItem(true);

    farm.actionPerformed(true);
    farm.damageAxe(blockState.getBlock(), pos);
    world.setBlockToAir(pos);
    result.harvestedBlocks.add(pos);
  }

  private static final EnumFacing[] GROW_DIRECTIONS = { EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST };

  private static class ChorusWalker {

    // Yes, there are some edge cases remaining where a chorus plant is broken improperly. But this is good enough and can deal with all properly grown plants.

    final List<BlockPos> flowersToHarvest = new ArrayList<BlockPos>();
    final List<BlockPos> stemsToHarvest = new ArrayList<BlockPos>();
    final TileFarmStation farm;

    ChorusWalker(TileFarmStation farm, BlockPos pos) {
      this.farm = farm;
      collect(pos, EnumFacing.DOWN);
    }

    boolean collect(BlockPos pos, EnumFacing from) {
      IBlockState state = farm.getWorld().getBlockState(pos);
      if (state.getBlock() == Blocks.CHORUS_PLANT) {
        boolean isNeeded = false;
        for (EnumFacing side : GROW_DIRECTIONS) {
          if (side != from) {
            isNeeded |= collect(pos.offset(side), side.getOpposite());
          }
        }
        if (!isNeeded) {
          stemsToHarvest.add(pos);
        }
        return isNeeded;
      } else if (state.getBlock() == Blocks.CHORUS_FLOWER) {
        if (state.getValue(BlockChorusFlower.AGE) == 5) {
          flowersToHarvest.add(pos);
          return false;
        }
        return true;
      }
      return false;
    }
  }
}
