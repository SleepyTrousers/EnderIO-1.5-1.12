package crazypants.enderio.base.farming.farmers;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;
import com.enderio.core.common.util.NNList.NNIterator;

import crazypants.enderio.api.farm.AbstractFarmerJoe;
import crazypants.enderio.api.farm.FarmNotification;
import crazypants.enderio.api.farm.FarmingAction;
import crazypants.enderio.api.farm.IFarmer;
import crazypants.enderio.api.farm.IHarvestResult;
import crazypants.enderio.base.farming.FarmingTool;
import crazypants.enderio.util.Prep;
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
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.ForgeEventFactory;

public class ChorusFarmer extends AbstractFarmerJoe {

  private final @Nonnull Item flowerItem;

  public ChorusFarmer() {
    flowerItem = Item.getItemFromBlock(Blocks.CHORUS_FLOWER);
  }

  @Override
  public boolean canPlant(@Nonnull ItemStack stack) {
    return stack.getItem() == flowerItem;
  }

  @Override
  public boolean prepareBlock(@Nonnull IFarmer farm, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
    final ItemStack seed = farm.getSeedTypeInSuppliesFor(pos);
    final EntityPlayerMP player = farm.getFakePlayer();
    final World world = farm.getWorld();
    if (canPlant(seed) && isValidPlantingSpot(world, pos) && player.canPlayerEdit(pos, EnumFacing.UP, seed)
        && world.mayPlace(Blocks.CHORUS_FLOWER, pos, false, EnumFacing.UP, (Entity) null)) {
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

  private boolean isValidPlantingSpot(@Nonnull World world, @Nonnull BlockPos pos) {
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

  private boolean placeBlockAt(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos,
      @Nonnull IBlockState newState) {
    if (!world.setBlockState(pos, newState, 3))
      return false;

    IBlockState state = world.getBlockState(pos);
    if (state.getBlock() == newState.getBlock()) {
      state.getBlock().onBlockPlacedBy(world, pos, state, player, stack);
    }

    return true;
  }

  @Override
  public boolean canHarvest(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull IBlockState state) {
    return state.getBlock() == Blocks.CHORUS_PLANT || state.getBlock() == Blocks.CHORUS_FLOWER;
  }

  @Override
  public IHarvestResult harvestBlock(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull IBlockState state) {
    if (!farm.hasTool(FarmingTool.AXE)) {
      farm.setNotification(FarmNotification.NO_AXE);
      return null;
    }

    ChorusWalker walker = new ChorusWalker(farm, bc);
    if (walker.flowersToHarvest.isEmpty() && walker.stemsToHarvest.isEmpty()) {
      return null;
    }

    World world = farm.getWorld();
    final int fortune = farm.getLootingValue(FarmingTool.AXE);
    HarvestResult result = new HarvestResult();

    while (!walker.flowersToHarvest.isEmpty() && farm.checkAction(FarmingAction.HARVEST, FarmingTool.AXE)) {
      BlockPos remove = walker.flowersToHarvest.remove(0);
      doHarvest(farm, world, farm.getBlockState(remove), remove, fortune, result);
    }
    while (!walker.stemsToHarvest.isEmpty() && farm.checkAction(FarmingAction.HARVEST, FarmingTool.AXE)) {
      // Note: While stems can be broken by any tool, I decided to use the axe. This way a chorus farm can be used without hoe and the various auto-smelt
      // enchantments on axes can be used for the fruits.
      BlockPos remove = walker.stemsToHarvest.remove(walker.stemsToHarvest.size() - 1);
      doHarvest(farm, world, farm.getBlockState(remove), remove, fortune, result);
    }

    return result.getDrops().isEmpty() && result.getHarvestedBlocks().isEmpty() ? null : result;
  }

  private void doHarvest(@Nonnull final IFarmer farm, @Nonnull final World world, @Nonnull IBlockState blockState, @Nonnull final BlockPos pos, int fortune,
      @Nonnull final HarvestResult result) {
    FakePlayer joe = farm.startUsingItem(FarmingTool.AXE);
    NNList<ItemStack> drops = new NNList<>();
    blockState.getBlock().getDrops(drops, world, pos, blockState, fortune);
    final float chance = ForgeEventFactory.fireBlockHarvesting(drops, world, pos, blockState, fortune, 1f, false, joe);

    // flowers drop here by spawning their drops into the world (joe's world captures those)
    blockState.getBlock().harvestBlock(joe.world, joe, pos, blockState, null, joe.getHeldItemMainhand());

    NNList.wrap(drops).apply(new Callback<ItemStack>() {
      @Override
      public void apply(@Nonnull ItemStack drop) {
        if (farm.getWorld().rand.nextFloat() <= chance) {
          result.getDrops().add(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop.copy()));
        }
      }
    });

    NNList.wrap(farm.endUsingItem(FarmingTool.AXE)).apply(new Callback<ItemStack>() {
      @Override
      public void apply(@Nonnull ItemStack drop) {
        result.getDrops().add(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop.copy()));
      }
    });

    farm.registerAction(FarmingAction.HARVEST, FarmingTool.AXE, blockState, pos);
    world.setBlockToAir(pos);
    result.getHarvestedBlocks().add(pos);
  }

  private static final @Nonnull NNList<EnumFacing> GROW_DIRECTIONS = new NNList<>(EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST,
      EnumFacing.EAST);

  private static class ChorusWalker {

    // Yes, there are some edge cases remaining where a chorus plant is broken improperly. But this is good enough and can deal with all properly grown plants.

    final @Nonnull NNList<BlockPos> flowersToHarvest = new NNList<BlockPos>();
    final @Nonnull NNList<BlockPos> stemsToHarvest = new NNList<BlockPos>();
    final @Nonnull IFarmer farm;

    ChorusWalker(@Nonnull IFarmer farm, @Nonnull BlockPos pos) {
      this.farm = farm;
      collect(pos, EnumFacing.DOWN);
    }

    boolean collect(@Nonnull BlockPos pos, @Nonnull EnumFacing from) {
      IBlockState state = farm.getWorld().getBlockState(pos);
      if (state.getBlock() == Blocks.CHORUS_PLANT) {
        boolean isNeeded = false;
        NNIterator<EnumFacing> iterator = GROW_DIRECTIONS.iterator();
        while (iterator.hasNext()) {
          EnumFacing side = iterator.next();
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
